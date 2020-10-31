package com.vaadin.tutorial.crm.ui.views.contact;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContactListTest {

    @Autowired
    private ContactList listView;

    @Test
        public void formShownWhenContactSelected() {
            Grid<Contact> grid = listView.grid;
            Contact firstContact = getFirstItem(grid);

            ContactEditor form = listView.editor;

            Assert.assertFalse(form.isVisible());
    		grid.asSingleSelect().setValue(firstContact);
            Assert.assertTrue(form.isVisible());
            Assert.assertEquals(firstContact.getFirstName(), form.firstName.getValue());
        }

    	private Contact getFirstItem(Grid<Contact> grid) {
    		return( (ListDataProvider<Contact>) grid.getDataProvider()).getItems().iterator().next();
    	}

}
