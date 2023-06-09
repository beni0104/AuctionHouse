package com.example.application.views.automaticbid;

import com.example.application.data.entity.Auction;
import com.example.application.data.entity.AutomaticBid;
import com.example.application.data.services.AuctionService;
import com.example.application.data.services.AutomaticBidService;
import com.example.application.security.SecurityService;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import java.text.DecimalFormat;
import java.util.List;

@PageTitle("Set automatic bid")
@Route(value = "automaticBid")
@PermitAll

public class AutomaticBidView extends Main implements HasComponents, HasStyle, HasUrlParameter<String> {
    private final AuctionService auctionService;
    private final AutomaticBidService automaticBidService;
    private final SecurityService securityService;

    public static final DecimalFormat decfor = new DecimalFormat("0.00");

    private OrderedList imageContainer;
    private Button back;
    private Button setBid;
    private Notification notification;
    private List<Auction> auctionsList;
    private Auction auction;
    private Paragraph description;
    private NumberField euroField;
    private TextArea currentPrice;


    public AutomaticBidView(AuctionService auctionService, AutomaticBidService automaticBidService, SecurityService securityService) {
        this.auctionService = auctionService;
        this.automaticBidService = automaticBidService;
        this.securityService = securityService;
        constructUI();
    }
    @Override
    public void setParameter(BeforeEvent beforeEvent, String parameter) {
        auctionsList = auctionService.findAll();
        for(Auction a : auctionsList)
            if(a.getId() == Long.parseLong(parameter)) {
                auction = new Auction(
                        a.getId(),
                        a.getTitle(),
                        a.getDescription(),
                        a.getStartingPrice(),
                        a.getCurrentPrice(),
                        a.getLastBidderUsername(),
                        a.getAuctionerUsername(),
                        a.getImage(),
                        a.getFromLD(),
                        a.getToLD(),
                        a.getFromLT(),
                        a.getToLT(),
                        a.getAccepted()
                );
            }

        description.setText("The selected item is: " + auction.getTitle());
        currentPrice.setValue("Current price: " + auction.getCurrentPrice());
        currentPrice.setReadOnly(true);
        euroField.setValue(Double.valueOf(decfor.format(auction.getCurrentPrice() * 1.2)));



        back.addClickListener(e -> {
            UI.getCurrent().navigate("auctionItem/" + parameter);
        });

        setBid.addClickListener(e ->{
            List<AutomaticBid> automaticBidList = automaticBidService.findByAuctionId(Long.parseLong(parameter));

            String autoBidUSername = "";
            double max1 = 0.0, max2 = 0.0;
            for(AutomaticBid a : automaticBidList){
                if(max1 < a.getMaximumPrice()){
                    max2 = max1;
                    max1 = a.getMaximumPrice();
                    autoBidUSername = a.getUsername();
                }else if(max2 < a.getMaximumPrice())
                    max2 = a.getMaximumPrice();
            }

            if(euroField.getValue() < max2){
                if(!autoBidUSername.equals(auction.getLastBidderUsername())) {
                    auction.setLastBidderUsername(autoBidUSername);
                    auction.setCurrentPrice(Math.min(max2 * 1.01, max1));
                    auctionService.update(auction);
                }
            }
            else {
                if(euroField.getValue() < max1){
                    auction.setLastBidderUsername(autoBidUSername);
                    auction.setCurrentPrice(Math.min(euroField.getValue() * 1.01, max1));
                    auctionService.update(auction);
                }
                else {
                    auction.setLastBidderUsername(securityService.getAuthenticatedUser().getUsername());
                    auction.setCurrentPrice(auction.getCurrentPrice()*1.01);
                    auctionService.update(auction);
                }
            }


            automaticBidService.save(new AutomaticBid(Long.parseLong(parameter), securityService.getAuthenticatedUser().getUsername(), euroField.getValue()));
            notification = Notification.show("Your automatic bid has been registered.");


        });

    }


    private void constructUI() {
        addClassNames("automaticBid-view");
        addClassNames(LumoUtility.MaxWidth.SCREEN_LARGE, LumoUtility.Margin.Horizontal.AUTO, LumoUtility.Padding.Bottom.LARGE, LumoUtility.Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Set automatic bid");
        header.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Margin.Top.XLARGE, LumoUtility.FontSize.XXXLARGE);

        description = new Paragraph();
        description.addClassNames(LumoUtility.Margin.Bottom.SMALL, LumoUtility.Margin.Top.NONE, LumoUtility.TextColor.SECONDARY);

        currentPrice = new TextArea();
        add(currentPrice);

        headerContainer.add(header, description, currentPrice);

        imageContainer = new OrderedList();
        imageContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.GRID, LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);

        container.add(headerContainer);
        add(container, imageContainer);

        euroField = new NumberField();
        euroField.setLabel("Maximum amount:");
        Div euroSuffix = new Div();
        euroSuffix.setText("€");
        euroField.setSuffixComponent(euroSuffix);
        add(euroField);

        back = new Button("Back");

        setBid = new Button("Set bid!");
        HorizontalLayout horizontalLayout = new HorizontalLayout(back, setBid);
        add(horizontalLayout);


    }


}
