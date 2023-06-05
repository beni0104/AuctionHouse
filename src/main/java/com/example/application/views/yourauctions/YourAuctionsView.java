package com.example.application.views.yourauctions;

import com.example.application.data.YourAuctionsViewCard;
import com.example.application.data.entity.Auction;
import com.example.application.data.services.AuctionService;
import com.example.application.data.services.UserService;
import com.example.application.security.SecurityService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@PageTitle("Your auctions")
@Route(value = "2", layout = MainLayout.class)
@PermitAll
public class YourAuctionsView extends Main implements HasComponents, HasStyle {
    private final AuctionService auctionService;
    private final SecurityService securityService;
    private final UserService userService;

    private OrderedList imageContainer;
    private List<Auction> auctions;
    private Select<String> sortBy;
    private Button button;


    public YourAuctionsView(AuctionService auctionService, SecurityService securityService, UserService userService) {
        this.auctionService = auctionService;
        this.securityService = securityService;
        this.userService = userService;
        constructUI();

        auctions = auctionService.findByUsername(securityService.getAuthenticatedUser().getUsername());

        for(Auction a : auctions)
        {
            imageContainer.add(new YourAuctionsViewCard(a, auctionService, "edit", this.userService));
        }

        sortBy.addValueChangeListener(event -> {
            if(event.getValue().equals("Newest first")) {
                auctions = auctionService.listSortedByNewest(auctions);
                imageContainer.removeAll();
                for(Auction a : auctions)
                {
                    if(a.getAuctionerUsername().equals(securityService.getAuthenticatedUser().getUsername()))
                        imageContainer.add(new YourAuctionsViewCard(a, auctionService, "edit", this.userService));
                }
            }
            else if(event.getValue().equals("Oldest first")) {
                imageContainer.removeAll();
                auctions = auctionService.listSortedByOldest(auctions);
                for (Auction a : auctions) {
                    if(a.getAuctionerUsername().equals(securityService.getAuthenticatedUser().getUsername()))
                        imageContainer.add(new YourAuctionsViewCard(a, auctionService, "edit", this.userService));
                }
            }
        });

        button.addClickListener(event ->{
            if(Objects.equals(button.getText(), "Closed auctions")) {
                button.setText("Your auctions");
                imageContainer.removeAll();
                for(Auction a : auctions)
                {
                    if(!Objects.equals(a.getAccepted(), "sold") &&
                            a.getToLD().isBefore(LocalDate.now()) ||
                            (a.getToLD().isEqual(LocalDate.now()) && a.getToLT().isBefore(LocalTime.now()))) {
                        imageContainer.add(new YourAuctionsViewCard(a, auctionService, "confirm", this.userService));
                    }
                }
            } else if (Objects.equals(button.getText(), "Your auctions")) {
                button.setText("Closed auctions");
                imageContainer.removeAll();
                for(Auction a : auctions)
                {
                    imageContainer.add(new YourAuctionsViewCard(a, auctionService, "edit", this.userService));
                }
            }
        });

    }

    private void constructUI() {
        addClassNames("auctions-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Your Auction Items");
        header.addClassNames("header");
        //header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        Paragraph description = new Paragraph("Below you can find the items you added for auction.");
        description.addClassName("description");
        //description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
        headerContainer.add(header, description);

        sortBy = new Select<>();
        sortBy.setLabel("Sort by");
        sortBy.setClassName("my-select");
        sortBy.setItems("Newest first", "Oldest first");
        sortBy.setValue("Newest first");

        button = new Button("Closed auctions");

        imageContainer = new OrderedList();
        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        container.add(headerContainer,button , sortBy);
        add(container, imageContainer);

    }
}
