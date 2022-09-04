package com.partior.client.views.transfers;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.AccountOwnerOnboardingResponse;
import com.partior.client.dto.FundPvpDto;
import com.partior.client.dto.PvpIntegrationDto;
import com.partior.client.dto.RequestDepositDto;
import com.partior.client.security.AuthenticatedUser;
import com.partior.client.ui.layout.size.Right;
import com.partior.client.ui.layout.size.Vertical;
import com.partior.client.ui.util.UIUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PvpTradesForm extends VerticalLayout {

    private ComboBox<String> centralBank = new ComboBox<>("CDBC DOMAIN");
    private Button save = new Button("Integrate");

    private Binder<RequestDepositDto> binder = new Binder<>(RequestDepositDto.class);
    private Grid<PvpIntegrationDto> grid;

    private Grid<PvpIntegrationDto> gridFund;


    private List<PvpIntegrationDto> listCbdcConnections;
    private ListDataProvider<PvpIntegrationDto> dataProvider;
    private ListDataProvider<PvpIntegrationDto> dataFundProvider;




    private CantonDataService cantonDataService;
    private AuthenticatedUser authenticatedUser;

    private Dialog createNewAgreementDialog = new Dialog();
    private Dialog createFundAgreementDialog = null;

    private Dialog createAcceptAgreementDialog = null;


    private List<PvpIntegrationDto> pvpIntegrationDtoList;


    public PvpTradesForm(CantonDataService cantonDataService, AuthenticatedUser authenticatedUser) throws Exception {
        this.cantonDataService = cantonDataService;
        this.authenticatedUser =authenticatedUser;

        this.setHeightFull();
        add( UIUtils.createTitle( VaadinIcon.CREDIT_CARD, "PVP Trades"));

        add(createAgreementsGrid());


    }

    private Component createFormLayout() throws Exception {

        setBinderAndValidation();

        centralBank.setItems(cantonDataService.getCdbcOperator() );

        FormLayout formLayout = new FormLayout();
        formLayout.add(centralBank, 1);
      //  formLayout.add(createButtonLayout(), 1);


        return formLayout;
    }

    private void setBinderAndValidation() {

        binder.forField(centralBank).asRequired("Please select CBDC DOMAIN")
                .bind(RequestDepositDto::getCentralBank,RequestDepositDto::setCentralBank);
    }


    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickListener(
                e -> {
                    binder.validate();

                    if (binder.isValid()) {

                        Notification.show("Integrating", 3000,
                                    Notification.Position.TOP_CENTER);
                    }
                }
        );


        buttonLayout.add(save);

        return buttonLayout;
    }

    private void clearForm() {
        this.binder.setBean(new RequestDepositDto());
    }

    private Component createTitle() {
        return new H3("CBDC DOMAIN CONNECTIVITY");
    }


    private Grid<PvpIntegrationDto> createAgreementsGrid() throws Exception {

        grid = new Grid<>();
        pvpIntegrationDtoList = cantonDataService.listPvpTrades(cantonDataService.getUserBankName());
        dataProvider = DataProvider.ofCollection(pvpIntegrationDtoList);
        grid.setDataProvider(dataProvider);


        grid.addColumn(this::getTradeid)
              //  .setAutoWidth(true)
             //   .setFlexGrow(1)
                .setHeader("TRADEID")
                .setSortable(true);

        grid.addColumn(new ComponentRenderer<>(this::createFromLeg1)) //((new ComponentRenderer<>(this::createAccountOwnerInfo)) this::createFromLeg1)
             //   .setAutoWidth(true)
            //    .setFlexGrow(1)
                .setHeader("FROM")
                .setSortable(true);

        grid.addColumn(this::getRates)
             //   .setAutoWidth(true)
             //   .setFlexGrow(1)
                .setHeader("FxRATES")
                .setSortable(true);

        grid.addColumn(new ComponentRenderer<>(this::createFromLeg2))
             //   .setAutoWidth(true)
             //   .setFlexGrow(1)
                .setHeader("TO")
                .setSortable(true);


        grid.addColumn(this::getStatus)
       //         .setAutoWidth(true)
       //         .setFlexGrow(1)
                .setHeader("STATUS")
                .setSortable(true);


        grid.addComponentColumn((pvpIntegrationDto) -> {
            try {
                return createActionButtonLayout(pvpIntegrationDto);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).setHeader("ACTION");

        return grid;
    }

    private String fromAccount(PvpIntegrationDto pvpIntegrationDto){
        return pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountOwner().getShortName();
    }

    private String getTradeid(PvpIntegrationDto pvpIntegrationDto){
        return pvpIntegrationDto.getPvp().getTradeId();
    }

    private String fromAmount(PvpIntegrationDto pvpIntegrationDto){
        return  String.valueOf(pvpIntegrationDto.getPvp().getLeg1().getAmount());
    }

    private String fromCurrency(PvpIntegrationDto pvpIntegrationDto){
        return pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountOwner().getCurrency();
    }

    private String getRates(PvpIntegrationDto pvpIntegrationDto){
        return pvpIntegrationDto.getPvp().getRates();
    }

    private String fromAccountId(PvpIntegrationDto pvpIntegrationDto){
        return pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountId();
    }

    private String toAccount(PvpIntegrationDto pvpIntegrationDto){
        return pvpIntegrationDto.getPvp().getLeg2().getFromAccount().getAccountOwner().getShortName();
    }

    private String toAmount(PvpIntegrationDto pvpIntegrationDto){
        return  String.valueOf(pvpIntegrationDto.getPvp().getLeg2().getAmount());
    }

    private String toCurrency(PvpIntegrationDto pvpIntegrationDto){
        return pvpIntegrationDto.getPvp().getLeg2().getFromAccount().getAccountOwner().getCurrency();
    }

    private String toAccountId(PvpIntegrationDto pvpIntegrationDto){
        return pvpIntegrationDto.getPvp().getLeg2().getFromAccount().getAccountId();
    }

    private String getStatus(PvpIntegrationDto pvpIntegrationDto){
        if(pvpIntegrationDto.getPvp().getStatus().equalsIgnoreCase("PARTIALFUNDING")){
            return "PARTIALLY FUNDED";
        } else {
            return pvpIntegrationDto.getPvp().getStatus();
        }
    }

    private Grid<PvpIntegrationDto> createFundAgreementsGrid() throws Exception {

        gridFund = new Grid<>();
        listCbdcConnections = new ArrayList();
       // listCbdcConnections.add(new PvpIntegrationDto("BI", "08:00 - 17:00", "ONLINE"));

        dataFundProvider = DataProvider.ofCollection(listCbdcConnections);
        gridFund.setDataProvider(dataFundProvider);


        gridFund.addColumn(this::status)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("FROM")
                .setSortable(true);

        gridFund.addColumn(this::status)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("TO")
                .setSortable(true);

        gridFund.addColumn(this::status)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("AMOUNT")
                .setSortable(true);

        gridFund.addColumn(this::status)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("CURRENCY")
                .setSortable(true);



        gridFund.addComponentColumn((accountOwnerResponseDto) -> {
            try {
                return createFundDetailsButtonLayout(accountOwnerResponseDto);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).setHeader("ACTION");

        return gridFund;
    }


    public void addToGrid(PvpIntegrationDto pvpIntegrationDto){
        listCbdcConnections.add(pvpIntegrationDto);
        dataProvider.refreshAll();
    }

    public void editToGrid(int idx, PvpIntegrationDto pvpIntegrationDto){
        listCbdcConnections.set(idx, pvpIntegrationDto);
        dataProvider.refreshItem(pvpIntegrationDto);
    }

    private String cbdcName(PvpIntegrationDto pvpIntegrationDto) {
        return pvpIntegrationDto.getDomain();
    }

    private String status(PvpIntegrationDto pvpIntegrationDto) {
        return pvpIntegrationDto.getStatus();
    }

    private Component createFromLeg1(PvpIntegrationDto pvpIntegrationDto) {
        com.partior.client.ui.components.ListItem item = new com.partior.client.ui.components.ListItem(
                pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountOwner().getShortName() ,
                pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountId(),
                pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountOwner().getCurrency(),
                 String.valueOf( pvpIntegrationDto.getPvp().getLeg1().getAmount())
         );

        return item;
    }

    private Component createFromLeg2(PvpIntegrationDto pvpIntegrationDto) {
        com.partior.client.ui.components.ListItem item = new com.partior.client.ui.components.ListItem(
                pvpIntegrationDto.getPvp().getLeg2().getFromAccount().getAccountOwner().getShortName() ,
                pvpIntegrationDto.getPvp().getLeg2().getFromAccount().getAccountId(),
                pvpIntegrationDto.getPvp().getLeg2().getFromAccount().getAccountOwner().getCurrency(),
                String.valueOf( pvpIntegrationDto.getPvp().getLeg2().getAmount())
        );
        return item;
    }





    private Component createActionButtonLayout(PvpIntegrationDto pvpIntegrationDto) throws Exception {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");




//        final VerticalLayout dialogLayout = accountOwnerResponseDto.getStatus().equalsIgnoreCase("INITIATED")?
//                createAcceptAgreementDialogLayout(createFundAgreementDialog):createFundAgreementDialogLayout(createFundAgreementDialog);

         Button connect = new Button("Details");
         connect.addClickListener( e ->{
             VerticalLayout dialogLayout  = null;
             try {
//                 if (accountOwnerResponseDto.getStatus().equalsIgnoreCase("INITIATED")) {
//                     createAcceptAgreementDialog = new Dialog();
//
//                     createAcceptAgreementDialog.setWidth(70, Unit.PERCENTAGE);
//                     createAcceptAgreementDialog.getElement().setAttribute("aria-label", "Funding");
//                     dialogLayout = createAcceptAgreementDialogLayout(createAcceptAgreementDialog);
//                     createAcceptAgreementDialog.add(dialogLayout);
//                     createAcceptAgreementDialog.open();
//
//                 } else {
                     createFundAgreementDialog = new Dialog();

                     createFundAgreementDialog.setWidth(80, Unit.PERCENTAGE);
                     createFundAgreementDialog.getElement().setAttribute("aria-label", "Funding");
                     dialogLayout = createFundAgreementDialogLayout(createFundAgreementDialog,pvpIntegrationDto);
                     createFundAgreementDialog.add(dialogLayout);
                     createFundAgreementDialog.open();

            //     }


             } catch(Exception ex){
                    ex.printStackTrace();
             }


         });

         connect.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button reject = new Button("REJECT");

        reject.addThemeVariants(ButtonVariant.LUMO_SMALL);


        connect.addClickListener(
                e -> {
               //     accountOwnerResponseDto.getCbdc();
                }
        );
        buttonLayout.add(connect);
        return buttonLayout;
    }

    private Component createFundDetailsButtonLayout(PvpIntegrationDto pvpIntegrationDto) throws Exception {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");


        createFundAgreementDialog.setWidth(70, Unit.PERCENTAGE);
        createFundAgreementDialog.getElement().setAttribute("aria-label", "Funding");
        VerticalLayout dialogLayout = createFundAgreementDialogLayout(createFundAgreementDialog,pvpIntegrationDto);
        createFundAgreementDialog.add(dialogLayout);

        Button connect = new Button("Details");
        connect.addClickListener( e ->{
            createFundAgreementDialog.open();
                }
        );

        connect.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button reject = new Button("REJECT");

        reject.addThemeVariants(ButtonVariant.LUMO_SMALL);


        connect.addClickListener(
                e -> {
                }
        );
        buttonLayout.add(connect);

        return buttonLayout;
    }



    private  VerticalLayout createButtonRightSide() throws Exception {

        Button createNewAgreement = new Button("New Agreement");
        createNewAgreement.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout horizontalLayout = new HorizontalLayout(createNewAgreement);

        createNewAgreementDialog.setWidth(70, Unit.PERCENTAGE);
        createNewAgreementDialog.getElement().setAttribute("aria-label", "New Agreement");
        VerticalLayout dialogLayout = createNewAgreementDialogLayout(createNewAgreementDialog);
        createNewAgreementDialog.add(dialogLayout);

        createNewAgreement.addClickListener( click ->{
                    createNewAgreementDialog.open();
                }
        );

        VerticalLayout fieldLayout = new VerticalLayout(horizontalLayout);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(Alignment.END);


        return fieldLayout;

    }

    private  VerticalLayout createNewAgreementDialogLayout(Dialog dialog) throws Exception {

        H3 h1 = new H3("New Agreement");

        TextField amount = new TextField("amount");

        TextField currency = new TextField("currency");
                 currency.setValue( UIUtils.CBDC_CURRENCY.get(cantonDataService.getCdbcOperator())  );


        Label from = new Label("from");
        ComboBox fromAccountId = new ComboBox("account id");
        try {
            fromAccountId.setItems(UIUtils.getCbdcAccountId( cantonDataService.getUserBankName(), cantonDataService));
        } catch (Exception e) {
            e.printStackTrace();
        }


        Div div =  new Div(new Text("I want to send"));
        HorizontalLayout iwantTo = new HorizontalLayout( div  ,amount, currency, from, fromAccountId );
        iwantTo.setWidthFull();
        iwantTo.setSpacing(true);
        iwantTo.setPadding(true);
        iwantTo.setDefaultVerticalComponentAlignment( Alignment.BASELINE );



        TextField toAmount = new TextField("amount");

        TextField toCurrency = new TextField("currency");
        toCurrency.setValue("IDR");

        Label to = new Label("to");
        ComboBox toAccountOwner = new ComboBox("account owner");

        List<String> otherCbdc = UIUtils.getToBankShortName(cantonDataService.getCdbcOperator(),cantonDataService);


        toAccountOwner.setItems(otherCbdc);


        ComboBox toAccountId = new ComboBox("account id");

        toAccountOwner.addValueChangeListener(event -> {
            try {
                if(event.getValue()!=null ) {
                    toAccountId.setItems(UIUtils.getCbdcAccountId( event.getValue().toString(), cantonDataService));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Div divToReceive =  new Div(new Text("to receive"));
        HorizontalLayout toReceive = new HorizontalLayout( divToReceive  ,toAmount, toCurrency, to, toAccountOwner, toAccountId );
        toReceive.setWidthFull();
        toReceive.setSpacing(true);
        toReceive.setPadding(true);

        toReceive.setDefaultVerticalComponentAlignment( Alignment.BASELINE );


        Div fromCounterPartyDiv =  new Div(new Text("from counterparty bank"));
        TextField counterParty = new TextField("account owner");
        counterParty.setValue(cantonDataService.getUserBankName());

        HorizontalLayout fromCounterParty = new HorizontalLayout( fromCounterPartyDiv  ,counterParty);
        fromCounterParty.setWidthFull();
        fromCounterParty.setSpacing(true);
        fromCounterParty.setPadding(true);
        fromCounterParty.setDefaultVerticalComponentAlignment( Alignment.BASELINE );

        Button saveButton = new Button("INITIATE", e -> dialog.close());
        Button cancelButton = new Button("CLOSE", e -> dialog.close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(h1, iwantTo, toReceive, fromCounterParty, buttonLayout);
        dialogLayout.setSpacing(false);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(Alignment.START);


        return dialogLayout;
    }


    private  VerticalLayout createFundAgreementDialogLayout(Dialog dialog, PvpIntegrationDto pvpIntegrationDto) throws Exception {

        H3 h1 = new H3("Fund PVP");

        String userName = cantonDataService.getUserBankName();

        String fromShortName = userName.equals(pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountOwner().getShortName())
                ?pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountOwner().getShortName()
                :pvpIntegrationDto.getPvp().getLeg2().getFromAccount().getAccountOwner().getShortName();

        String contractId =  pvpIntegrationDto.getPvp().getContractId();


        String fromTxtAmount = pvpIntegrationDto.getPvp().getLeg1().getAmount().toString();
        String fromTxtCurrency =  pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountOwner().getCurrency();
        String fromTxtAccountId =  pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountId();

        String fromTxtCounterParty = pvpIntegrationDto.getPvp().getLeg2().getFromAccount().getAccountOwner().getShortName();

        String toTxtAmount = pvpIntegrationDto.getPvp().getLeg2().getAmount().toString();
        String toTxtCurrency =  pvpIntegrationDto.getPvp().getLeg2().getToAccount().getAccountOwner().getCurrency();
        String toTxtAccountOwner = pvpIntegrationDto.getPvp().getLeg2().getToAccount().getAccountOwner().getShortName();
        String toTxtAccountId =  pvpIntegrationDto.getPvp().getLeg2().getToAccount().getAccountId();

        if(!userName.equals(pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountOwner().getShortName())){

             fromTxtAmount = pvpIntegrationDto.getPvp().getLeg2().getAmount().toString();
             fromTxtCurrency =  pvpIntegrationDto.getPvp().getLeg2().getFromAccount().getAccountOwner().getCurrency();
             fromTxtAccountId =  pvpIntegrationDto.getPvp().getLeg2().getFromAccount().getAccountId();
             fromTxtCounterParty = pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountOwner().getShortName();

             toTxtAmount = pvpIntegrationDto.getPvp().getLeg1().getAmount().toString();
             toTxtCurrency =  pvpIntegrationDto.getPvp().getLeg1().getToAccount().getAccountOwner().getCurrency();
             toTxtAccountOwner = pvpIntegrationDto.getPvp().getLeg1().getToAccount().getAccountOwner().getShortName();
             toTxtAccountId =  pvpIntegrationDto.getPvp().getLeg1().getToAccount().getAccountId();
        }


        TextField amount = new TextField("amount");
        amount.setValue( fromTxtAmount );


        TextField currency = new TextField("currency");
        currency.setValue( fromTxtCurrency );

        Label from = new Label("from");

        TextField fromAccount = new TextField("account id");
        fromAccount.setValue( fromTxtAccountId);

        Div div =  new Div(new Text("I want to fund"));
        HorizontalLayout iwantTo = new HorizontalLayout( div  ,amount, currency, from, fromAccount );
        iwantTo.setWidthFull();
        iwantTo.setSpacing(true);
        iwantTo.setPadding(true);
        iwantTo.setDefaultVerticalComponentAlignment( Alignment.BASELINE );


        TextField toAmount = new TextField("amount");
        toAmount.setValue(toTxtAmount);

        TextField toCurrency = new TextField("currency");
        toCurrency.setValue(toTxtCurrency);

        Label to = new Label("to");
        TextField toAccountOwner = new TextField("account owner");
        toAccountOwner.setValue( toTxtAccountOwner );
        List<String> otherCbdc = UIUtils.getCbdcAccountId("BI", cantonDataService);


        TextField toAccountId = new TextField("account id");
        toAccountId.setValue(toTxtAccountId);

        Div divToReceive =  new Div(new Text("to receive"));
        HorizontalLayout toReceive = new HorizontalLayout( divToReceive  ,toAmount, toCurrency, to, toAccountOwner, toAccountId );
        toReceive.setWidthFull();
        toReceive.setSpacing(true);
        toReceive.setPadding(true);
        // toReceive.getStyle().set("margin-top", "5px");
        toReceive.setDefaultVerticalComponentAlignment( Alignment.BASELINE );


        Div fromCounterPartyDiv =  new Div(new Text("from counterparty bank"));
        TextField counterParty = new TextField("account owner");
        counterParty.setValue(fromTxtCounterParty);

        HorizontalLayout fromCounterParty = new HorizontalLayout( fromCounterPartyDiv  ,counterParty);
        fromCounterParty.setWidthFull();
        fromCounterParty.setSpacing(true);
        fromCounterParty.setPadding(true);
        fromCounterParty.setDefaultVerticalComponentAlignment( Alignment.BASELINE );

        Button fundButton = new Button("FUND", e -> dialog.close());
        fundButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        if(pvpIntegrationDto.getPvp().getLeg1().getFromAccount().getAccountOwner().getShortName().equalsIgnoreCase(userName)){
//            if(pvpIntegrationDto.getPvp().getLeg1(). .getCommittedToken()!=null) {
//                fundButton.setEnabled(false);
//            }
        }else if(pvpIntegrationDto.getPvp().getLeg2().getFromAccount().getAccountOwner().getShortName().equalsIgnoreCase(userName)) {
//            if(pvpIntegrationDto.getPvp().getLeg2().getCommittedToken()!=null) {
//                fundButton.setEnabled(false);
//            }
        }

        fundButton.addClickListener( click->{
                  String response =  cantonDataService.fundPvp(new FundPvpDto( fromShortName, contractId ) );
                    if(response!=null){
                        Notification.show( "PVP Trade successfully funded", 3000, Notification.Position.BOTTOM_CENTER);

                        try {
                            pvpIntegrationDtoList = cantonDataService.listPvpTrades(cantonDataService.getUserBankName());
                            grid.setDataProvider(DataProvider.ofCollection(pvpIntegrationDtoList));
                            grid.getDataProvider().refreshAll();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        log.info("PVP Fund Response:{}", response);
                    } else {
                        Notification.show( "Something went wrong", 3000, Notification.Position.BOTTOM_CENTER);
                    }
            }

        );
        Button closeButton = new Button("CLOSE", e -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_SMALL);


        HorizontalLayout buttonLayout = new HorizontalLayout(fundButton, closeButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(h1, iwantTo, toReceive, fromCounterParty, buttonLayout);

        dialogLayout.setSpacing(false);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(Alignment.START);


        return dialogLayout;
    }


    private  VerticalLayout createAcceptAgreementDialogLayout(Dialog dialog) throws Exception {

        H3 h1 = new H3("Accept Agreement");

        TextField amount = new TextField("amount");

        TextField currency = new TextField("currency");
        currency.setValue( UIUtils.CBDC_CURRENCY.get(cantonDataService.getCdbcOperator())  );

        Label from = new Label("from");
        ComboBox fromAccount = new ComboBox("account id");
        fromAccount.setItems(UIUtils.getCbdcAccountId(cantonDataService.getUserBankName(), cantonDataService));

        Div div =  new Div(new Text("I agree to send"));
        HorizontalLayout iwantTo = new HorizontalLayout( div  ,amount, currency, from, fromAccount );
        iwantTo.setWidthFull();
        iwantTo.setSpacing(true);
        iwantTo.setPadding(true);
        iwantTo.setDefaultVerticalComponentAlignment( Alignment.BASELINE );



        TextField toAmount = new TextField("amount");

        TextField toCurrency = new TextField("currency");
        toCurrency.setValue("IDR");

        Label to = new Label("to");
        ComboBox toAccountOwner = new ComboBox("account owner");
        List<String> otherCbdc = UIUtils.getCbdcAccountId("BI", cantonDataService);

        toAccountOwner.setItems(otherCbdc);

        ComboBox toAccountId = new ComboBox("account id");

        toAccountOwner.addValueChangeListener(event -> {
            try {
                if(event.getValue()!=null ) {
                    toAccountId.setItems(UIUtils.getCbdcAccountId( event.getValue().toString(), cantonDataService));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });



        Div divToReceive =  new Div(new Text("to receive"));
        HorizontalLayout toReceive = new HorizontalLayout( divToReceive  ,toAmount, toCurrency, to, toAccountOwner, toAccountId );
        toReceive.setWidthFull();
        toReceive.setSpacing(true);
        toReceive.setPadding(true);
        // toReceive.getStyle().set("margin-top", "5px");
        toReceive.setDefaultVerticalComponentAlignment( Alignment.BASELINE );


        Div fromCounterPartyDiv =  new Div(new Text("from counterparty bank"));
        TextField counterParty = new TextField("account owner");
        counterParty.setValue(cantonDataService.getUserBankName());
        HorizontalLayout fromCounterParty = new HorizontalLayout( fromCounterPartyDiv  ,counterParty);
        fromCounterParty.setWidthFull();
        fromCounterParty.setSpacing(true);
        fromCounterParty.setPadding(true);
        fromCounterParty.setDefaultVerticalComponentAlignment( Alignment.BASELINE );

        Button fundButton = new Button("PROCEED", e -> dialog.close());
        fundButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button cancelButton = new Button("REJECT", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button closeButton = new Button("CANCEL", e -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_SMALL);


        HorizontalLayout buttonLayout = new HorizontalLayout(fundButton, cancelButton, closeButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(h1, iwantTo, toReceive, fromCounterParty, buttonLayout);
        dialogLayout.setSpacing(false);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(Alignment.START);


        return dialogLayout;
    }

}
