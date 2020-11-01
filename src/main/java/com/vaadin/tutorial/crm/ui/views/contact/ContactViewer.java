package com.vaadin.tutorial.crm.ui.views.contact;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;

public class ContactViewer extends FormLayout {

    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    TextField email = new TextField("Email");
    TextField status = new TextField("Status");
    TextField company = new TextField("Company");

    Button edit = new Button("Edit");
    Button close = new Button("Close");

    Binder<Contact> binder = new Binder<>(Contact.class);
    private Contact contact;

    public ContactViewer() {
        addClassName("item-form");

        binder.forField(company).bind(c -> {
            Company company = c.getCompany();
            return company == null ? "-" : company.getName();
        }, null);

        binder.forField(status).bind(c -> c.getStatus().name(), null);

        binder.bindInstanceFields(this);

        firstName.setReadOnly(true);
        lastName.setReadOnly(true);
        email.setReadOnly(true);
        status.setReadOnly(true);

        add(
                firstName,
                lastName,
                email,
                status,
                company,
                createButtonsLayout()
        );
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        binder.readBean(contact);
    }

    private Component createButtonsLayout() {
        edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        edit.addClickListener(click -> fireEvent(new EditEvent(this, contact)));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));
        return new HorizontalLayout(edit, close);

    }

    // Events
    public static abstract class ContactViewerEvent extends ComponentEvent<ContactViewer> {
        private Contact contact;

        protected ContactViewerEvent(ContactViewer source, Contact contact) {
            super(source, false);
            this.contact = contact;
        }

        public Contact getContact() {
            return contact;
        }
    }

    public static class EditEvent extends ContactViewerEvent {
        EditEvent(ContactViewer source, Contact contact) {
            super(source, contact);
        }
    }

    public static class CloseEvent extends ContactViewerEvent {
        CloseEvent(ContactViewer source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }


}
