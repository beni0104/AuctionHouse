package com.example.application.data;

import com.example.application.data.entity.Auction;
import com.example.application.data.services.AuctionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AdminAuctionsViewCard extends ListItem {

    private final AuctionService auctionService;

    private Span status;
    private Button accept, reject;

    private void setStatus(String value) {
        status.setText("Status: " + value);
        status.setVisible(true);
    };

    public AdminAuctionsViewCard(Auction auction, AuctionService auctionService) {
        this.auctionService = auctionService;
        addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
                BorderRadius.LARGE, "item");

        Div div = new Div();
        div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
                Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);
        div.setHeight("160px");


        InputStream imageStream = new ByteArrayInputStream(auction.getImage());
        StreamResource imageResource = new StreamResource("StreamedImage",
                () -> imageStream);
        Image image = new Image(imageResource, "Image");
        image.setWidth("100%");
        //image.setSrc(url);
        image.setAlt("No image");



        div.add(image);

        Span header = new Span();
        header.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD);
        header.setText(auction.getTitle());

        Span subtitle = new Span();
        subtitle.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
        subtitle.setText("Card subtitle");

        Paragraph description = new Paragraph(auction.getDescription());
        description.addClassName(Margin.Vertical.MEDIUM);

        Button view = new Button("View!");
        view.setClassName("item-button");
        view.addClickListener(e -> {
            UI.getCurrent().navigate("auctionItem/" + auction.getId());
        });

        accept = new Button("Accept");
        accept.getStyle().set("background-color", "green");
        accept.addClickListener(e -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            status = new Span();
            status.setVisible(false);

            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Are you sure you want accept the selected item?");
            dialog.setText("You cannot undo the action!");


            dialog.setRejectable(true);
            dialog.setRejectText("Discard");
            dialog.addRejectListener(event -> {
                setStatus("Discarded");
                Notification notification = Notification.show("Discarded");
            });

            dialog.setConfirmText("Accept");
            dialog.addConfirmListener(event -> {
                auction.setAccepted("accepted");
                auctionService.update(auction);
                setStatus("Accepted");
                Notification notification = Notification.show("Item accepted");
            });


            dialog.open();

            add(layout);


        });
        reject = new Button("Reject");
        reject.getStyle().set("background-color", "red");
        reject.addClickListener(e -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            status = new Span();
            status.setVisible(false);

            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Are you sure you want reject the selected item?");
            dialog.setText("You cannot undo the action!");


            dialog.setRejectable(true);
            dialog.setRejectText("Discard");
            dialog.addRejectListener(event -> {
                setStatus("Discarded");
                Notification notification = Notification
                        .show("Discarded");
            });

            dialog.setConfirmText("Reject");
            dialog.addConfirmListener(event -> {
                auction.setAccepted("rejected");
                auctionService.update(auction);
                setStatus("Rejected");
                Notification notification = Notification
                        .show("Item rejected");
            });


            dialog.open();

            add(layout);


        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(view, accept, reject);
        add(horizontalLayout);

        add(div, header, subtitle, description, horizontalLayout);

    }
}
