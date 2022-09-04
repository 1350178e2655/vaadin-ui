package com.partior.client.views.accounts;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.*;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.layout.size.*;
import com.partior.client.ui.util.IconSize;
import com.partior.client.ui.util.TextColor;
import com.partior.client.ui.util.UIUtils;
import com.partior.client.ui.util.css.BoxSizing;
import com.partior.client.ui.util.css.Display;
import com.partior.client.views.MainLayout;
import com.partior.client.views.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

@Route(value = "domain-account-overview", layout = MainLayout.class)
@PageTitle("AccountOwner List")
@PermitAll
public class ListAccountOwnerView extends SplitViewFrame {

    private Grid<AccountOwnerResponseDto> grid;
    private ListDataProvider<AccountOwnerResponseDto> dataProvider;
    private List<AccountOwnerResponseDto> listAccountOwners;


    private Grid<AccountOwnerResponseDto> accountIdGrid;
    private List<AccountOwnerResponseDto> listAccountId;
    private ListDataProvider<AccountOwnerResponseDto> accountIdDataProvider;


    private Grid<AccountOwnerOnboardingResponse> accountForApprovalGrid;

    private ListDataProvider<AccountOwnerOnboardingResponse> forApprovalDataProvider;

    private List<AccountOwnerOnboardingResponse> listOnboarding;


    @Autowired
    final CantonDataService cantonDataService;

    public ListAccountOwnerView(CantonDataService cantonDataService) throws Exception {
        this.cantonDataService = cantonDataService;
        setViewContent(createAccountOwnerApprovalContent(), createContent(), createAccountIdContent() );
        setViewDetailsPosition(Position.BOTTOM);
    }

    private Component createAccountOwnerApprovalContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.USER, "Pending Participants"),
                createAccountOwnerApprovalGrid());

        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();

        return content;
    }


    private FlexBoxLayout createHeader(VaadinIcon icon, String title) {

        FlexBoxLayout header = new FlexBoxLayout(
                UIUtils.createIcon(IconSize.M, TextColor.TERTIARY, icon),
                UIUtils.createH3Label(title));

        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setMargin(Bottom.L, Horizontal.RESPONSIVE_L);
        header.setSpacing(Right.L);
        return header;
    }

    private Grid createAccountOwnerApprovalGrid() throws Exception {

        accountForApprovalGrid = new Grid<>();
        listOnboarding = cantonDataService.listAccountOwnerOnboardingRequest(cantonDataService.getCdbcOperator() );
        forApprovalDataProvider = DataProvider.ofCollection(listOnboarding);
        accountForApprovalGrid.setDataProvider(forApprovalDataProvider);

        accountForApprovalGrid.addColumn(new ComponentRenderer<>(this::createAccountOwnerForApprovalInfo))
             //   .setFrozen(true)
                .setHeader("BankName");

        accountForApprovalGrid.addColumn(AccountOwnerOnboardingResponse::getCurrency)
            //    .setFrozen(true)
                .setHeader("Currency")
                .setSortable(true);

        accountForApprovalGrid.addColumn(this::createForApprovalType)
            //    .setFlexGrow(0)
                .setHeader("Type");
                //.setTextAlign(ColumnTextAlign.END);

        accountForApprovalGrid.addComponentColumn((onboardResponse) -> {
            Button approve = new Button("APPROVE");
            approve.addThemeVariants(ButtonVariant.LUMO_SMALL);

            Button reject = new Button("REJECT");
            reject.addThemeVariants(ButtonVariant.LUMO_SMALL);

            Icon iconApprove = new Icon(VaadinIcon.CHECK);
            iconApprove.setColor("green");
            approve.setIcon(iconApprove);

            Icon iconReject = new Icon(VaadinIcon.EXCLAMATION);
            iconReject.setColor("red");
            reject.setIcon(iconReject);



            approve.addClickListener(
                    event ->{
                        TransactionResponseDto transactionResponseDto =    cantonDataService.approveOnnboardingRequest(
                                new AccountOwnerOnboardingRequest(onboardResponse.getShortName(),
                                        onboardResponse.getCentralBank())
                        );

                        if(transactionResponseDto.getEffectiveAt() > 0 ){
                            Notification.show("Account Owner has been approved ", 3000,
                                    Notification.Position.BOTTOM_END);

                            listOnboarding.removeIf( onboarding -> ( onboarding.getShortName().equals(onboardResponse.getShortName()))  );
                            forApprovalDataProvider.refreshAll();

                            AccountOwnerResponseDto responseDto = new AccountOwnerResponseDto();
                            try {
                                BeanUtils.copyProperties(responseDto, onboardResponse);
                                responseDto.setSponsorParty( onboardResponse.getSponsor());
                                responseDto.setCentralBankParty(onboardResponse.getCentralBank());
                                responseDto.setLocal( onboardResponse.getIsLocal());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }

                            listAccountOwners.add( responseDto);
                            dataProvider.refreshAll();

                        }
                    }
            );

            reject.addClickListener(
                    event ->{
                        TransactionResponseDto transactionResponseDto =   cantonDataService.rejectOnnboardingRequest(
                                new AccountOwnerOnboardingRequest(onboardResponse.getShortName(),
                                        onboardResponse.getCentralBank())
                        );

                        if(transactionResponseDto.getEffectiveAt() > 0 ) {
                            Notification.show("Account Owner has been rejected ", 3000,
                                    Notification.Position.BOTTOM_END);
                        }

                    }
            );

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setSpacing(true);
            horizontalLayout.add( reject, approve);
            return horizontalLayout;
        }).setHeader("Enable");


        return accountForApprovalGrid;
    }


    private Component createContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.USER, "Onboarded Participants"),

                createGrid());
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();

        return content;
    }

    private Grid createGrid() throws Exception {

        grid = new Grid<AccountOwnerResponseDto>();
        listAccountOwners = cantonDataService.listAccountOwners(cantonDataService.getCdbcOperator() );
        dataProvider = DataProvider.ofCollection(listAccountOwners);
        grid.setDataProvider(dataProvider);

        grid.addColumn(new ComponentRenderer<>(this::createAccountOwnerInfo))
                .setHeader("Bic");


        grid.addColumn( this::createParty  )
                .setHeader("CBDC")
                .setSortable(true);
        grid.addColumn(  this::sponsor)
                .setHeader("Sponsor")
                .setSortable(true);

        grid.addColumn(AccountOwnerResponseDto::getCurrency)
                .setFrozen(true)
                .setHeader("Currency")
                .setSortable(true);


        grid.addColumn(this::createType)
                .setHeader("Type");


        List<AccountOwnerResponseDto>  disabledAccounts =cantonDataService.listDisabledParticipantBanks(cantonDataService.getCdbcOperator() );


        grid.addComponentColumn((accountOwnerResponseDto) -> {
            Checkbox checkBox = new Checkbox();
            checkBox.setValue( disabledAccounts.indexOf(accountOwnerResponseDto) < 0 ? true: false);
            checkBox.addValueChangeListener(
                    event ->  {

                        boolean disable = !event.getValue();

                        AccountOwnerResponseDto bank = cantonDataService.getBank(accountOwnerResponseDto.getShortName(), listAccountOwners);

                        TransactionResponseDto transactionResponseDto =  cantonDataService.enableOrDisableAccOwner(
                                new DisableAccountOwnerResponseDto(accountOwnerResponseDto.getBic(), bank.getShortName(),
                                        cantonDataService.getCdbcOperator(),disable));


                        if(transactionResponseDto.getEffectiveAt() > 0){
                            Notification.show("Account was successfully updated.", 3000,
                                    Notification.Position.BOTTOM_CENTER);
                        } else {
                            Notification.show("Something went wrong.", 3000,
                                    Notification.Position.BOTTOM_CENTER);
                        }
                    }
            );
            return checkBox;
        }).setHeader("Enable");

        grid.addSelectionListener(selection -> {
            Optional<AccountOwnerResponseDto> optionalAccountOwner = selection.getFirstSelectedItem();
            if (optionalAccountOwner.isPresent()) {

                try {
                    listAccountId = cantonDataService.listParticipantBankAccounts(optionalAccountOwner.get().getShortName());
                    accountIdDataProvider = DataProvider.ofCollection(listAccountId);
                    accountIdGrid.setDataProvider(accountIdDataProvider);
                    accountIdDataProvider.refreshAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return grid;
    }

    private String createParty(AccountOwnerResponseDto accountOwnerResponseDto) {
        return UIUtils.CBDC_NAME.get( cantonDataService.getCdbcOperator() );
    }


    private Component createAccountOwnerInfo(AccountOwnerResponseDto accountOwnerResponseDto) {
        com.partior.client.ui.components.ListItem item = new com.partior.client.ui.components.ListItem(
                new com.partior.client.ui.components.Initials( accountOwnerResponseDto.getBic().substring(0,1)),
                accountOwnerResponseDto.getShortName() );

        item.setPadding(Vertical.XS);
        item.setSpacing(Right.M);
        return item;
    }

    private Component createAccountOwnerForApprovalInfo(AccountOwnerOnboardingResponse accountOwnerOnboardingResponse) {
        com.partior.client.ui.components.ListItem item = new com.partior.client.ui.components.ListItem(
                new com.partior.client.ui.components.Initials( accountOwnerOnboardingResponse.getBic().substring(0,1)),
                accountOwnerOnboardingResponse.getShortName());

        item.setPadding(Vertical.XS);
        item.setSpacing(Right.M);
        return item;
    }

    private String createType(AccountOwnerResponseDto accountOwnerResponseDto ) {
        return accountOwnerResponseDto.isLocal() ? "Local" : "Foreign";
    }

    private String createForApprovalType(AccountOwnerOnboardingResponse accountOwnerOnboardingResponse ) {
        return accountOwnerOnboardingResponse.getIsLocal() ? "Local" : "Foreign";
    }

    private String party(AccountOwnerResponseDto accountOwnerResponseDto){
        return accountOwnerResponseDto.getCentralBankParty().substring(0,3);
    }

    private String sponsor(AccountOwnerResponseDto accountOwnerResponseDto){
        return accountOwnerResponseDto.getSponsorParty() != null?
                accountOwnerResponseDto.getSponsorParty().split("::")[0]:accountOwnerResponseDto.getBank();
    }


    private Component createAccountIdContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.USER, "Participant Accounts"),

                createAccountIdGrid());

        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();
        return content;

    }


    private Grid createAccountIdGrid() throws Exception {
        accountIdGrid = new Grid<AccountOwnerResponseDto>();
        accountIdGrid.addColumn(AccountOwnerResponseDto::getAccountId)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("ACCOUNT ID");

        return accountIdGrid;
    }




}
