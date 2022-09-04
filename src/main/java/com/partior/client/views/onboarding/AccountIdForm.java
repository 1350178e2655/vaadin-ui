package com.partior.client.views.onboarding;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.AccountOwnerDto;
import com.partior.client.dto.AccountOwnerResponseDto;
import com.partior.client.dto.CbdcAccountDto;
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

public class AccountIdForm extends VerticalLayout {

  //  private ComboBox<String> centralBank = new ComboBox<>("Central Bank");

   // private ComboBox<String> bic = new ComboBox<>("SHORT NAME");


    private Button cancel = new Button("Cancel");
    private Button save = new Button("Generate Account Id");

    private Binder<CbdcAccountDto> binder = new Binder<>(CbdcAccountDto.class);

    private List<AccountOwnerResponseDto> accountOwnerResponseDtoList;


    private CantonDataService cantonDataService;
    public AccountIdForm(CantonDataService cantonDataService) {
        this.cantonDataService = cantonDataService;
        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());
    }

    private Component createFormLayout() {

        FormLayout formLayout = new FormLayout();
//        formLayout.add(centralBank, 2);
//
//        try {
//            bic.setItems(getShortName(cantonDataService.getCdbcOperator()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        formLayout.add(bic, 2);
//
//        centralBank.setItems( cantonDataService.getCdbcOperator()  );


        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickListener(
                e -> {
                    binder.validate();
                    if (binder.isValid()) {
                        //AccountOwnerResponseDto bank = cantonDataService.getBank(bic.getValue(), accountOwnerResponseDtoList);
                        TransactionResponseDto responseDto = cantonDataService.onboardBankAccountId(
                                new CbdcAccountDto( cantonDataService.getUserBankName())
                        );

                        if (responseDto.getEffectiveAt() > 0) {
                            Notification.show("Bank Account ID Successfully Generated.", 3000,
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
        this.binder.setBean(new CbdcAccountDto());
    }

    private Component createTitle() {
        return new H3("Onboard Account ID");
    }

    private List<String> getBic(String centralBank) throws Exception {
        accountOwnerResponseDtoList = cantonDataService.listAccountOwners(centralBank);
        List<String> sponsors =  accountOwnerResponseDtoList
                .stream().map( accDto -> accDto.getBic()
                ).collect(Collectors.toList());
        return  sponsors;
    }

    private List<String> getShortName(String centralBank) throws Exception {
        accountOwnerResponseDtoList = cantonDataService.listAccountOwners(centralBank);
        List<String> sponsors =  accountOwnerResponseDtoList
                .stream().map( accDto -> accDto.getShortName()
                ).collect(Collectors.toList());
        return  sponsors;
    }
}
