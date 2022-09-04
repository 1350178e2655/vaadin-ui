package com.partior.client.views.transactions;

import com.partior.client.data.entity.Payment;
import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.AccountOwnerResponseDto;
import com.partior.client.dto.TransactionResponseDto;
import com.partior.client.ui.components.DataSeriesItemWithRadius;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.components.detailsdrawer.DetailsDrawer;
import com.partior.client.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.partior.client.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.partior.client.ui.layout.size.*;
import com.partior.client.ui.layout.size.Bottom;
import com.partior.client.ui.layout.size.Top;
import com.partior.client.ui.util.*;
import com.partior.client.ui.util.css.*;
import com.partior.client.ui.util.css.Position;
import com.partior.client.views.MainLayout;
import com.partior.client.views.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import javax.annotation.security.PermitAll;
import java.util.Arrays;

import static com.partior.client.ui.util.UIUtils.MAX_WIDTH;

@Route(value = "transactions_account_id", layout = MainLayout.class)
@PageTitle("AccountOwner List")
@PermitAll
public class ListTrancationsAccountIdView extends SplitViewFrame {

    private Grid<AccountOwnerResponseDto> grid;
    private ListDataProvider<AccountOwnerResponseDto> dataProvider;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;

    private final Binder<AccountOwnerResponseDto> binder;

    private AccountOwnerResponseDto AccountOwnerResponseDto;


    private TextField bic;
    private RadioButtonGroup<String> isLocal;
    private TextField sponsorParty;
    private TextField centralBankParty;
    private ComboBox currency;


    @Autowired
    final CantonDataService cantonDataService;

    public ListTrancationsAccountIdView(CantonDataService cantonDataService) throws Exception {

        this.cantonDataService = cantonDataService;
        setViewContent(createContent());
        setViewDetails(createDetailsDrawer());
        setViewDetailsPosition(Position.BOTTOM);
        binder = new BeanValidationBinder<>(AccountOwnerResponseDto.class);


        //  filter();
    }

    private Component createContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(accountOwnerGrid());
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, com.partior.client.ui.layout.size.Top.RESPONSIVE_X);
        return content;
    }

    private Component createTransactionFlowBoxes() {
        FlexBoxLayout payments = new FlexBoxLayout(
                createHeader(VaadinIcon.CREDIT_CARD, "Payments"),
                createTransactionFlows()
        );

        payments.setBoxSizing(BoxSizing.BORDER_BOX);
        payments.setDisplay(Display.BLOCK);
        payments.setMargin(Top.L);
       // payments.setMaxWidth(MAX_WIDTH);
        payments.setPadding(Horizontal.RESPONSIVE_L);
        payments.setWidthFull();
        return payments;
    }

    private Component createTransactionFlows() {
        Row charts = new Row();
        UIUtils.setBackgroundColor(LumoStyles.Color.BASE_COLOR, charts);
        UIUtils.setBorderRadius(BorderRadius.S, charts);
        UIUtils.setShadow(Shadow.XS, charts);

        for (Payment.Status status : Payment.Status.values()) {
            charts.add(createTransactionFlowChart(status));
        }

        return charts;
    }

    private Component createTransactionFlowChart(Payment.Status status) {
        int value;

        switch (status) {
            case PENDING:
                value = 24;
                break;

            case SUBMITTED:
                value = 40;
                break;

            case CONFIRMED:
                value = 32;
                break;

            default:
                value = 4;
                break;
        }

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

        FlexBoxLayout paymentChart = new FlexBoxLayout(new Label(status.getName()), chartContainer);
        paymentChart.addClassName("CLASS_NAME" + "__payment-chart");
        paymentChart.setAlignItems(FlexComponent.Alignment.CENTER);
        paymentChart.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        paymentChart.setPadding(Bottom.S, Top.M);
        return paymentChart;

    }

    private Chart createProgressChart(Payment.Status status, int value) {
        Chart chart = new Chart();
        chart.addClassName(status.getName().toLowerCase());
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
    private FlexBoxLayout createHeader(VaadinIcon icon, String title) {
        FlexBoxLayout header = new FlexBoxLayout(
                UIUtils.createIcon(IconSize.M, TextColor.TERTIARY, icon),
                UIUtils.createH3Label(title));
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setMargin(Bottom.L, Horizontal.RESPONSIVE_L);
        header.setSpacing(Right.L);
        return header;
    }

    private Grid accountOwnerGrid() throws Exception {
        grid = new Grid<>();
        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(this::showDetails));
        dataProvider = DataProvider.ofCollection(cantonDataService.listAccountOwners(cantonDataService.getCdbcOperator() ));
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

            grid.addColumn( com.partior.client.dto.AccountOwnerResponseDto::getCurrency)
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

    private Grid accountOwnerAccountGrid() throws Exception {
        grid = new Grid<>();
        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(this::showDetails));
        dataProvider = DataProvider.ofCollection(cantonDataService.listAccountOwners(cantonDataService.getCdbcOperator() ));
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

        grid.addColumn(com.partior.client.dto.AccountOwnerResponseDto::getCurrency)
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

    private Component createEnabled(AccountOwnerResponseDto AccountOwnerResponseDto ) {
        Icon icon;
        if (AccountOwnerResponseDto.isLocal()) {
            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
        } else {
            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
        }
        return icon;
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

    private DetailsDrawer createDetailsDrawer() {
        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);

        // Header
        detailsDrawerHeader = new DetailsDrawerHeader("");
        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
        detailsDrawer.setHeader(detailsDrawerHeader);

        // Footer
        DetailsDrawerFooter footer = new DetailsDrawerFooter();
        footer.addSaveListener(e -> {
            detailsDrawer.hide();
            binder.writeBeanIfValid(this.AccountOwnerResponseDto);

//            TransactionResponseDto transactionResponseDto
//                    = cantonDataService.onboardAccountOwner( this.AccountOwnerResponseDto );
//
//            transactionResponseDto.getTransactionId();

//            if( Integer.parseInt(transactionResponseDto.getTransactionId()) > 0){
//                UIUtils.showNotification("Changes saved.");
//            } else {
//                UIUtils.showNotification("Something went wrong");
//            }

        });
        footer.addCancelListener(e -> detailsDrawer.hide());
        detailsDrawer.setFooter(footer);

        return detailsDrawer;
    }

    private void showDetails(AccountOwnerResponseDto AccountOwnerResponseDto) {
        detailsDrawerHeader.setTitle(AccountOwnerResponseDto.getBic());
        detailsDrawer.setContent(createFormDetails(AccountOwnerResponseDto));
        detailsDrawer.show();
    }

    private FormLayout createFormDetails(AccountOwnerResponseDto AccountOwnerResponseDto) {
        this.AccountOwnerResponseDto = AccountOwnerResponseDto;

//        bankName = new TextField();
//        bankName.setValue(AccountOwnerResponseDto.getBank());
//        bankName.setWidthFull();

        bic = new TextField();
        bic.setValue(AccountOwnerResponseDto.getBic());
        bic.setWidthFull();

        isLocal = new RadioButtonGroup<>();
        isLocal.setItems("True", "False");
        isLocal.setValue(AccountOwnerResponseDto.isLocal() ? Boolean.TRUE.toString() : Boolean.FALSE.toString());



        sponsorParty = new TextField();
        sponsorParty.setValue(AccountOwnerResponseDto.getSponsorParty());
        sponsorParty.setWidthFull();

        centralBankParty = new TextField();
        centralBankParty.setValue(AccountOwnerResponseDto.getCentralBankParty());
        centralBankParty.setWidthFull();

        currency = new ComboBox();
        currency.setItems(Arrays.asList("SGD", "USD"));
        currency.setValue(Arrays.asList("SGD", "USD"));
        currency.setWidthFull();

        binder.forField(bic).bind("bic");
        binder.forField(isLocal).bind("local");
        binder.forField(sponsorParty).bind("sponsorParty");
        binder.forField(centralBankParty).bind("centralBankParty");
        binder.forField(currency).bind("currency");
        binder.bindInstanceFields(this);


        // Form layout
        FormLayout form = new FormLayout();
        form.addClassNames(LumoStyles.Padding.Bottom.L,
                LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("1024px", 3,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP));
        form.addFormItem(bic, "BIC");
        form.addFormItem(isLocal, "Local");
        form.addFormItem(sponsorParty, "Sponsor");
        form.addFormItem(centralBankParty, "CBDC");
        form.addFormItem(currency, "Currency");
//        form.addFormItem(new Upload(), "Image");
        return form;
    }


}
