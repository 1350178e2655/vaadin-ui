package com.partior.client.views.transfers;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.*;
import com.partior.client.dto.enums.Currency;
import com.partior.client.security.AuthenticatedUser;
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
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;


public class DepositForm extends VerticalLayout {

    private ComboBox<String> centralBank = new ComboBox<>("Central Bank");
    private ComboBox<String> banks = new ComboBox<>("Bank");
    //private ComboBox<String> rtgsAccountIds = new ComboBox<>("RTGS Account");
    private ComboBox<String> cbdcAccountId = new ComboBox<>("CBDC Account");
//    private ComboBox<String> currency = new ComboBox<>("Currency");
    private TextField amount = new TextField("Amount");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Deposit");

    private Binder<RequestDepositDto> binder = new Binder<>(RequestDepositDto.class);

    private CantonDataService cantonDataService;
    private AuthenticatedUser authenticatedUser;


    public DepositForm(CantonDataService cantonDataService, AuthenticatedUser authenticatedUser) throws Exception {
        this.cantonDataService = cantonDataService;
        this.authenticatedUser =authenticatedUser;
        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());
    }

    private Component createFormLayout() throws Exception {

        setBinderAndValidation();

        centralBank.setItems(cantonDataService.getCdbcOperator() );
        List<String> shortNames = UIUtils.getShortName( cantonDataService.getCdbcOperator() ,cantonDataService);
        banks.setItems(shortNames);

        banks.addValueChangeListener(event -> {
            try {
                if(event.getValue()!=null && event.getValue().length()>0) {
                    cbdcAccountId.setItems(UIUtils.getCbdcAccountId(event.getValue(), cantonDataService));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        FormLayout formLayout = new FormLayout();
        formLayout.add(centralBank, 1);
        formLayout.add(banks, 1);
     //   formLayout.add(rtgsAccountIds, 1);
        formLayout.add(cbdcAccountId, 1);
        formLayout.add(amount,1);

        centralBank.setValue(cantonDataService.getCdbcOperator());
        banks.setValue(shortNames.iterator().next());

        return formLayout;
    }

    private void setBinderAndValidation() {

        binder.forField(centralBank).asRequired("Please select CBDC Bank Bank")
                .bind(RequestDepositDto::getCentralBank,RequestDepositDto::setCentralBank);

        binder.forField(banks)
                .asRequired("Please select Bank Bank")
                .bind(RequestDepositDto::getBankParty,RequestDepositDto::setBankParty);

        binder.forField(amount)
                .asRequired("Please input amount")
                .withConverter(new StringToBigDecimalConverter("Not a number"))
                .bind(RequestDepositDto::getAmount, RequestDepositDto::setAmount);


    }


    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickListener(
                e -> {
                    binder.validate();

                    if (binder.isValid()) {

                        RtgsCallbackDto responseDto = cantonDataService.deposit(
                                new RequestDepositDto(
                                cbdcAccountId.getValue(), banks.getValue(),
                                BigDecimal.valueOf( Double.parseDouble(amount.getValue())),
                                Currency.valueOf(UIUtils.CBDC_CURRENCY.get(centralBank.getValue()))
                                 )
                        );

                        if (responseDto.getResponse().equals("success")) {
                            Notification.show("Bank Deposit process initiated", 3000,
                                    Notification.Position.TOP_CENTER);


                        } else {
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
        this.binder.setBean(new RequestDepositDto());
    }

    private Component createTitle() {
        return new H3("Deposit");
    }
}
