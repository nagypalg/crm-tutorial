package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.tutorial.crm.ui.views.company.CompanyList;
import com.vaadin.tutorial.crm.ui.views.contact.ContactList;

@PWA(
        name = "Vaadin CRM",
        shortName = "CRM",
        offlineResources = {
                "./styles/offline.css",
                "./images/offline.png"
        },
        enableInstallPrompt = false
)
@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Vaadin CRM");
        logo.addClassName("logo");

        Anchor logout = new Anchor("/logout", "Log out");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logout);
        header.addClassName("header");
        header.setWidth("100%");
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink contactsLink = new RouterLink("Contacts", ContactList.class);
        contactsLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink companiesLink = new RouterLink("Companies", CompanyList.class);
        companiesLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(
                contactsLink, companiesLink
        ));
    }


}
