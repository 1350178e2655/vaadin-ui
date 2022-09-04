package com.partior.client.views.transfers;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.CentralBankDto;
import com.partior.client.dto.RequestDepositDto;
import com.partior.client.dto.RequestWithdrawDto;
import com.partior.client.dto.RtgsCallbackDto;
import com.partior.client.dto.enums.Currency;
import com.partior.client.ui.util.UIUtils;
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
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;

import java.math.BigDecimal;
import java.util.List;

public class WithdrawForm extends VerticalLayout {

    private ComboBox<String> centralBank = new ComboBox<>("Central Bank");
    private ComboBox<String> banks = new ComboBox<>("Bank");
    private ComboBox<String> cbdcAccountId = new ComboBox<>("CBDC Account");
//    private ComboBox<String> rtgsAccount = new ComboBox<>("RTGS Account");

   // private ComboBox<String> currency = new ComboBox<>("Currency");
    private TextField amount = new TextField("Amount");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Withdraw");

    private Binder<RequestWithdrawDto> binder = new Binder<>(RequestWithdrawDto.class);

    private CantonDataService cantonDataService;

    public WithdrawForm(CantonDataService cantonDataService) throws Exception {
        this.cantonDataService = cantonDataService;
        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());
    }

    private Component createFormLayout() throws Exception {

        centralBank.setItems(cantonDataService.getCdbcOperator());
        List<String> shortNames = UIUtils.getShortName( cantonDataService.getCdbcOperator() ,cantonDataService);
        banks.setItems(shortNames);

        banks.addValueChangeListener(event -> {
            try {
                cbdcAccountId.setItems(UIUtils.getCbdcAccountId(event.getValue(),cantonDataService));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(centralBank, 1);
        formLayout.add(banks, 1);
        formLayout.add(cbdcAccountId, 1);

        formLayout.add(amount,1);

        centralBank.setValue(cantonDataService.getCdbcOperator());
        banks.setValue(shortNames.iterator().next());

        return formLayout;
    }

    private void setBinderAndValidation() {

        binder.forField(centralBank).asRequired("Please select CBDC Bank Bank")
                .bind(RequestWithdrawDto::getCentralBank,RequestWithdrawDto::setCentralBank);

        binder.forField(banks)
                .asRequired("Please select Bank Bank")
                .bind(RequestWithdrawDto::getBankParty,RequestWithdrawDto::setBankParty);

        binder.forField(amount)
                .asRequired("Please input amount")
                .withConverter(new StringToBigDecimalConverter("Not a number"))
                .bind(RequestWithdrawDto::getAmount, RequestWithdrawDto::setAmount);


    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickListener(
                e -> {
                    binder.validate();

                    if (binder.isValid()) {

                        try {
                            RtgsCallbackDto responseDto = cantonDataService.withdraw(
                                    new RequestWithdrawDto(
                                            cbdcAccountId.getValue(), banks.getValue(),
                                            BigDecimal.valueOf(Double.parseDouble(amount.getValue())),
                                            Currency.valueOf(UIUtils.CBDC_CURRENCY.get(centralBank.getValue()))
                                    )
                            );

                            if (responseDto.getResponse().equals("success")) {
                                Notification.show(" Withdrawal successfully Initiated", 3000,
                                        Notification.Position.TOP_CENTER);
                                clearForm();

                            } else {
                                Notification.show("Something went wrong", 3000,
                                        Notification.Position.TOP_CENTER);
                            }
                        } catch(Exception ex){
                            Notification.show("Something went wrong", 3000,
                                    Notification.Position.TOP_CENTER);
                        }
                    }
                }
        );

        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

    private void clearForm() {
        this.binder.setBean(new RequestWithdrawDto());
    }

    private Component createTitle() {
        return new H3("Withdraw");
    }
}
