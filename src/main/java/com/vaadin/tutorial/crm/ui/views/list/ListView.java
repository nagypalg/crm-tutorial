package com.vaadin.tutorial.crm.ui.views.list;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import com.vaadin.tutorial.crm.backend.service.CompanyService;
import com.vaadin.tutorial.crm.backend.service.ContactService;
import com.vaadin.tutorial.crm.ui.MainLayout;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Route(value = "", layout = MainLayout.class)
@PageTitle("Contacts | Vaadin CRM")
public class ListView extends VerticalLayout {

    ContactEditor editor;
    ContactViewer viewer;
    Grid<Contact> grid = new Grid<>(Contact.class);
    TextField filterText = new TextField();

    ContactService contactService;

    public ListView(ContactService contactService,
                    CompanyService companyService) {
        this.contactService = contactService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();


        editor = new ContactEditor(companyService.findAll());
        editor.addListener(ContactEditor.SaveEvent.class, e -> {
            saveContact(e);
            grid.select(e.getContact());
            viewContact(e.getContact());
        });
        editor.addListener(ContactEditor.DeleteEvent.class, this::deleteContact);
        editor.addListener(ContactEditor.CloseEvent.class, e -> {
            closeEditor();
            viewContact(e.getContact());
        });

        viewer = new ContactViewer();
        viewer.addListener(ContactViewer.EditEvent.class, e -> {
            editContact(e.getContact());
            closeViewer();
        });
        viewer.addListener(ContactViewer.CloseEvent.class, e -> {
            closeViewer();
            grid.asSingleSelect().clear();
        });

        Div content = new Div(grid, viewer, editor);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();
        closeViewer();
    }

    private void deleteContact(ContactEditor.DeleteEvent evt) {
        contactService.delete(evt.getContact());
        updateList();
        closeEditor();
    }

    private void saveContact(ContactEditor.SaveEvent evt) {
        contactService.save(evt.getContact());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getToolBar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add contact", click -> addContact());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Contact());
    }

    private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();
        grid.removeColumnByKey("company");
        grid.setColumns("firstName", "lastName", "email", "status");
        grid.addColumn(contact -> {
            Company company = contact.getCompany();
            return company == null ? "-" : company.getName();
        }).setHeader("Company");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(evt -> viewContact(evt.getValue()));
    }

    private void viewContact(Contact contact) {
        if (contact == null) {
            closeViewer();
        } else {
            viewer.setContact(contact);
            viewer.setVisible(true);
            addClassName("viewing");
        }
    }

    private void editContact(Contact contact) {
        if (contact == null) {
            closeEditor();
        } else {
            editor.setContact(contact);
            editor.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        editor.setContact(null);
        editor.setVisible(false);
        removeClassName("editing");
    }

    private void closeViewer() {
        viewer.setContact(null);
        viewer.setVisible(false);
        removeClassName("viewing");

    }

    private void updateList() {
        grid.setItems(contactService.findAll(filterText.getValue()));
    }

}
