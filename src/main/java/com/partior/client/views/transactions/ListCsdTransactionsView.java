package com.partior.client.views.transactions;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.RtgsTransactionResponseProposalDto;
import com.partior.client.ui.components.DataSeriesItemWithRadius;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.layout.size.Bottom;
import com.partior.client.ui.layout.size.Horizontal;
import com.partior.client.ui.layout.size.Right;
import com.partior.client.ui.layout.size.Top;
import com.partior.client.ui.util.*;
import com.partior.client.ui.util.css.BorderRadius;
import com.partior.client.ui.util.css.BoxSizing;
import com.partior.client.ui.util.css.Display;
import com.partior.client.ui.util.css.Shadow;
import com.partior.client.views.MainLayout;
import com.partior.client.views.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@Route(value = "csd-pending-transaction", layout = MainLayout.class)
@PageTitle("RTGS Pending transactions")
@PermitAll
public class ListCsdTransactionsView extends SplitViewFrame {


    private Grid<RtgsTransactionResponseProposalDto> pendingTransactionHistory;
    private ListDataProvider<RtgsTransactionResponseProposalDto> pendingTransactionDataProvider;

    private Grid<RtgsTransactionResponseProposalDto> approvedTransactionHistory;
    private ListDataProvider<RtgsTransactionResponseProposalDto> approvedTransactionDataProvider;



    @Autowired
    final CantonDataService cantonDataService;

    public ListCsdTransactionsView(CantonDataService cantonDataService) throws Exception {
        this.cantonDataService = cantonDataService;
//        setViewContent(createGauges(), createPendingTransactionContent(), createCompletedTransactionContent());
        setViewContent( createPendingTransactionContent(), createCompletedTransactionContent());
        setViewDetailsPosition(Position.BOTTOM);

    }

    private Component createGauges() {
        FlexBoxLayout payments = new FlexBoxLayout(
                createHeader(VaadinIcon.CREDIT_CARD, "Status"),
                createGauge()
        );

        payments.setBoxSizing(BoxSizing.BORDER_BOX);
        payments.setDisplay(Display.BLOCK);
        payments.setMargin(Top.L);
        payments.setMaxWidth( UIUtils.MAX_WIDTH);
        payments.setPadding(Horizontal.RESPONSIVE_L);
        payments.setWidthFull();
        return payments;
    }


    private Component createGauge() {
        Row box = new Row();
        UIUtils.setBackgroundColor(LumoStyles.Color.BASE_COLOR, box);
        UIUtils.setBorderRadius(BorderRadius.S, box);
        UIUtils.setShadow(Shadow.XS, box);

        box.add(boxDetail("PENDING",100, "PENDING"));
        box.add(boxDetail("COMPLETED",0, "COMPLETED"));


        return box;
    }


    private Component createPendingTransactionContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.WALLET, "Pending Transactions"),
                createPendingTransactionGrid()
        );

        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
        content.setMaxWidth( UIUtils.MAX_WIDTH);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();

        return content;
    }

    private Grid createPendingTransactionGrid() throws Exception {

        pendingTransactionHistory = new Grid<>();
        pendingTransactionDataProvider = DataProvider.ofCollection(cantonDataService.listPendingRtgsTransactions(cantonDataService.getCdbcOperator(), cantonDataService.getUserBankName() ));


        pendingTransactionHistory.setDataProvider(pendingTransactionDataProvider);
        pendingTransactionHistory.addColumn(RtgsTransactionResponseProposalDto::getType)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Type")
                .setSortable(true);

        pendingTransactionHistory.addColumn(this::toShortName)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Bank Name")
                .setSortable(true);

        pendingTransactionHistory.addColumn(this::toAccountId)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("To AccountID")
                .setSortable(true);


        pendingTransactionHistory.addColumn(RtgsTransactionResponseProposalDto::getCurrency)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Shares")
                .setSortable(true);

        pendingTransactionHistory.addColumn(RtgsTransactionResponseProposalDto::getAmount)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Quantity")
                .setSortable(true);

        return pendingTransactionHistory;
    }


    private Component createCompletedTransactionContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.WALLET, "Completed Transactions"),
                createCompletedTransactionGrid()
        );

        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
        content.setMaxWidth( UIUtils.MAX_WIDTH);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();

        return content;
    }

    private Grid createCompletedTransactionGrid() throws Exception {

        approvedTransactionHistory = new Grid<>();
        approvedTransactionDataProvider = DataProvider.ofCollection(cantonDataService.listApprovedRtgsTransactions(cantonDataService.getCdbcOperator(), cantonDataService.getUserBankName()));
        approvedTransactionHistory.setDataProvider(approvedTransactionDataProvider);

        approvedTransactionHistory.addColumn(RtgsTransactionResponseProposalDto::getType)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Type")
                .setSortable(true);

        approvedTransactionHistory.addColumn(this::toShortName)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("To Bank")
                .setSortable(true);

        approvedTransactionHistory.addColumn(this::toAccountId)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("To Account")
                .setSortable(true);


        approvedTransactionHistory.addColumn(RtgsTransactionResponseProposalDto::getCurrency)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Shares")
                .setSortable(true);

        approvedTransactionHistory.addColumn(RtgsTransactionResponseProposalDto::getAmount)
                .setAutoWidth(true)
                .setFrozen(true)
                .setHeader("Quantity")
                .setSortable(true);

        return approvedTransactionHistory;
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



    private String toShortName(RtgsTransactionResponseProposalDto depositResponseProposalDto){
        return depositResponseProposalDto.getAccount().getAccountOwner().getShortName();
    }

    private String toAccountId(RtgsTransactionResponseProposalDto depositResponseProposalDto){
        return depositResponseProposalDto.getAccount().getAccountId();
    }


}
