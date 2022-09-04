package com.partior.client.views.onboarding;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.AccountOwnerResponseDto;
import com.partior.client.dto.CentralBankDto;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class CentralBankForm extends VerticalLayout {

    private TextField centralBank = new TextField("Central Bank Name");

    private ComboBox<String> currency = new ComboBox<>("Currency");


    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<CentralBankDto> binder = new Binder<>(CentralBankDto.class);

    private CantonDataService cantonDataService;

    public CentralBankForm(CantonDataService cantonDataService) {
        this.cantonDataService = cantonDataService;
        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());
    }

    private Component createFormLayout() {

        FormLayout formLayout = new FormLayout();
        formLayout.add(centralBank, 2);
        currency.setItems("SGD", "USD", "EUR","IDR");

        formLayout.add(currency,2);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

    private void clearForm() {
        this.binder.setBean(new CentralBankDto());
    }

    private Component createTitle() {
        return new H3("Onboard Central Bank Domain");
    }
}
