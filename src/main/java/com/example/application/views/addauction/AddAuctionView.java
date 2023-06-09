package com.example.application.views.addauction;

import com.example.application.data.repository.AuctionRepository;
import com.example.application.security.SecurityService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Add Auction")
@Route(value = "addauction", layout = MainLayout.class)
@PermitAll
public class AddAuctionView extends VerticalLayout {

    private final AuctionRepository auctionRepository;
    private final SecurityService securityService;

    public AddAuctionView(AuctionRepository auctionRepository, SecurityService securityService) {
        this.auctionRepository = auctionRepository;
        this.securityService = securityService;

        setSpacing(true);
        FormLayoutColspan registrationForm = new FormLayoutColspan(this.auctionRepository, this.securityService);
        setHorizontalComponentAlignment(Alignment.CENTER, registrationForm);

        add(registrationForm);

    }

}
