package com.vaadin.tutorial.crm.ui.views.contact;

import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactEditorTest {
    private List<Company> companies;
    private Contact marcUsher;
    private Company company1;
    private Company company2;

    @BeforeEach
    public void setupData() {
        companies = new ArrayList<>();
        company1 = new Company("Vaadin Ltd");
        company2 = new Company("IT Mill");
        companies.add(company1);
        companies.add(company2);

        marcUsher = new Contact();
        marcUsher.setFirstName("Marc");
        marcUsher.setLastName("Usher");
        marcUsher.setEmail("marc@usher.com");
        marcUsher.setStatus(Contact.Status.NotContacted);
        marcUsher.setCompany(company2);
        marcUsher.setBirthDate(LocalDate.now());
    }

    @Test
    public void formFieldsPopulated() {
        ContactEditor form = new ContactEditor(companies);
        form.setContact(marcUsher);
        assertThat(form.firstName.getValue()).isEqualTo("Marc");
        assertThat(form.lastName.getValue()).isEqualTo("Usher");
    }

    @Test
    public void saveEventHasCorrectValues() {
        ContactEditor form = new ContactEditor(companies);
        Contact contact = new Contact();
        form.setContact(contact);

        form.firstName.setValue("John");
        form.lastName.setValue("Doe");
        form.company.setValue(company1);
        form.email.setValue("john@doe.com");
        form.status.setValue(Contact.Status.Customer);
        form.birthDate.setValue(LocalDate.now());

        AtomicReference<Contact> savedContactRef = new AtomicReference<>(null);
        form.addListener(ContactEditor.SaveEvent.class, e -> {
            savedContactRef.set(e.getContact());
        });
        form.save.click();
        Contact savedContact = savedContactRef.get();

        assertThat(savedContact.getFirstName()).isEqualTo("John");
        assertThat(savedContact.getLastName()).isEqualTo("Doe");
    }

}
