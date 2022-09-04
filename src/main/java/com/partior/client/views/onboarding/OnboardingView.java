package com.partior.client.views.onboarding;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.AccountOwnerResponseDto;
import com.partior.client.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PageTitle("Account Onboarding")
@Route(value = "onboarding/:onboardingID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
@Tag("onboarding-view")
@Uses(Icon.class)
public class OnboardingView extends VerticalLayout {

    @Autowired
    final CantonDataService cantonDataService;

    Grid<AccountOwnerResponseDto> grid = new Grid<>(AccountOwnerResponseDto.class);
    TextField filterText = new TextField();
    OnboardingForm form;

    public OnboardingView(CantonDataService cantonDataService) {
        this.cantonDataService = cantonDataService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add Account Owner");
        addContactButton.addClickListener(click -> addAccountOwner());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }
    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new OnboardingForm(Collections.emptyList());
        form.setWidth("25em");

      //  form.addListener(OnboardingForm.SaveEvent.class, this::saveContact);
      //  form.addListener(OnboardingForm.DeleteEvent.class, this::deleteContact);
        form.addListener(OnboardingForm.CloseEvent.class, e -> closeEditor());

    }


    private void configureGrid() {
        grid.addClassNames("onboarding-grid");
        grid.setSizeFull();
        grid.setColumns("bank", "bic", "sponsorParty", "centralBankParty","currency","rtgsAccountId", "local");
      //  grid.addColumn(contact -> contact.getStatus().getName()).setHeader("Status");
       // grid.addColumn(contact -> contact.getCompany().getName()).setHeader("Company");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event ->
                editAccountOwner(event.getValue()));
    }

    public void editAccountOwner(AccountOwnerResponseDto accountOwner) {
        if (accountOwner == null) {
            closeEditor();
        } else {
            form.setAccountOwner(accountOwner);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setAccountOwner(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addAccountOwner() {
        grid.asSingleSelect().clear();
        editAccountOwner(new AccountOwnerResponseDto());
    }


    private void updateList() {
        List<AccountOwnerResponseDto> accountOwnerList = new ArrayList<>();
        try {
            accountOwnerList = cantonDataService.listAccountOwners(cantonDataService.getCdbcOperator() );
        } catch (Exception e) {
            e.printStackTrace();
        }
        grid.setItems(accountOwnerList);

    }

    private void saveContact(OnboardingForm.SaveEvent event) {
    //    service.saveContact(event.getContact());
        updateList();
        closeEditor();
    }

    private void deleteContact(OnboardingForm.DeleteEvent event) {
     ///   service.deleteContact(event.getContact());
        updateList();
        closeEditor();
    }
}
