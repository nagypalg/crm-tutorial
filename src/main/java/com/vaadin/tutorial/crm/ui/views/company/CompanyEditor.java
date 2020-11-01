package com.vaadin.tutorial.crm.ui.views.company;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;

import java.util.List;

public class CompanyEditor extends FormLayout {

    TextField name = new TextField("Company name");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Company> binder = new BeanValidationBinder<>(Company.class);
    private Company company;

    public CompanyEditor() {
        addClassName("item-form");

        binder.bindInstanceFields(this);

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
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, company)));
        close.addClickListener(click -> fireEvent(new CloseEvent(this, company)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {

        try {
            binder.writeBean(company);
            fireEvent(new SaveEvent(this, company));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class ContactFormEvent extends ComponentEvent<CompanyEditor> {
        private Company company;

        protected ContactFormEvent(CompanyEditor source, Company company) {
            super(source, false);
            this.company = company;
        }

        public Company getCompany() {
            return company;
        }
    }

    public static class SaveEvent extends ContactFormEvent {
        SaveEvent(CompanyEditor source, Company company) {
            super(source, company);
        }
    }

    public static class DeleteEvent extends ContactFormEvent {
        DeleteEvent(CompanyEditor source, Company company) {
            super(source, company);
        }

    }

    public static class CloseEvent extends ContactFormEvent {
        CloseEvent(CompanyEditor source, Company company) {
            super(source, company);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
