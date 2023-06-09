package com.example.application.views.edititemview;

import com.example.application.data.entity.Auction;
import com.example.application.data.services.AuctionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@PageTitle("Edit auction item")
@Route(value = "edit")
@PermitAll
public class EditItemView  extends VerticalLayout implements HasUrlParameter<String> {

    private final AuctionService auctionService;

    int charLimit = 140;
    private Span status;

    private DatePicker dateFrom, dateTo;
    private TimePicker fromTP, toTP;
    private NumberField euroField;
    private Button back, submitButton;
    private TextField title;
    private TextArea textArea, details;
    private Auction auction;


    private void setStatus(String value) {
        status.setText("Status: " + value);
        status.setVisible(true);
    };

    @Override
    public void setParameter(BeforeEvent beforeEvent, String parameter) {

        Optional<Auction> a = auctionService.findById(Long.parseLong(parameter));
        a.ifPresent(value -> auction = value);

        details.setValue("Title: " + auction.getTitle() + "\n" +
                "Description: " + auction.getDescription() + "\n" +
                "Starting date: " + auction.getFromLD() + " Starting hour: " + auction.getFromLT() + "\n" +
                "Ending date: " + auction.getToLD() + "  Ending hour: "+ auction.getToLT() + "\n" +
                "Minimum accepted price: " + auction.getStartingPrice() + "\n" +
                "Last Bid: " + auction.getCurrentPrice());


        title.setValue(auction.getTitle());
        textArea.setValue(auction.getDescription());
        dateFrom.setValue(auction.getFromLD());
        dateTo.setValue(auction.getToLD());
        fromTP.setValue(auction.getFromLT());
        toTP.setValue(auction.getToLT());
        euroField.setValue(auction.getStartingPrice());

        LocalTime nowTime = LocalTime.now();

        dateFrom.addValueChangeListener(event -> {
            LocalDate dateFromLD = dateFrom.getValue();
            if(dateFromLD != null)
                dateTo.setMin(dateFromLD);
        });

        dateTo.addValueChangeListener(event -> {
            LocalDate dateToValue = dateTo.getValue();
            LocalDate dateFromValue = dateFrom.getValue();
            if(dateToValue != null) {
                if (dateFromValue.isAfter(dateToValue)) {
                    dateTo.setValue(dateFromValue);
                }
                if (dateFromValue.isBefore(dateToValue))
                    toTP.setMin(LocalTime.of(0, 0));
                else if (dateFromValue.isEqual(dateToValue)) {
                    fromTP.setMin(nowTime);
                    if (fromTP.getValue() != null)
                        toTP.setMin(fromTP.getValue().plusHours(1));
                }
            }
        });

        fromTP.addValueChangeListener(event -> {
            LocalTime fromTpValue = fromTP.getValue();
            LocalTime toTpValue = toTP.getValue();
            if(fromTpValue != null && dateFrom.getValue() != null && dateTo.getValue() != null)
                if(dateFrom.getValue().isEqual(dateTo.getValue()))
                    toTP.setMin(fromTpValue.plusHours(1));
            if(toTpValue != null)
                if(fromTpValue.isAfter(toTpValue))
                    toTP.setValue(fromTpValue.plusHours(1));
        });


        toTP.addValueChangeListener(event -> {
            LocalTime fromTpValue = fromTP.getValue();
            LocalTime toTpValue = toTP.getValue();
            if(fromTpValue != null && toTpValue != null && dateFrom.getValue() != null && dateTo.getValue() != null)
                if(dateFrom.getValue().isEqual(dateTo.getValue()))
                    if(fromTpValue.plusHours(1).isAfter(toTpValue))
                        toTP.setValue(fromTpValue.plusHours(1));

        });

        submitButton.addClickListener(e -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            status = new Span();
            status.setVisible(false);

            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Are you sure you want to save the added information?");
            dialog.setText("Please check if all the introduced data is correct.");


            dialog.setRejectable(true);
            dialog.setRejectText("Discard");
            dialog.addRejectListener(event -> {
                setStatus("Discarded");
                Notification notification = Notification.show("Discarded");
            });

            dialog.setConfirmText("Save");
            dialog.addConfirmListener(event -> {

                auction.setTitle(title.getValue());
                auction.setDescription(textArea.getValue());
                auction.setStartingPrice(euroField.getValue());
                auction.setFromLD(dateFrom.getValue());
                auction.setToLD(dateTo.getValue());
                auction.setFromLT(fromTP.getValue());
                auction.setToLT(toTP.getValue());

                auctionService.update(auction);
                setStatus("Submit");
                Notification notification = Notification.show("Data saved!");
            });


            dialog.open();

            add(layout);

        });
    }

    public EditItemView(AuctionService auctionService){
        this.auctionService = auctionService;
        details = new TextArea();
        details.setReadOnly(true);
        details.setLabel("Current item details");
        details.setWidthFull();
        add(details);

        title = new TextField("Title");


        textArea = new TextArea();
        textArea.setLabel("Description");
        textArea.setMaxLength(charLimit);
        textArea.setValueChangeMode(ValueChangeMode.EAGER);
        textArea.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(e.getValue().length() + "/" + charLimit);
        });
        textArea.setValue("");


        dateFrom = new DatePicker("Date from");

        fromTP = new TimePicker("From");

        dateTo = new DatePicker("Date to");

        toTP = new TimePicker("To");

        euroField = new NumberField();
        euroField.setLabel("Minimum accepted price:");
        euroField.setValue(10.0);
        Div euroSuffix = new Div();
        euroSuffix.setText("€");
        euroField.setSuffixComponent(euroSuffix);

        submitButton = new Button("Edit");


        back = new Button("Back");
        back.addClickListener(e -> {
            UI.getCurrent().navigate("2");
        });
        submitButton.getStyle().set("background-color", "white");
        submitButton.getStyle().set("color", "black");

        HorizontalLayout horizontalLayout = new HorizontalLayout(back, submitButton);
        add(horizontalLayout);

        FormLayout formLayout = new FormLayout();
        formLayout.add(title, textArea, dateFrom, fromTP, dateTo, toTP, euroField, horizontalLayout);
        // tag::snippet[]
        formLayout.setColspan(title, 3);
        formLayout.setColspan(textArea, 3);
        formLayout.setColspan(fromTP, 2);
        formLayout.setColspan(toTP, 2);
        formLayout.setColspan(horizontalLayout, 3);


        // end::snippet[]
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 3));
        add(formLayout);
    }


}

