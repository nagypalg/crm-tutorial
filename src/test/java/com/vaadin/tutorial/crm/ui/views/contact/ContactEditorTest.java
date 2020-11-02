package com.vaadin.tutorial.crm.ui.views.contact;

import com.vaadin.flow.component.UI;
import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ContactEditorTest {
    private List<Company> companies;
    private Contact marcUsher;
    private Company company1;
    private Company company2;

    @Before
    public void setupData() throws InterruptedException {
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
        Assert.assertEquals("Marc", form.firstName.getValue());
        Assert.assertEquals("Usher", form.lastName.getValue());
        Assert.assertEquals("marc@usher.com", form.email.getValue());
        Assert.assertEquals(company2, form.company.getValue());
        Assert.assertEquals(Contact.Status.NotContacted, form.status.getValue());
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

            Assert.assertEquals("John", savedContact.getFirstName());
            Assert.assertEquals("Doe", savedContact.getLastName());
            Assert.assertEquals("john@doe.com", savedContact.getEmail());
            Assert.assertEquals(company1, savedContact.getCompany());
            Assert.assertEquals(Contact.Status.Customer, savedContact.getStatus());
        }

}
