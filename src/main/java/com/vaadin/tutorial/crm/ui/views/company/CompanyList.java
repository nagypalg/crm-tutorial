package com.vaadin.tutorial.crm.ui.views.company;

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
import com.vaadin.tutorial.crm.backend.service.CompanyService;
import com.vaadin.tutorial.crm.ui.MainLayout;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Route(value = "companies", layout = MainLayout.class)
@PageTitle("Companies | Vaadin CRM")
public class CompanyList extends VerticalLayout {

    CompanyEditor editor;
    CompanyViewer viewer;
    Grid<Company> grid = new Grid<>(Company.class);
    TextField filterText = new TextField();

    CompanyService companyService;

    public CompanyList(
                       CompanyService companyService) {
        this.companyService = companyService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();


        editor = new CompanyEditor();
        editor.addListener(CompanyEditor.SaveEvent.class, e -> {
            saveItem(e);
            grid.select(e.getCompany());
            viewItem(e.getCompany());
        });
        editor.addListener(CompanyEditor.DeleteEvent.class, this::deleteItem);
        editor.addListener(CompanyEditor.CloseEvent.class, e -> {
            closeEditor();
            viewItem(e.getCompany());
        });

        viewer = new CompanyViewer();
        viewer.addListener(CompanyViewer.EditEvent.class, e -> {
            editItem(e.getCompany());
            closeViewer();
        });
        viewer.addListener(CompanyViewer.CloseEvent.class, e -> {
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

    private void deleteItem(CompanyEditor.DeleteEvent evt) {
        companyService.delete(evt.getCompany());
        updateList();
        closeEditor();
    }

    private void saveItem(CompanyEditor.SaveEvent evt) {
        companyService.save(evt.getCompany());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getToolBar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add company", click -> addItem());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addItem() {
        grid.asSingleSelect().clear();
        editItem(new Company());
    }

    private void configureGrid() {
        grid.addClassName("item-grid");
        grid.setSizeFull();
        grid.setColumns("name");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(evt -> viewItem(evt.getValue()));
    }

    private void viewItem(Company company) {
        if (company == null) {
            closeViewer();
        } else {
            viewer.setCompany(company);
            viewer.setVisible(true);
            addClassName("viewing");
        }
    }

    private void editItem(Company company) {
        if (company == null) {
            closeEditor();
        } else {
            editor.setCompany(company);
            editor.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        editor.setCompany(null);
        editor.setVisible(false);
        removeClassName("editing");
    }

    private void closeViewer() {
        viewer.setCompany(null);
        viewer.setVisible(false);
        removeClassName("viewing");

    }

    private void updateList() {
        grid.setItems(companyService.findAll(filterText.getValue()));
    }

}
