//package com.partior.client.views.accounts;
//
//import com.partior.client.data.service.CantonDataService;
//import com.partior.client.dto.AccountOwnerDto;
//import com.partior.client.dto.AccountOwnerResponseDto;
//import com.partior.client.dto.AccountOwnerResponseDto;
//import com.partior.client.dto.TransactionResponseDto;
//import com.partior.client.ui.components.FlexBoxLayout;
//import com.partior.client.ui.components.detailsdrawer.DetailsDrawer;
//import com.partior.client.ui.components.detailsdrawer.DetailsDrawerFooter;
//import com.partior.client.ui.components.detailsdrawer.DetailsDrawerHeader;
//import com.partior.client.ui.layout.size.Horizontal;
//import com.partior.client.ui.layout.size.Right;
//import com.partior.client.ui.layout.size.Vertical;
//import com.partior.client.ui.util.LumoStyles;
//import com.partior.client.ui.util.UIUtils;
//import com.partior.client.ui.util.css.BoxSizing;
//import com.partior.client.views.MainLayout;
//import com.partior.client.views.SplitViewFrame;
//import com.vaadin.flow.component.Component;
//import com.vaadin.flow.component.combobox.ComboBox;
//import com.vaadin.flow.component.formlayout.FormLayout;
//import com.vaadin.flow.component.grid.ColumnTextAlign;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.component.icon.Icon;
//import com.vaadin.flow.component.icon.VaadinIcon;
//import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
//import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.data.binder.BeanValidationBinder;
//import com.vaadin.flow.data.binder.Binder;
//import com.vaadin.flow.data.provider.DataProvider;
//import com.vaadin.flow.data.provider.ListDataProvider;
//import com.vaadin.flow.data.renderer.ComponentRenderer;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//
//import javax.annotation.security.PermitAll;
//import java.util.Arrays;
//
//@Route(value = "accoounts_account_id", layout = MainLayout.class)
//@PageTitle("AccountOwner List")
//@PermitAll
//public class ListAccountIdView extends SplitViewFrame {
//
//    private Grid<AccountOwnerDto> grid;
//    private ListDataProvider<AccountOwnerDto> dataProvider;
//
//    private DetailsDrawer detailsDrawer;
//    private DetailsDrawerHeader detailsDrawerHeader;
//
//    private final Binder<AccountOwnerDto> binder;
//
//    private AccountOwnerDto accountOwnerDto;
//
//
//    private TextField bic;
//    private RadioButtonGroup<String> isLocal;
//    private TextField sponsorParty;
//    private TextField centralBankParty;
//    private ComboBox currency;
//
//
//    @Autowired
//    final CantonDataService cantonDataService;
//
//    public ListAccountIdView(CantonDataService cantonDataService) throws Exception {
//
//        this.cantonDataService = cantonDataService;
//        setViewContent(createContent());
//        setViewDetails(createDetailsDrawer());
//        setViewDetailsPosition(Position.BOTTOM);
//
//        binder = new BeanValidationBinder<>(AccountOwnerDto.class);
//
//
//        //  filter();
//    }
//
//    private Component createContent() throws Exception {
//        FlexBoxLayout content = new FlexBoxLayout(createGrid());
//        content.setBoxSizing(BoxSizing.BORDER_BOX);
//        content.setHeightFull();
//        content.setPadding(Horizontal.RESPONSIVE_X, com.partior.client.ui.layout.size.Top.RESPONSIVE_X);
//        return content;
//    }
//
//    private Grid createGrid() throws Exception {
//        grid = new Grid<>();
//        grid.addSelectionListener(event -> event.getFirstSelectedItem()
//                .ifPresent(this::showDetails));
//
//        dataProvider = DataProvider.ofCollection(cantonDataService.listAccountOwners("mas"));
//        grid.setDataProvider(dataProvider);
//        grid.setSizeFull();
//
////        grid.addColumn(AccountOwnerResponseDto::getBank)
////                .setAutoWidth(true)
////                .setFlexGrow(0)
////                .setFrozen(true)
////                .setHeader("Bank")
////                .setSortable(true);
//
//        grid.addColumn(new ComponentRenderer<>(this::createAccountOwnerInfo))
//                .setAutoWidth(true)
//                .setFrozen(true)
//                .setHeader("Bic");
//
//        grid.addColumn(com.partior.client.dto.AccountOwnerResponseDto::getCurrency)
//                .setAutoWidth(true)
//                .setFrozen(true)
//                .setHeader("Currency")
//                .setSortable(true);
//
//
//        grid.addColumn(  this::sponsor)
//                .setAutoWidth(true)
//                .setFlexGrow(0)
//                .setHeader("Sponsor")
//                .setSortable(true);
//
//        grid.addColumn( this::party)
//                .setAutoWidth(true)
//                .setFlexGrow(0)
//                .setHeader("CBDC")
//                .setSortable(true);
//
//        grid.addColumn(this::createType)
//                .setAutoWidth(true)
//                .setFlexGrow(0)
//                .setHeader("Type")
//                .setTextAlign(ColumnTextAlign.END);
//
////        grid.addColumn(new ComponentRenderer<>(this::createEnabled))
////                .setAutoWidth(true)
////                .setFlexGrow(0)
////                .setHeader("Type")
////                .setTextAlign(ColumnTextAlign.END);
////
////        grid.addColumn(new ComponentRenderer<>(this::createApprovalLimit))
////                .setAutoWidth(true)
////                .setFlexGrow(0)
////                .setHeader("Approval Limit ($)")
////                .setTextAlign(ColumnTextAlign.END);
////        grid.addColumn(new ComponentRenderer<>(this::createDate))
////                .setAutoWidth(true)
////                .setFlexGrow(0)
////                .setHeader("Last Report")
////                .setTextAlign(ColumnTextAlign.END);
//
//        return grid;
//    }
//
//
//    private Component createAccountOwnerInfo(AccountOwnerResponseDto AccountOwnerResponseDto) {
//
//        com.partior.client.ui.components.ListItem item = new com.partior.client.ui.components.ListItem(
//                new com.partior.client.ui.components.Initials( AccountOwnerResponseDto.getBic().substring(0,1)),
//                AccountOwnerResponseDto.getBic(),
//                AccountOwnerResponseDto.getRtgsAccountId());
//        item.setPadding(Vertical.XS);
//        item.setSpacing(Right.M);
//        return item;
//    }
//
//    private Component createEnabled(AccountOwnerResponseDto AccountOwnerResponseDto ) {
//        Icon icon;
//        if (AccountOwnerResponseDto.isLocal()) {
//            icon = UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
//        } else {
//            icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
//        }
//        return icon;
//    }
//
//    private String createType(AccountOwnerResponseDto AccountOwnerResponseDto ) {
//
//        return AccountOwnerResponseDto.isLocal() ? "Local" : "Foreign";
//
//    }
//
//    private String party(AccountOwnerResponseDto AccountOwnerResponseDto){
//        return AccountOwnerResponseDto.getSponsorParty().substring(0,3);
//    }
//
//    private String sponsor(AccountOwnerResponseDto AccountOwnerResponseDto){
//        return AccountOwnerResponseDto.getSponsorParty().substring(0,3);
//    }
//
////    private Component createApprovalLimit(Person person) {
////        int amount = person.getRandomInteger() > 0 ? person.getRandomInteger()
////                : 0;
////        return UIUtils.createAmountLabel(amount);
////    }
//
////   // private Component createDate(Person person) {
////        return new Span(UIUtils.formatDate(person.getLastModified()));
////    }
//
//    private DetailsDrawer createDetailsDrawer() {
//        detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.BOTTOM);
//
//        // Header
//        detailsDrawerHeader = new DetailsDrawerHeader("");
//        detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
//        detailsDrawer.setHeader(detailsDrawerHeader);
//
//        // Footer
//        DetailsDrawerFooter footer = new DetailsDrawerFooter();
//        footer.addSaveListener(e -> {
//            detailsDrawer.hide();
//            binder.writeBeanIfValid(this.accountOwnerDto);
//
//            TransactionResponseDto transactionResponseDto
//                    = cantonDataService.onboardAccountOwner( this.accountOwnerDto );
//
//            transactionResponseDto.getTransactionId();
//
//            if( Integer.parseInt(transactionResponseDto.getTransactionId()) > 0){
//                UIUtils.showNotification("Changes saved.");
//            } else {
//                UIUtils.showNotification("Something went wrong");
//            }
//
//        });
//        footer.addCancelListener(e -> detailsDrawer.hide());
//        detailsDrawer.setFooter(footer);
//
//        return detailsDrawer;
//    }
//
//    private void showDetails(AccountOwnerDto accountOwnerDto) {
//        detailsDrawerHeader.setTitle(accountOwnerDto.getBic());
//        detailsDrawer.setContent(createFormDetails(accountOwnerDto));
//        detailsDrawer.show();
//    }
//
//
//
//    private FormLayout createFormDetails(AccountOwnerDto accountOwnerDto) {
//        this.accountOwnerDto = accountOwnerDto;
//
////        bankName = new TextField();
////        bankName.setValue(AccountOwnerResponseDto.getBank());
////        bankName.setWidthFull();
//
//        bic = new TextField();
//        bic.setValue(accountOwnerDto.getBic());
//        bic.setWidthFull();
//
//        isLocal = new RadioButtonGroup<>();
//        isLocal.setItems("True", "False");
//        isLocal.setValue(accountOwnerDto.isLocal() ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
//
//
//
//        sponsorParty = new TextField();
//        sponsorParty.setValue(accountOwnerDto.getSponsorParty());
//        sponsorParty.setWidthFull();
//
//        centralBankParty = new TextField();
//        centralBankParty.setValue(accountOwnerDto.getCentralBankParty());
//        centralBankParty.setWidthFull();
//
//        currency = new ComboBox();
//        currency.setItems(Arrays.asList("SGD", "USD"));
//        currency.setValue(Arrays.asList("SGD", "USD"));
//        currency.setWidthFull();
//
//        binder.forField(bic).bind("bic");
//        binder.forField(isLocal).bind("local");
//        binder.forField(sponsorParty).bind("sponsorParty");
//        binder.forField(centralBankParty).bind("centralBankParty");
//        binder.forField(currency).bind("currency");
//        binder.bindInstanceFields(this);
//
//
//        // Form layout
//        FormLayout form = new FormLayout();
//        form.addClassNames(LumoStyles.Padding.Bottom.L,
//                LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
//        form.setResponsiveSteps(
//                new FormLayout.ResponsiveStep("0", 1,
//                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
//                new FormLayout.ResponsiveStep("600px", 2,
//                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
//                new FormLayout.ResponsiveStep("1024px", 3,
//                        FormLayout.ResponsiveStep.LabelsPosition.TOP));
//        form.addFormItem(bic, "BIC");
//        form.addFormItem(isLocal, "Local");
//        form.addFormItem(sponsorParty, "Sponsor");
//        form.addFormItem(centralBankParty, "CBDC");
//        form.addFormItem(currency, "Currency");
////        form.addFormItem(new Upload(), "Image");
//        return form;
//    }
//
//
//}
