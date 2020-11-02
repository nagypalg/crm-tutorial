package com.vaadin.tutorial.crm.ui.views.contact;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ContactListTest {

    @Autowired
    private ContactList listView;

    @Test
    public void formShownWhenContactSelected() {
        Grid<Contact> grid = listView.grid;
        Contact firstContact = getFirstItem(grid);

        ContactViewer form = listView.viewer;

        assertThat(form.isVisible()).isFalse();
        grid.asSingleSelect().setValue(firstContact);
        assertThat(form.isVisible()).isTrue();
        assertThat(form.firstName.getValue()).isEqualTo(firstContact.getFirstName());
    }

    private Contact getFirstItem(Grid<Contact> grid) {
        return ((ListDataProvider<Contact>) grid.getDataProvider()).getItems().iterator().next();
    }

}
