package com.partior.client.views.transfers;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.*;
import com.partior.client.ui.components.DataSeriesItemWithRadius;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.layout.size.Bottom;
import com.partior.client.ui.layout.size.Top;
import com.partior.client.ui.layout.size.*;
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

@Route(value = "balances_view_csd", layout = MainLayout.class)
@PageTitle("CSD Balances List")
@PermitAll
public class BalanceViewCsd extends SplitViewFrame {

    private Grid<CsdBalanceResponseDto> accountOverviewGrid;
    private ListDataProvider<CsdBalanceResponseDto> balanceAccountOverviewDataProvider;




    @Autowired
    final CantonDataService cantonDataService;

    public BalanceViewCsd(CantonDataService cantonDataService) throws Exception {
        this.cantonDataService = cantonDataService;
        setViewContent( createAccountOverviewContent());
        setViewDetailsPosition(Position.BOTTOM);

    }




    private Component createAccountOverviewContent() throws Exception {


        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.CREDIT_CARD, "Account Balance Overview"),
                createAccountIdOverviewGrid()
        );

        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);

        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();
        return content;
    }

    private Grid createAccountIdOverviewGrid() throws Exception {
        accountOverviewGrid = new Grid<>();

        balanceAccountOverviewDataProvider = DataProvider.ofCollection(
                cantonDataService.getCsdBalances( new CsdBalanceRequestDto( cantonDataService.getUserBankName()  )));

        accountOverviewGrid.setDataProvider(balanceAccountOverviewDataProvider);

//
//        accountOverviewGrid.addColumn(cantonDataService.getUserBankName())
//                .setHeader("Bank Name")
//                .setSortable(true);

        accountOverviewGrid.addColumn(CsdBalanceResponseDto::getAccountId)
                .setHeader("CSD Account ID")
                .setSortable(true);

        accountOverviewGrid.addColumn(CsdBalanceResponseDto::getIsin)
                .setHeader("Instrument")
                .setSortable(true);

        accountOverviewGrid.addColumn(CsdBalanceResponseDto::getQuantity)
                .setHeader("Quantity")
                .setSortable(true);

        return accountOverviewGrid;
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
