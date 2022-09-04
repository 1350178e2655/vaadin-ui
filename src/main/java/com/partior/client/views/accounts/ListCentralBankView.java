package com.partior.client.views.accounts;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.AccountOwnerResponseDto;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.layout.size.Horizontal;
import com.partior.client.ui.layout.size.Right;
import com.partior.client.ui.layout.size.Vertical;
import com.partior.client.ui.util.css.BoxSizing;
import com.partior.client.views.MainLayout;
import com.partior.client.views.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@Route(value = "list_central_bank", layout = MainLayout.class)
@PageTitle("Central Bank List")
@PermitAll
public class ListCentralBankView extends SplitViewFrame {

    private Grid<AccountOwnerResponseDto> grid;
    private ListDataProvider<AccountOwnerResponseDto> dataProvider;

    @Autowired
    final CantonDataService cantonDataService;

    public ListCentralBankView(CantonDataService cantonDataService) throws Exception {
        this.cantonDataService = cantonDataService;
        setViewContent(createContent());
        setViewDetailsPosition(Position.BOTTOM);
    }

    private Component createContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(createGrid());
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, com.partior.client.ui.layout.size.Top.RESPONSIVE_X);
        return content;
    }

    private Grid createGrid() throws Exception {
        grid = new Grid<>();
        dataProvider = DataProvider.ofCollection(cantonDataService.listAccountOwners(cantonDataService.getCdbcOperator()));
        grid.setDataProvider(dataProvider);
        grid.setSizeFull();

//        grid.addColumn(AccountOwnerResponseDto::getBank)
//                .setAutoWidth(true)
//                .setFlexGrow(0)
//                .setFrozen(true)
//                .setHeader("Bank")
//                .setSortable(true);

        grid.addColumn(new ComponentRenderer<>(this::createAccountOwnerInfo))
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Bic");

        grid.addColumn(AccountOwnerResponseDto::getCurrency)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Currency")
                .setSortable(true);


        grid.addColumn(  this::sponsor)
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setHeader("Sponsor")
                .setSortable(true);

        grid.addColumn( this::party)
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setHeader("CBDC")
                .setSortable(true);

        grid.addColumn(this::createType)
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setHeader("Type")
                .setTextAlign(ColumnTextAlign.END);

//        grid.addColumn(new ComponentRenderer<>(this::createEnabled))
//                .setAutoWidth(true)
//                .setFlexGrow(0)
//                .setHeader("Type")
//                .setTextAlign(ColumnTextAlign.END);
//
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


    private Component createAccountOwnerInfo(AccountOwnerResponseDto AccountOwnerResponseDto) {

        com.partior.client.ui.components.ListItem item = new com.partior.client.ui.components.ListItem(
                new com.partior.client.ui.components.Initials( AccountOwnerResponseDto.getBic().substring(0,1)),
                AccountOwnerResponseDto.getBic(),
                AccountOwnerResponseDto.getRtgsAccountId());
        item.setPadding(Vertical.XS);
        item.setSpacing(Right.M);
        return item;
    }

    private String createType(AccountOwnerResponseDto AccountOwnerResponseDto ) {
        return AccountOwnerResponseDto.isLocal() ? "Local" : "Foreign";
    }

    private String party(AccountOwnerResponseDto AccountOwnerResponseDto){
        return AccountOwnerResponseDto.getSponsorParty().substring(0,3);
    }

    private String sponsor(AccountOwnerResponseDto AccountOwnerResponseDto){
        return AccountOwnerResponseDto.getSponsorParty().substring(0,3);
    }







}
