package com.vaadin.tutorial.crm.ui.views.company;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;

public class CompanyViewer extends FormLayout {

    TextField name = new TextField("Company name");

    Button edit = new Button("Edit");
    Button close = new Button("Close");

    Binder<Company> binder = new Binder<>(Company.class);
    private Company company;

    public CompanyViewer() {
        addClassName("item-form");
        binder.bindInstanceFields(this);

        name.setReadOnly(true);

        add(
                name,
                createButtonsLayout()
        );
    }

    public void setCompany(Company company) {
        this.company = company;
        binder.readBean(company);
    }

    private Component createButtonsLayout() {
        edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        edit.addClickListener(click -> fireEvent(new EditEvent(this, company)));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(edit, close);
    }

    // Events
    public static abstract class CompanyViewerEvent extends ComponentEvent<CompanyViewer> {
        private Company company;

        protected CompanyViewerEvent(CompanyViewer source, Company company) {
            super(source, false);
            this.company = company;
        }

        public Company getCompany() {
            return company;
        }
    }

    public static class EditEvent extends CompanyViewerEvent {
        EditEvent(CompanyViewer source, Company company) {
            super(source, company);
        }
    }

    public static class CloseEvent extends CompanyViewerEvent {
        CloseEvent(CompanyViewer source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
