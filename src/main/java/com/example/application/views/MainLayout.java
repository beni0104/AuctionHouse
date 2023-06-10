package com.example.application.views;


import com.example.application.components.appnav.AppNav;
import com.example.application.components.appnav.AppNavItem;
import com.example.application.data.entity.UserRole;
import com.example.application.data.services.UserService;
import com.example.application.security.SecurityService;
import com.example.application.views.addauction.AddAuctionView;
import com.example.application.views.adminauctions.AdminAuctionView;
import com.example.application.views.auctions.AuctionsView;
import com.example.application.views.favoriteauctions.FavoriteAuctions;
import com.example.application.views.wonauctions.WonAuctionsView;
import com.example.application.views.yourauctions.YourAuctionsView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
    private final SecurityService securityService;
    private final UserService userService;
    private H2 viewTitle;
    private Button logout;

    public MainLayout(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        toggle.setClassName("nav");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);


        logout = new Button("Log out ");
        logout.addClickListener(event ->{
            securityService.logout();
        });


        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("AuctionHouse");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);


        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();


        AppNavItem a1 = new AppNavItem("Auctions", AuctionsView.class, LineAwesomeIcon.TH_LIST_SOLID.create());
        a1.setClassName("nav-item");

        AppNavItem a2 = new AppNavItem("Your auctions", YourAuctionsView.class, LineAwesomeIcon.TH_LIST_SOLID.create());
        a2.setClassName("nav-item");

        AppNavItem a3 = new AppNavItem("Favorite auctions", FavoriteAuctions.class, LineAwesomeIcon.TH_LIST_SOLID.create());
        a3.setClassName("nav-item");

        AppNavItem a4 = new AppNavItem("Add Auction", AddAuctionView.class, LineAwesomeIcon.PLUS_CIRCLE_SOLID.create());
        a4.setClassName("nav-item");

        AppNavItem a5 = new AppNavItem("Won Auctions", WonAuctionsView.class, LineAwesomeIcon.TH_LIST_SOLID.create());
        a5.setClassName("nav-item");

        nav.addItem(a1);
        nav.addItem(a2);
        nav.addItem(a3);
        nav.addItem(a5);
        nav.addItem(a4);
/*
        if(securityService.getAuthenticatedUser() != null)
            if(userService.findByUsername(securityService.getAuthenticatedUser().getUsername()).get().getUserRole() == UserRole.ADMIN) {
                AppNavItem a6 = new AppNavItem("Administrator", AdminAuctionView.class, LineAwesomeIcon.TH_LIST_SOLID.create());
                a6.setClassName("nav-item");
                nav.addItem(a6);
            }
*/
        AppNavItem a6 = new AppNavItem("Administrator", AdminAuctionView.class, LineAwesomeIcon.TH_LIST_SOLID.create());
        a6.setClassName("nav-item");
        nav.addItem(a6);


        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();


        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
