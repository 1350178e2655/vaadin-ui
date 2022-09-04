package com.partior.client.views.transfers;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.CentralBankDto;
import com.partior.client.dto.RequestTransferDto;
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

import java.math.BigDecimal;
import java.util.List;

/**
 *  Admin cannot do transfer
 */
public class TransferForm extends VerticalLayout {

    private ComboBox<String> centralBank = new ComboBox<>("Central Bank");

    private ComboBox<String> fromBanks = new ComboBox<>("From Bank");
    private ComboBox<String> fromAccountIds = new ComboBox<>("From Account Id");

    private ComboBox<String> toBanks = new ComboBox<>("To Bank");
    private ComboBox<String> toAccountIds = new ComboBox<>("To Account Id");

  //  private ComboBox<String> currency = new ComboBox<>("Currency");
    private TextField amount = new TextField("Amount");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Transfer");

    private Binder<CentralBankDto> binder = new Binder<>(CentralBankDto.class);

    private CantonDataService cantonDataService;
    public TransferForm(CantonDataService cantonDataService) throws Exception {
        this.cantonDataService = cantonDataService;
        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());
    }

    private Component createFormLayout() throws Exception {

        centralBank.setItems( cantonDataService.getCdbcOperator() );

        List<String> fromBanksList = UIUtils.getShortName( cantonDataService.getCdbcOperator() ,cantonDataService);


        centralBank.addValueChangeListener(event -> {
            try {
                fromBanks.setItems(fromBanksList);
                toBanks.setItems(UIUtils.getToBankShortName(event.getValue(),cantonDataService));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        fromBanks.addValueChangeListener(event -> {
            try {
                fromAccountIds.setItems(UIUtils.getParticipantAccount(event.getValue(),cantonDataService));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        toBanks.addValueChangeListener(event -> {
            try {
                toAccountIds.setItems(UIUtils.getParticipantAccount(event.getValue(),cantonDataService));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //currency.setItems("SGD", "USD", "EUR");

        FormLayout formLayout = new FormLayout();
        formLayout.add(centralBank, 2);
        formLayout.add(fromBanks, 1);
        formLayout.add(fromAccountIds, 1);
        formLayout.add(toBanks, 1);
        formLayout.add(toAccountIds, 1);
        formLayout.add(amount,1);

        centralBank.setValue(cantonDataService.getCdbcOperator());
        fromBanks.setValue(fromBanksList.iterator().next());
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

                        RtgsCallbackDto responseDto = cantonDataService.transfer(
                                new RequestTransferDto(
                                        fromBanks.getValue(),
                                        fromAccountIds.getValue(),
                                        toBanks.getValue(), toAccountIds.getValue(),
                                        Currency.valueOf(UIUtils.CBDC_CURRENCY.get(centralBank.getValue())),
                                        BigDecimal.valueOf( Double.valueOf(amount.getValue()))
                                )
                        );

                        if (responseDto.getResponse().equals("success")) {
                            Notification.show("Funds Successfully Transfered.", 3000,
                                    Notification.Position.TOP_CENTER);
                            clearForm();

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
        this.binder.setBean(new CentralBankDto());
    }

    private Component createTitle() {
        return new H3("Transfer");
    }
}
