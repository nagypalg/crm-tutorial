package com.vaadin.tutorial.crm.ui.views.contact;

import com.vaadin.flow.component.ComponentEvent;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Route(value = "", layout = MainLayout.class)
@PageTitle("Contacts | Vaadin CRM")
@Slf4j
public class ContactList extends VerticalLayout {

    ContactEditor editor;
    ContactViewer viewer;
    Grid<Contact> grid = new Grid<>(Contact.class);
    TextField filterText = new TextField();

    ContactService contactService;

    public ContactList(ContactService contactService,
                       CompanyService companyService) {
        this.contactService = contactService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();


        editor = new ContactEditor(companyService.findAll());
        editor.addListener(ContactEditor.SaveEvent.class, e -> {
            log.info("Editor save triggered");
            saveContact(e);
            viewer.ignoreFirstKeyEvent();
            grid.asSingleSelect().clear();
            grid.select(e.getContact());
//            fireEvent(new ViewItemEvent(this, e.getContact()));
        });
        editor.addListener(ContactEditor.DeleteEvent.class, this::deleteContact);
        editor.addListener(ContactEditor.CloseEvent.class, e -> {
            log.info("Editor close triggered");
            closeEditor();
            viewer.ignoreFirstKeyEvent();
//            fireEvent(new ViewItemEvent(this, e.getContact()));
            grid.asSingleSelect().clear();
            grid.select(e.getContact());
        });

        viewer = new ContactViewer();
        viewer.addListener(ContactViewer.EditEvent.class, e -> {
            log.info("Viewer edit triggered");
            closeViewer();
            fireEvent(new EditItemEvent(this, e.getContact()));
//            editContact(e.getContact());
        });
        viewer.addListener(ContactViewer.CloseEvent.class, e -> {
            log.info("Viewer close triggered");
            closeViewer();
            grid.asSingleSelect().clear();
        });

        addListener(ViewItemEvent.class, e -> {
            log.info("View item triggered");
            viewContact(e.getContact());
        });

        addListener(EditItemEvent.class, e -> {
            log.info("Edit item triggered");
            editContact(e.getContact());
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
        closeEditor();
        updateList();
    }

    private void saveContact(ContactEditor.SaveEvent evt) {
        contactService.save(evt.getContact());
        closeEditor();
        updateList();
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
        grid.addClassName("item-grid");
        grid.setSizeFull();
        grid.removeColumnByKey("company");
        grid.setColumns("firstName", "lastName", "email", "status");
        grid.addColumn(contact -> {
            Company company = contact.getCompany();
            return company == null ? "-" : company.getName();
        }).setHeader("Company");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(evt -> fireEvent(new ViewItemEvent(this, evt.getValue())));
    }

    private void viewContact(Contact contact) {
        log.info("View contact {}", contact);
        if (contact == null) {
            closeViewer();
        } else {
            viewer.setContact(contact);
            viewer.setVisible(true);
            addClassName("viewing");
        }
    }

    private void editContact(Contact contact) {
        log.info("Edit contact {}", contact);
        if (contact == null) {
            closeEditor();
        } else {
            editor.setContact(contact);
            editor.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        log.info("Closing editor");
        editor.setContact(null);
        editor.setVisible(false);
        removeClassName("editing");
    }

    private void closeViewer() {
        log.info("Closing viewer");
        viewer.setContact(null);
        viewer.setVisible(false);
        removeClassName("viewing");

    }

    private void updateList() {
        grid.setItems(contactService.findAll(filterText.getValue()));
    }


    // Events
    public static abstract class ContactListEvent extends ComponentEvent<ContactList> {
        private Contact contact;

        protected ContactListEvent(ContactList source, Contact contact) {
            super(source, false);
            this.contact = contact;
        }

        public Contact getContact() {
            return contact;
        }
    }

    public static class ViewItemEvent extends ContactListEvent {
        private boolean ignoreFirst;
        ViewItemEvent(ContactList source, Contact contact) {
            super(source, contact);
        }
        ViewItemEvent(ContactList source, Contact contact, boolean ignoreFirst) {
            super(source, contact);
            this.ignoreFirst = ignoreFirst;
        }

        public boolean isIgnoreFirst() {
            return ignoreFirst;
        }
    }

    public static class EditItemEvent extends ContactListEvent {
        EditItemEvent(ContactList source, Contact contact) {
            super(source, contact);
        }
    }

}
