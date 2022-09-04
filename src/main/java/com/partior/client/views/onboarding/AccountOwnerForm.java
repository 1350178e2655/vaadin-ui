package com.partior.client.views.onboarding;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.AccountOwnerDto;
import com.partior.client.dto.AccountOwnerResponseDto;
import com.partior.client.dto.TransactionResponseDto;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;
import java.util.stream.Collectors;

public class AccountOwnerForm extends VerticalLayout {

    private ComboBox<String> centralBank = new ComboBox<>("Central Bank");
    private TextField bank = new TextField("Bank Name");
    private TextField bic = new TextField("BIC");


    private ComboBox<String> sponsorParty = new ComboBox<>("Sponsor Party");
    private ComboBox<String> currency = new ComboBox<>("Currency");

    private TextField rtgsAccountId = new TextField("RTGS ID");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<AccountOwnerDto> binder = new Binder<>(AccountOwnerDto.class);

    private CantonDataService cantonDataService;
    private String cbdc;


    public AccountOwnerForm(CantonDataService cantonDataService, String cbdc) {
        this.cantonDataService = cantonDataService;
        this.cbdc = cbdc;

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(centralBank, 2);
        centralBank.setItems( cantonDataService.getCdbcOperator()  );
        currency.setItems("SGD", "USD", "EUR","IDR");
        try {
            sponsorParty.setItems(getSponsors( cantonDataService.getCdbcOperator()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        bank.addValueChangeListener(event -> {
                    if (event.getValue().length()>3) {
                        bic.setValue(event.getValue().substring(0,3) + "XXXX" + 7);
                    }
                }
        );

        binder.forField(bank)
                .asRequired("Please input Bank name")
                .bind(AccountOwnerDto::getShortName,AccountOwnerDto::setShortName);

        binder.forField(centralBank)
                .asRequired("Please select CBDC")
                .bind(AccountOwnerDto::getCentralBankParty,AccountOwnerDto::setCentralBankParty);

        binder.forField(currency)
                .asRequired("Please select currency")
                .bind(AccountOwnerDto::getCurrency,AccountOwnerDto::setCurrency);


        bic.setEnabled(false);

        formLayout.add(centralBank, bank, bic, sponsorParty, rtgsAccountId, currency);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickListener(
               e -> {
                   binder.validate();
                   if(binder.isValid()) {
                       String rtgsId = rtgsAccountId.getValue().length() > 0 ? rtgsAccountId.getValue() : "";
                       TransactionResponseDto responseDto = cantonDataService.onboardAccountOwner(
                               new AccountOwnerDto(
                                       bank.getValue(), bic.getValue(),
                                       sponsorParty.getValue(), centralBank.getValue(),
                                       currency.getValue(), rtgsId
                               )
                       );

                       if (responseDto.getEffectiveAt() > 0) {
                           Notification.show("Bank Successfully Onboarded For Approval.", 3000,
                                   Notification.Position.BOTTOM_END);
                       } else {
                           Notification.show("Something went wrong", 3000,
                                   Notification.Position.BOTTOM_END);
                       }
                   }

                 }
       );

        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

    private void clearForm() {
        this.binder.setBean(new AccountOwnerDto());
    }

    private Component createTitle() {
        return new H3("Onboard Account Owner");
    }

    private List<String> getSponsors(String centralBank) throws Exception {
         List<AccountOwnerResponseDto> accountOwnerResponseDtoList = cantonDataService.listAccountOwners(centralBank);

       List<String> sponsors =  accountOwnerResponseDtoList
               .stream().map( accDto -> accDto.getShortName()
        ).collect(Collectors.toList());

        return  sponsors;
    }
}
