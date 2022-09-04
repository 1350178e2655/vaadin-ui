package com.partior.client.views.onboarding;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.AccountOwnerOnboardingRequest;
import com.partior.client.dto.AccountOwnerOnboardingResponse;
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
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.util.List;

@Route(value = "approval_account_owner", layout = MainLayout.class)
@PageTitle("Account Owner Approval")
@PermitAll
public class AccountOwnerApprovalView extends SplitViewFrame {

    private Grid<AccountOwnerOnboardingResponse> grid;
    private ListDataProvider<AccountOwnerOnboardingResponse> dataProvider;

    private List<AccountOwnerOnboardingResponse> listOnboarding;
    private List<AccountOwnerOnboardingResponse> listRejected;
    private List<AccountOwnerOnboardingResponse> listApproved;


    @Autowired
    final CantonDataService cantonDataService;

    public AccountOwnerApprovalView(CantonDataService cantonDataService) throws Exception {
        this.cantonDataService = cantonDataService;
        setViewContent(createAccountOwnerApprovalContent());
        setViewDetailsPosition(Position.BOTTOM);
    }

    private Component createAccountOwnerApprovalContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(createAccountOwnerApprovalGrid());
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, com.partior.client.ui.layout.size.Top.RESPONSIVE_X);
        return content;
    }

    private Grid createAccountOwnerApprovalGrid() throws Exception {

        grid = new Grid<>();
        listOnboarding = cantonDataService.listAccountOwnerOnboardingRequest(cantonDataService.getCdbcOperator() );
        dataProvider = DataProvider.ofCollection(listOnboarding);
        grid.setDataProvider(dataProvider);
        grid.setSizeFull();


        grid.addColumn(new ComponentRenderer<>(this::createAccountOwnerInfo))
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("BankName");


        grid.addColumn( this::createParty  )
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setHeader("CBDC")
                .setSortable(true);


        grid.addColumn(AccountOwnerOnboardingResponse::getCurrency)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Currency")
                .setSortable(true);


        grid.addColumn(this::createType)
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setHeader("Type")
                .setTextAlign(ColumnTextAlign.END);

        grid.addComponentColumn((accountOwnerResponseDto) -> {
            Button approve = new Button("APPROVE");

            Button reject = new Button("REJECT");

            approve.addClickListener(
                event ->{
                    cantonDataService.approveOnnboardingRequest(
                            new AccountOwnerOnboardingRequest(accountOwnerResponseDto.getShortName(),
                                    accountOwnerResponseDto.getCentralBank())
                    );
                }
            );

            reject.addClickListener(
                    event ->{
                        cantonDataService.rejectOnnboardingRequest(
                                new AccountOwnerOnboardingRequest(accountOwnerResponseDto.getShortName(),
                                        accountOwnerResponseDto.getCentralBank())
                        );
                    }
            );

             HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setSpacing(true);
            horizontalLayout.add( reject, approve);
            return horizontalLayout;
        }).setHeader("Enable");

//        LitRenderer<AccountOwnerResponseDto> importantRenderer = LitRenderer.<AccountOwnerResponseDto>of(
//                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
//        .withProperty("icon", enable -> enable.isEnable() ? "check" : "minus").withProperty("color",
//                enable -> enable.isEnable()
//                        ? "var(--lumo-primary-text-color)"
//                        : "var(--lumo-disabled-text-color)");
//
//        grid.addColumn(importantRenderer).setHeader("Enable").setAutoWidth(true);


     //   grid.setSelectionMode(Grid.SelectionMode.MULTI);

//        grid.addColumn(new ComponentRenderer<>(this::createApprovalLimit))
//                .setAutoWidth(true)
//                .setFlexGrow(0)
//                .setHeader("Approval Limit ($)")
//                .setTextAlign(ColumnTextAlign.END);
//        grid.addColumn(new ComponentRenderer<>(this::createDate))
//                .setAutoWidth(true)
//                .setFlexGrow(0)
//                .setHeader("Last Report")
//                .setTextAlign(ColumnTextAlign.END);

        return grid;
    }

    private String createParty(AccountOwnerOnboardingResponse accountOwnerOnbaordingResponse) {
        return UIUtils.CBDC_NAME.get( cantonDataService.getCdbcOperator() );
    }


    private Component createAccountOwnerInfo(AccountOwnerOnboardingResponse accountOwnerOnbaordingResponse) {

        com.partior.client.ui.components.ListItem item = new com.partior.client.ui.components.ListItem(
                new com.partior.client.ui.components.Initials( accountOwnerOnbaordingResponse.getBic().substring(0,1)),
                accountOwnerOnbaordingResponse.getShortName() );

        item.setPadding(Vertical.XS);
        item.setSpacing(Right.M);
        return item;
    }

    private String createType(AccountOwnerOnboardingResponse accountOwnerResponseDto ) {
        return accountOwnerResponseDto.getIsLocal() ? "Local" : "Foreign";
    }


}
