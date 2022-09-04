package com.partior.client.views.accounts;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.*;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.layout.size.Horizontal;
import com.partior.client.ui.layout.size.Right;
import com.partior.client.ui.layout.size.Vertical;
import com.partior.client.ui.util.UIUtils;
import com.partior.client.ui.util.css.BoxSizing;
import com.partior.client.views.MainLayout;
import com.partior.client.views.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
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

@Route(value = "csd-domain-account-overview", layout = MainLayout.class)
@PageTitle("AccountOwner List")
@PermitAll
public class ListCsdAccountOwnerView extends SplitViewFrame {

    private Grid<CSDAccountOwner> csdAccountOwnerGrid;
    private ListDataProvider<CSDAccountOwner> csdAccountOwnerDataProvider;
    private List<CSDAccountOwner> csdAccountOwnerList;


    private Grid<CSDAccount> accountIdGrid;
    private List<CSDAccount> listAccountId;
    private ListDataProvider<CSDAccount> accountIdDataProvider;


    private Grid<AccountOwnerOnboardingResponse> accountForApprovalGrid;

    private ListDataProvider<AccountOwnerOnboardingResponse> forApprovalDataProvider;

    private List<AccountOwnerOnboardingResponse> listOnboarding;


    @Autowired
    final CantonDataService cantonDataService;

    public ListCsdAccountOwnerView(CantonDataService cantonDataService) throws Exception {
        this.cantonDataService = cantonDataService;
        setViewContent(createAccountOwnerContent(), createAccountIdContent() );
        setViewDetailsPosition(Position.BOTTOM);
    }






    private Component createAccountOwnerContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(createAccountOwnerGrid());
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setPadding(Horizontal.RESPONSIVE_X, com.partior.client.ui.layout.size.Top.RESPONSIVE_X);
        return content;
    }

    private Grid createAccountOwnerGrid() throws Exception {

        csdAccountOwnerGrid = new Grid<CSDAccountOwner>();
        csdAccountOwnerList = cantonDataService.listCsdParticipantBanks( "SGCSD");
//cantonDataService.getCdbcOperator()
        csdAccountOwnerDataProvider = DataProvider.ofCollection(csdAccountOwnerList);
        csdAccountOwnerGrid.setDataProvider(csdAccountOwnerDataProvider);

        csdAccountOwnerGrid.addColumn(new ComponentRenderer<>(this::createAccountOwnerInfo))
                .setHeader("BANK NAME");

        csdAccountOwnerGrid.addColumn(CSDAccountOwner::getCurrency)
                .setHeader("CURRENCY");

        List<AccountOwnerResponseDto>  disabledAccounts =cantonDataService
                .listDisabledParticipantBanks(cantonDataService.getCdbcOperator() );

        csdAccountOwnerGrid.addSelectionListener(selection -> {
            Optional<CSDAccountOwner> optionalAccountOwner = selection.getFirstSelectedItem();
            if (optionalAccountOwner.isPresent()) {

                try {
                    listAccountId = cantonDataService.listCsdParticipantBankAccountId(optionalAccountOwner.get().getShortName());
                    accountIdDataProvider = DataProvider.ofCollection(listAccountId);
                    accountIdGrid.setDataProvider(accountIdDataProvider);
                    accountIdDataProvider.refreshAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return csdAccountOwnerGrid;
    }

    private String createParty(AccountOwnerResponseDto accountOwnerResponseDto) {
        return UIUtils.CBDC_NAME.get( cantonDataService.getCdbcOperator() );
    }


    private Component createAccountOwnerInfo(CSDAccountOwner accountOwnerResponseDto) {
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
                accountOwnerResponseDto.getSponsorParty().substring(0,3):accountOwnerResponseDto.getBank();
    }


    private Component createAccountIdContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(createAccountIdGrid());
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setPadding(Horizontal.RESPONSIVE_X, com.partior.client.ui.layout.size.Top.RESPONSIVE_X);
        return content;
    }


    private Grid createAccountIdGrid() throws Exception {
        accountIdGrid = new Grid<CSDAccount>();
        accountIdGrid.addColumn(CSDAccount::getAccountId)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("ACCOUNT ID");

        return accountIdGrid;
    }




}
