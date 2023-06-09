package com.example.application.views.favoriteauctions;

import com.example.application.data.AuctionsViewCard;
import com.example.application.data.entity.Auction;
import com.example.application.data.services.AuctionService;
import com.example.application.data.services.FavouriteService;
import com.example.application.security.SecurityService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
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

import java.util.List;

@PageTitle("Favorite auctions")
@Route(value = "favorite", layout = MainLayout.class)
@PermitAll
public class FavoriteAuctions extends Main implements HasComponents, HasStyle {

    private final FavouriteService favouriteService;
    private final SecurityService securityService;
    private final AuctionService auctionService;

    private List<Auction> auctions;

    private OrderedList imageContainer;
    private Select<String> sortBy;

    public FavoriteAuctions(FavouriteService favouriteService, SecurityService securityService, AuctionService auctionService) {
        this.favouriteService = favouriteService;
        this.securityService = securityService;
        this.auctionService = auctionService;


        constructUI();

        auctions = favouriteService.findAll(securityService.getAuthenticatedUser().getUsername());

        for(Auction a : auctions){
            imageContainer.add(new AuctionsViewCard(a));
        }

        sortBy.addValueChangeListener(event -> {
            if(event.getValue().equals("Newest first")) {
                auctions = this.auctionService.listSortedByNewest(auctions);
                imageContainer.removeAll();
                for(Auction a : auctions)
                {
                    imageContainer.add(new AuctionsViewCard(a));
                }
            }
            else if(event.getValue().equals("Oldest first")) {
                imageContainer.removeAll();
                auctions = this.auctionService.listSortedByOldest(auctions);
                for (Auction a : auctions) {
                    imageContainer.add(new AuctionsViewCard(a));
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
        H2 header = new H2("Favorite Auction Items");
        header.addClassNames("header");
        //header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        Paragraph description = new Paragraph("Here you can find the items added by you as favorites.");
        //description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
        description.addClassName("description");
        headerContainer.add(header, description);

        sortBy = new Select<>();
        sortBy.setLabel("Sort by");
        sortBy.setClassName("my-select");
        sortBy.setItems("Newest first", "Oldest first");
        sortBy.setValue("Newest first");

        imageContainer = new OrderedList();
        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        container.add(headerContainer, sortBy);
        add(container, imageContainer);

    }
}
