package com.partior.client.views.transactions;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.*;
import com.partior.client.ui.components.DataSeriesItemWithRadius;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.layout.size.*;
import com.partior.client.ui.layout.size.Bottom;
import com.partior.client.ui.layout.size.Top;
import com.partior.client.ui.util.*;
import com.partior.client.ui.util.css.*;
import com.partior.client.views.MainLayout;
import com.partior.client.views.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Data;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;

import static com.partior.client.ui.util.UIUtils.MAX_WIDTH;

@Route(value = "", layout = MainLayout.class)
@PageTitle("AccountOwner List")
@PermitAll
public class ListTrancationsAccountOwnerView extends SplitViewFrame {

    private Grid<DashboardAccountOverviewDto> accountOverviewGrid;
    private Grid<DashboardTransactionHistoryDto> accountIdOverviewGrid;

    private Grid<DashboardTransactionHistoryDto> transactionHistory;

    private ListDataProvider<DashboardAccountOverviewDto> accountOverviewDataProvider;
    private ListDataProvider<DashboardTransactionHistoryDto> accountIdOverviewDataProvider;

    private ListDataProvider<DashboardTransactionHistoryDto> transactionHistoryDataProvider;
    private List<DashboardAccountOverviewDto> dashboardAccountOverviewDtoList;


    private Grid<PvpIntegrationDto> completedPvpGrid;
    private ListDataProvider<PvpIntegrationDto> completedPvpDataProvider;
    private List<PvpIntegrationDto> completedPvpDtoList;


    private Grid<DvpIntegrationDto> completedDvpGrid;
    private ListDataProvider<DvpIntegrationDto> completedDvpDataProvider;
    private List<DvpIntegrationDto> completedDvpDtoList;


    @Autowired
    final CantonDataService cantonDataService;

    public ListTrancationsAccountOwnerView(CantonDataService cantonDataService) throws Exception {
        this.cantonDataService = cantonDataService;
        String userName = cantonDataService.getUserBankName();

        if(cantonDataService.getUserBankName().equals("SGCSD")
                || cantonDataService.getUserBankName().equals("BICSD")){

        } else {

            Component firstRow = createTwoCol(createAccountOverviewContent(), createAccountIdOverviewContent());
            Component secondRow = createTwoCol(createPvpView(), createDvpView());
            Component thirdRow = createOneCol(createTransactionHistoryContent());

            setViewContent(firstRow, secondRow, thirdRow);
            setViewDetailsPosition(Position.BOTTOM);

        }
    }

    private Component createOneCol( Component one) {
        Row docs = new Row(one);
        docs.addClassName(LumoStyles.Margin.Top.XL);
        //  UIUtils.setMaxWidth(MAX_WIDTH, docs);
        docs.setWidthFull();
        return docs;
    }

    private Component createTwoCol( Component one, Component two) {
        Row docs = new Row(one,two);
        docs.addClassName(LumoStyles.Margin.Top.XL);
        docs.setWidthFull();
        return docs;
    }

    private Component createGauges() {
        FlexBoxLayout payments = new FlexBoxLayout(
                createHeader(VaadinIcon.CREDIT_CARD, "Status"),
                createGauge()
        );

        payments.setBoxSizing(BoxSizing.BORDER_BOX);
        payments.setDisplay(Display.BLOCK);
        payments.setMargin(Top.L);
    //    payments.setMaxWidth( UIUtils.MAX_WIDTH);
        payments.setPadding(Horizontal.RESPONSIVE_L);
        payments.setWidthFull();
        return payments;
    }

    private Component createGauge() {
        Row box = new Row();
        UIUtils.setBackgroundColor(LumoStyles.Color.BASE_COLOR, box);
        UIUtils.setBorderRadius(BorderRadius.S, box);
        UIUtils.setShadow(Shadow.XS, box);

        box.add(boxDetail("SUCCESS",100, "SUCCESS"));
        box.add(boxDetail("FAILURE",0, "FAILURE"));


        return box;
    }

    private Component createTransactionHistoryContent() throws Exception {

        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.WALLET, "Transaction History"),
                createTransactionHistoryGrid()
        );

        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
      //  content.setMaxWidth( UIUtils.MAX_WIDTH);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();

        return content;
    }

    private Component createAccountOverviewContent() throws Exception {
        initAccountOverviewGrid();
        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.CREDIT_CARD, "Participant Bank Overview"),
                createAccountOverviewGrid()
        );
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();
        return content;
    }

    private Component createPvpView() throws Exception {
        initAccountOverviewGrid();
        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.CREDIT_CARD, "Completed PvP"),
                createCompletedPvpGrid()
        );
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();
        return content;
    }

    private Component createDvpView() throws Exception {
        initAccountOverviewGrid();
        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.CREDIT_CARD, "Completed DvP"),
                createCompletedDvpGrid()
        );
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();
        return content;
    }


    private Component createAccountIdOverviewContent() throws Exception {

        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.CREDIT_CARD, "CBDC Account Overview"),
                createAccountIdOverviewGrid()
        );

        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
      //  content.setMaxWidth( UIUtils.MAX_WIDTH);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();
        return content;
    }


    private Grid createAccountIdOverviewGrid() throws Exception {

        accountIdOverviewGrid = new Grid<DashboardTransactionHistoryDto>();

        accountIdOverviewDataProvider = DataProvider.ofCollection(
                cantonDataService.listAggregatedAccountIdTransactionHistory(cantonDataService.getCdbcOperator(), dashboardAccountOverviewDtoList) );

        accountIdOverviewGrid.setDataProvider(accountIdOverviewDataProvider);

        accountIdOverviewGrid.addColumn(DashboardTransactionHistoryDto::getAccountId)
                .setFrozen(true)
                .setHeader("Account ID")
                .setSortable(true);


        accountIdOverviewGrid.addColumn(DashboardTransactionHistoryDto::getCurrency)
                .setFrozen(true)
                .setHeader("Currency")
                .setSortable(true);

        accountIdOverviewGrid.addColumn(this::roundAmount)
                .setFrozen(true)
                .setHeader("Account Balance")
                .setSortable(true);


        accountIdOverviewGrid.addSelectionListener(selection -> {

            Optional<DashboardTransactionHistoryDto> optionalAccountId = selection.getFirstSelectedItem();

            if (optionalAccountId.isPresent()) {

                try {

                    transactionHistoryDataProvider = DataProvider.ofCollection(
                            cantonDataService.listCbdcDashboardTransactionHistory(cantonDataService.getCdbcOperator(), optionalAccountId.get().getAccountId() ));


                    transactionHistory.setDataProvider(transactionHistoryDataProvider);
                    transactionHistoryDataProvider.refreshAll();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        return accountIdOverviewGrid;
    }

    private String roundAmount(DashboardTransactionHistoryDto dto){
        return  String.format("%.3f",dto.getQuantity());
    }

    private String formatDeposit(DashboardAccountOverviewDto dto){
        return   String.format("%.3f", Double.valueOf(dto.getDeposit()));
    }

    private String formatWithdrawals(DashboardAccountOverviewDto dto){
        return   String.format("%.3f", Double.valueOf(dto.getWithdrawals()));
    }

    private String formatSent(DashboardAccountOverviewDto dto){
        return   String.format("%.3f",Double.valueOf(dto.getSent()));
    }

    private String formatReceived(DashboardAccountOverviewDto dto){
        return String.format("%.3f",Double.valueOf(dto.getReceived()));
    }

    private void initAccountOverviewGrid() throws Exception {
        accountOverviewGrid = new Grid<>();
        dashboardAccountOverviewDtoList = cantonDataService
                .listCbdcDashboardAccountOverview(cantonDataService.getCdbcOperator());
        accountOverviewDataProvider = DataProvider.ofCollection(dashboardAccountOverviewDtoList);
        accountOverviewGrid.setDataProvider(accountOverviewDataProvider);
    }

    private Grid createAccountOverviewGrid() throws Exception {

        Grid.Column<DashboardAccountOverviewDto> accountType =
        accountOverviewGrid.addColumn(DashboardAccountOverviewDto::getAccountType).setHeader("Participant Type") .setSortable(true);
        accountOverviewGrid.addColumn(DashboardAccountOverviewDto::getBic)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Short Name")
                .setSortable(true);

        accountOverviewGrid.addColumn( this::formatDeposit)
                .setAutoWidth(true)
                .setHeader("Deposit")
                .setSortable(true);

        accountOverviewGrid.addColumn(this::formatWithdrawals)
                .setAutoWidth(true)
                .setHeader("Withdrawals")
                .setSortable(true);

        accountOverviewGrid.addColumn(this::formatSent)
                .setAutoWidth(true)
                .setHeader("Sent")
                .setSortable(true);

        accountOverviewGrid.addColumn(this::formatReceived)
                .setAutoWidth(true)
                .setHeader("Received")
                .setSortable(true);

       accountOverviewGrid.getHeaderRows().clear();
       accountOverviewGrid.addSelectionListener(selection -> {
            Optional<DashboardAccountOverviewDto> optionalAccountOwner = selection.getFirstSelectedItem();

            if (optionalAccountOwner.isPresent()) {
                try {
                    accountIdOverviewDataProvider = DataProvider.ofCollection(cantonDataService
                            .listAggregatedAccountIdTransactionHistory (optionalAccountOwner.get().getBic(), dashboardAccountOverviewDtoList) );
                    accountIdOverviewGrid.setDataProvider(accountIdOverviewDataProvider);
                    accountIdOverviewDataProvider.refreshAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return accountOverviewGrid;
    }

    private Grid createCompletedPvpGrid() throws Exception {

        completedPvpGrid = new Grid<>();
        completedPvpDtoList = cantonDataService.listCompletedPvps(cantonDataService.getUserBankName());
        completedPvpDataProvider = DataProvider.ofCollection(completedPvpDtoList);
        completedPvpGrid.setDataProvider(completedPvpDataProvider);


        completedPvpGrid.addColumn(this::getPvpTradeid)
                .setHeader("TRADEID")
                .setSortable(true);

        completedPvpGrid.addColumn(new ComponentRenderer<>(this::createFromLeg1))
                .setHeader("FROM")
                .setSortable(true);

        completedPvpGrid.addColumn(this::getRates)
                .setHeader("FxRATES")
                .setSortable(true);

        completedPvpGrid.addColumn(new ComponentRenderer<>(this::createFromLeg2))
                .setHeader("TO")
                .setSortable(true);


        completedPvpGrid.addColumn(this::getStatus)
                .setHeader("STATUS")
                .setSortable(true);


        completedPvpGrid.addSelectionListener(selection -> {
            Optional<PvpIntegrationDto> pvpTransactions = selection.getFirstSelectedItem();
            if (pvpTransactions.isPresent()) {

                try {
                  showCompletedPvp(pvpTransactions.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return completedPvpGrid;
    }

    private Grid createCompletedDvpGrid() throws Exception {

        completedDvpGrid = new Grid<>();
        completedDvpDtoList = cantonDataService.listCompletedDvps(cantonDataService.getUserBankName());
        completedDvpDataProvider = DataProvider.ofCollection(completedDvpDtoList);
        completedDvpGrid.setDataProvider(completedDvpDataProvider);


        completedDvpGrid.addColumn(this::getDvpTradeid)
                .setHeader("TRADEID")
                .setSortable(true);

        completedDvpGrid.addColumn(new ComponentRenderer<>(this::createDvpFromLeg1))
                .setHeader("FROM")
                .setSortable(true);

        completedDvpGrid.addColumn(this::getIsin)
                .setHeader("ISIN")
                .setSortable(true);

        completedDvpGrid.addColumn(new ComponentRenderer<>(this::createDvpFromLeg2))
                .setHeader("TO")
                .setSortable(true);

        completedDvpGrid.addColumn(this::getDvpStatus)
                .setHeader("STATUS")
                .setSortable(true);


        completedDvpGrid.addSelectionListener(selection -> {
            Optional<DvpIntegrationDto> dvpTransactions = selection.getFirstSelectedItem();
            if (dvpTransactions.isPresent()) {

                try {
                    showCompletedDvp(dvpTransactions.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return completedDvpGrid;
    }

    private String getIsin(DvpIntegrationDto dvpIntegrationDto){
        return dvpIntegrationDto.getDvp().getLeg2().getIsin();
    }

    private void showCompletedPvp(PvpIntegrationDto pvpTransactions) throws Exception {
       Dialog pvpLegs = new Dialog();
        pvpLegs.setWidth(80, Unit.PERCENTAGE);
        pvpLegs.getElement().setAttribute("aria-label", "Pvp Legs");
        VerticalLayout dialogLayout  = createFundAgreementDialogLayout(pvpLegs,pvpTransactions);
        pvpLegs.add(dialogLayout);
        pvpLegs.open();
    }

    private void showCompletedDvp(DvpIntegrationDto pvpTransactions) throws Exception {
        Dialog pvpLegs = new Dialog();
        pvpLegs.setWidth(80, Unit.PERCENTAGE);
        pvpLegs.getElement().setAttribute("aria-label", "Pvp Legs");
        VerticalLayout dialogLayout  = createDvpFundAgreementDialogLayout(pvpLegs,pvpTransactions);
        pvpLegs.add(dialogLayout);
        pvpLegs.open();
    }


    private  VerticalLayout createFundAgreementDialogLayout(Dialog dialog, PvpIntegrationDto pvpIntegrationDto) throws Exception {

        Grid<PvPLeg> gridLeg = new Grid<>();
        List<PvPLeg> pvpLegs = new ArrayList<>();
        pvpLegs.add(pvpIntegrationDto.getPvp().getLeg1());
        pvpLegs.add(pvpIntegrationDto.getPvp().getLeg2());


        DataProvider legsProvider = DataProvider.ofCollection(pvpLegs);
        gridLeg.setDataProvider(legsProvider);

        gridLeg.addColumn(this::fromLegShortName)
                .setHeader("FROM");

        gridLeg.addColumn(this::pvpDetailCurrency)
                .setHeader("CURRENCY");


        gridLeg.addColumn(this::pvpDetailAmount)
                .setHeader("AMOUNT");

        gridLeg.addColumn(this::toLegShortName)
                .setHeader("TO");

        H3 h1 = new H3("PVP Leg");

        VerticalLayout dialogLayout = new VerticalLayout(h1,gridLeg);

        dialogLayout.setSpacing(false);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.START);


        return dialogLayout;

    }

    private  VerticalLayout createDvpFundAgreementDialogLayout(Dialog dialog, DvpIntegrationDto pvpIntegrationDto) throws Exception {

//        private DvPCBDCLeg leg1;
  //      private DvPCSDLeg leg2;

        Grid gridLeg = new Grid<>();
        List pvpLegs = new ArrayList<>();

        pvpLegs.add(pvpIntegrationDto.getDvp().getLeg1());
        pvpLegs.add(pvpIntegrationDto.getDvp().getLeg2());


        DataProvider legsProvider = DataProvider.ofCollection(pvpLegs);
        gridLeg.setDataProvider(legsProvider);

        gridLeg.addColumn(this::fromDvpLegShortName).setHeader("FROM");

        gridLeg.addColumn(this::dvpIsinCurrency).setHeader("ISIN");

        gridLeg.addColumn(this::dvpAmountQuantity).setHeader("QUANTITY");

        gridLeg.addColumn(this::toDvpLegShortName).setHeader("TO");

        H3 h1 = new H3("DVP Leg");

        VerticalLayout dialogLayout = new VerticalLayout(h1,gridLeg);

        dialogLayout.setSpacing(false);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.START);

        return dialogLayout;

    }


    private String fromLegShortName(PvPLeg pvpLeg){
        return pvpLeg.getFromAccount().getAccountOwner().getShortName();
    }

    private String toLegShortName(PvPLeg pvpLeg){
        return pvpLeg.getToAccount().getAccountOwner().getShortName();
    }

    private String pvpDetailCurrency(PvPLeg pvpLeg){
        return pvpLeg.getFromAccount().getAccountOwner().getCurrency();
    }

    private String pvpDetailAmount(PvPLeg pvpLeg){
        return String.valueOf(pvpLeg.getAmount());
    }

    private String getStatus(PvpIntegrationDto pvpIntegrationDto){
        return pvpIntegrationDto.getPvp().getStatus();
    }

    private String getDvpStatus(DvpIntegrationDto dvpIntegrationDto){
        return dvpIntegrationDto.getDvp().getStatus();
    }

    private String fromDvpLegShortName(Object leg){
        if(leg instanceof  DvPCBDCLeg){
            return ((DvPCBDCLeg)leg).getFromAccount().getAccountOwner().getShortName();
        } else if(leg instanceof  DvPCSDLeg){
            return ((DvPCSDLeg)leg).getFromAccount().getAccountOwner().getShortName();
        }
        return "-";
    }

    private String toDvpLegShortName(Object leg){
        if(leg instanceof  DvPCBDCLeg){
            return ((DvPCBDCLeg)leg).getToAccount().getAccountOwner().getShortName();
        } else if(leg instanceof  DvPCSDLeg){
            return ((DvPCSDLeg)leg).getToAccount().getAccountOwner().getShortName();
        }
        return "-";
    }

    private String dvpAmountQuantity(Object leg){
        if(leg instanceof  DvPCBDCLeg){
            return  String.valueOf ( ((DvPCBDCLeg)leg).getAmount());
        } else if(leg instanceof  DvPCSDLeg){
            return  String.valueOf (((DvPCSDLeg)leg).getQuantity());
        }
        return "-";
    }

    private String dvpIsinCurrency(Object leg){
        if(leg instanceof  DvPCBDCLeg){
            return  String.valueOf ( ((DvPCBDCLeg)leg).getFromAccount().getAccountOwner().getCurrency() );
        } else if(leg instanceof  DvPCSDLeg){
            return  String.valueOf (((DvPCSDLeg)leg).getIsin());
        }
        return "-";
    }

    private String getRates(PvpIntegrationDto pvpIntegrationDto){
        return pvpIntegrationDto.getPvp().getRates();
    }
    private boolean matchesTerm(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || value
                .toLowerCase().contains(searchTerm.toLowerCase());
    }
    private Grid createTransactionHistoryGrid() throws Exception {

        transactionHistory = new Grid<>();
        transactionHistoryDataProvider = DataProvider.ofCollection(cantonDataService
                .listCbdcDashboardTransactionHistory(cantonDataService.getCdbcOperator(), null ));


        transactionHistory.setDataProvider(transactionHistoryDataProvider);
        transactionHistory.addColumn(DashboardTransactionHistoryDto::getAccountId)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Account ID")
                .setSortable(true);

        transactionHistory.addColumn(DashboardTransactionHistoryDto::getTxnType)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Type")
                .setSortable(true);

        transactionHistory.addColumn(DashboardTransactionHistoryDto::getPayor)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Payor")
                .setSortable(true);

        transactionHistory.addColumn(DashboardTransactionHistoryDto::getPayee)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Payee")
                .setSortable(true);

        transactionHistory.addColumn(DashboardTransactionHistoryDto::getQuantity)
                .setAutoWidth(true)
                .setHeader("AMOUNT")
                .setSortable(true);



        transactionHistory.addColumn(DashboardTransactionHistoryDto::getStatus)
                .setAutoWidth(true)
                .setHeader("Status")
                .setSortable(true);

        transactionHistory.addColumn(DashboardTransactionHistoryDto::getTimeStamp)
                .setAutoWidth(true)
                .setHeader("Timestamp")
                .setSortable(true);
        return transactionHistory;
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




    private Component boxDetail(String label, int value,String status) {

        FlexBoxLayout textContainer = new FlexBoxLayout(
                UIUtils.createH2Label(Integer.toString(value)),
                UIUtils.createLabel(FontSize.S, "%"));

        textContainer.setAlignItems(FlexComponent.Alignment.BASELINE);
        textContainer.setPosition(com.partior.client.ui.util.css.Position.ABSOLUTE);
        textContainer.setSpacing(Right.XS);

        Chart chart = createProgressChart(status, value);

        FlexBoxLayout chartContainer = new FlexBoxLayout(chart, textContainer);
        chartContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        chartContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        chartContainer.setPosition(com.partior.client.ui.util.css.Position.RELATIVE);
        chartContainer.setHeight("120px");
        chartContainer.setWidth("120px");


        FlexBoxLayout paymentChart = new FlexBoxLayout(new Label(label), chartContainer);

        //    paymentChart.addClassName(CLASS_NAME + "__payment-chart");
        paymentChart.setAlignItems(FlexComponent.Alignment.CENTER);
        paymentChart.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        paymentChart.setPadding(Bottom.S, Top.M);
        return paymentChart;
    }

    private Chart createProgressChart(String status, int value) {
        Chart chart = new Chart();
        chart.addClassName(status.toLowerCase());
        chart.setSizeFull();

        Configuration configuration = chart.getConfiguration();
        configuration.getChart().setType(ChartType.SOLIDGAUGE);
        configuration.setTitle("");
        configuration.getTooltip().setEnabled(false);

        configuration.getyAxis().setMin(0);
        configuration.getyAxis().setMax(100);
        configuration.getyAxis().getLabels().setEnabled(false);

        PlotOptionsSolidgauge opt = new PlotOptionsSolidgauge();
        opt.getDataLabels().setEnabled(false);
        configuration.setPlotOptions(opt);

        DataSeriesItemWithRadius point = new DataSeriesItemWithRadius();
        point.setY(value);
        point.setInnerRadius("100%");
        point.setRadius("110%");
        configuration.setSeries(new DataSeries(point));

        Pane pane = configuration.getPane();
        pane.setStartAngle(0);
        pane.setEndAngle(360);

        Background background = new Background();
        background.setShape(BackgroundShape.ARC);
        background.setInnerRadius("100%");
        background.setOuterRadius("110%");
        pane.setBackground(background);

        return chart;
    }

    private  Component createFilterSection(String labelText) {

        GridListDataView<DashboardAccountOverviewDto> dataView = accountOverviewGrid.setItems(accountOverviewDataProvider);
        AccountOwnerTransactionOverviewFilter accountOwnerTransactionOverviewFilter = new AccountOwnerTransactionOverviewFilter(dataView);

        Consumer<String> filterChangeConsumer = accountOwnerTransactionOverviewFilter::setAccountType;


        ComboBox<String> accountType = new ComboBox("Participant Type");
        accountType.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
        accountType.setClearButtonVisible(true);
        accountType.setItems(Arrays.asList("FOREIGN","DOMESTIC"));



        accountType.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));

        Hr hr = new Hr();


        FormLayout formLayout = new FormLayout(  accountType);

        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if the layout's width exceeds 320px
                new FormLayout.ResponsiveStep("320px", 2),
                // Use three columns, if the layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 3)
        );



        formLayout.setColspan(accountType, 1);
        formLayout.getStyle().set("margin", "100 100 100 100");




        return formLayout;


    }

    private static class AccountOwnerTransactionOverviewFilter {
        private final GridListDataView<DashboardAccountOverviewDto> dataView;
        private String accountType;


        public AccountOwnerTransactionOverviewFilter(GridListDataView<DashboardAccountOverviewDto> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
            this.dataView.refreshAll();
        }

        public boolean test(DashboardAccountOverviewDto filter) {
            boolean matchesTransactionType = matches(filter.getAccountType(), accountType);
            //return matchesFullName && matchesEmail && matchesProfession;
            return matchesTransactionType;

        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty() || value
                    .toLowerCase().contains(searchTerm.toLowerCase());
        }
    }


    private String getPvpTradeid(PvpIntegrationDto pvpIntegrationDto){
        return pvpIntegrationDto.getPvp().getTradeId();
    }

    private String getDvpTradeid(DvpIntegrationDto dvpIntegrationDto){
        return dvpIntegrationDto.getDvp().getTradeId();
    }

    private Component createDvpFromLeg1(DvpIntegrationDto pvpIntegrationDto) {
        com.partior.client.ui.components.ListItem item = new com.partior.client.ui.components.ListItem(
                pvpIntegrationDto.getDvp().getLeg1().getFromAccount().getAccountOwner().getShortName() ,
                pvpIntegrationDto.getDvp().getLeg1().getFromAccount().getAccountId(),
                pvpIntegrationDto.getDvp().getLeg1().getFromAccount().getAccountOwner().getCurrency(),
                String.valueOf( pvpIntegrationDto.getDvp().getLeg1().getAmount())
        );
        return item;
    }

    private Component createDvpFromLeg2(DvpIntegrationDto pvpIntegrationDto) {
        com.partior.client.ui.components.ListItem item = new com.partior.client.ui.components.ListItem(
                pvpIntegrationDto.getDvp().getLeg2().getFromAccount().getAccountOwner().getShortName() ,
                pvpIntegrationDto.getDvp().getLeg2().getFromAccount().getAccountId(),
                pvpIntegrationDto.getDvp().getLeg2().getIsin(),
                String.valueOf( pvpIntegrationDto.getDvp().getLeg2().getQuantity())
        );
        return item;
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
}
