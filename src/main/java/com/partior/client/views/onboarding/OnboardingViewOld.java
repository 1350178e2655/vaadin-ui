//package com.partior.client.views.onboarding;
//
//
//
//import com.partior.client.data.service.AccountOwnerService;
//import com.partior.client.dto.AccountOwnerResponseDto;
//import com.partior.client.views.MainLayout;
//import com.partior.client.views.masterdetail.MasterDetailView;
//import com.vaadin.flow.component.HasStyle;
//import com.vaadin.flow.component.Tag;
//import com.vaadin.flow.component.UI;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.checkbox.Checkbox;
//import com.vaadin.flow.component.dependency.Uses;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.component.grid.GridVariant;
//import com.vaadin.flow.component.icon.Icon;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.template.Id;
//import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.data.binder.BeanValidationBinder;
//import com.vaadin.flow.data.binder.ValidationException;
//import com.vaadin.flow.data.renderer.LitRenderer;
//import com.vaadin.flow.router.BeforeEnterEvent;
//import com.vaadin.flow.router.BeforeEnterObserver;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.annotation.security.PermitAll;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//
//public class OnboardingViewOld  extends VerticalLayout implements HasStyle, BeforeEnterObserver {
//
//    private final String ONBOARDING_ID = "onboardingID";
//    private final String ONBOARDING_EDIT_ROUTE_TEMPLATE = "onboarding/%s/edit";
//
//    // This is the Java companion file of a design
//    // You can find the design file inside /frontend/views/
//    // The design can be easily edited by using Vaadin Designer
//    // (vaadin.com/designer)
//
//    @Id
//    private Grid<AccountOwnerResponseDto> grid;
//
//    @Id
//    private TextField bankName;
//    @Id
//    private TextField bic;
//
//    @Id
//    private Checkbox enable;
//
//    @Id
//    private Button cancel;
//    @Id
//    private Button save;
//
//    private BeanValidationBinder<AccountOwnerResponseDto> binder;
//
//    private AccountOwnerResponseDto accountOwner;
//
//    private  AccountOwnerService accountOwnerService;
//
////    public OnboardingView(){
////
////        Grid<AccountOwnerResponseDto> grid = new Grid<>(AccountOwnerResponseDto.class, false);
////        grid.addColumn(AccountOwnerResponseDto::getBankName).setHeader("BankName").setAutoWidth(true);
////        grid.addColumn(AccountOwnerResponseDto::getBic).setHeader("Bic").setAutoWidth(true);
////
////                List<AccountOwnerResponseDto> accountOwnerList = new ArrayList<>();
////
////        accountOwnerList.add( new AccountOwnerResponseDto(1, "dbs", "dbs_bic", true));
////        accountOwnerList.add( new AccountOwnerResponseDto(2, "uob", "uob_bic", true));
////        accountOwnerList.add( new AccountOwnerResponseDto(3, "uob", "uob_bic", true));
////        accountOwnerList.add( new AccountOwnerResponseDto(4, "uob", "uob_bic", true));
////        accountOwnerList.add( new AccountOwnerResponseDto(5, "uob", "uob_bic", true));
////        grid.setItems(accountOwnerList);
////
////        add( grid);
////    }
//
//
//    @Autowired
//    public OnboardingViewOld(AccountOwnerService accountOwnerService) {
//
//        this.accountOwnerService = accountOwnerService;
//
//     //   addClassNames("onboarding-view");
//
//        grid = new Grid<>();
//        grid.addColumn(AccountOwnerResponseDto::getBankName).setHeader("BankName").setAutoWidth(true);
//        grid.addColumn(AccountOwnerResponseDto::getBic).setHeader("Bic").setAutoWidth(true);
//
//
//        LitRenderer<AccountOwnerResponseDto> importantRenderer = LitRenderer.<AccountOwnerResponseDto>of(
//                        "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
//                .withProperty("icon", enable -> enable.isEnable() ? "check" : "minus").withProperty("color",
//                        enable -> enable.isEnable()
//                                ? "var(--lumo-primary-text-color)"
//                                : "var(--lumo-disabled-text-color)");
//
//        grid.addColumn(importantRenderer).setHeader("Important").setAutoWidth(true);
//
//        List<AccountOwnerResponseDto> accountOwnerList = new ArrayList<>();
//
//        accountOwnerList.add( new AccountOwnerResponseDto(1, "dbs", "dbs_bic", true));
//        accountOwnerList.add( new AccountOwnerResponseDto(2, "uob", "uob_bic", true));
//        accountOwnerList.add( new AccountOwnerResponseDto(3, "uob", "uob_bic", true));
//        accountOwnerList.add( new AccountOwnerResponseDto(4, "uob", "uob_bic", true));
//        accountOwnerList.add( new AccountOwnerResponseDto(5, "uob", "uob_bic", true));
//
//        grid.setItems(accountOwnerList);
//
//        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
//        grid.setHeightFull();
//
//        // when a row is selected or deselected, populate form
//        grid.asSingleSelect().addValueChangeListener(event -> {
//            if (event.getValue() != null) {
//                UI.getCurrent().navigate(String.format(ONBOARDING_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
//            } else {
//                clearForm();
//                UI.getCurrent().navigate(MasterDetailView.class);
//            }
//        });
//
//        // Configure Form
//        binder = new BeanValidationBinder<>(AccountOwnerResponseDto.class);
//
//        // Bind fields. This is where you'd define e.g. validation rules
//
//        binder.bindInstanceFields(this);
//
//        cancel = new Button();
//        cancel.addClickListener(e -> {
//            clearForm();
//            refreshGrid();
//        });
//
//        save = new Button();
//        save.addClickListener(e -> {
//            try {
//                if (this.accountOwner == null) {
//                    this.accountOwner = new AccountOwnerResponseDto();
//                }
//                binder.writeBean(this.accountOwner);
//            //    samplePersonService.update(this.samplePerson);
//                clearForm();
//                refreshGrid();
//                Notification.show("SamplePerson details stored.");
//                UI.getCurrent().navigate(OnboardingView.class);
//            } catch (ValidationException validationException) {
//                Notification.show("An exception happened while trying to store the samplePerson details.");
//            }
//        });
//
//
//        add( grid);
//    }
//
//    @Override
//    public void beforeEnter(BeforeEnterEvent event) {
//
//        Optional<UUID> onboardingID = event.getRouteParameters().get(ONBOARDING_ID).map(UUID::fromString);
//
//        if (onboardingID.isPresent()) {
//
//            Optional<AccountOwnerResponseDto> samplePersonFromBackend = null;// accountOwnerService.get(onboardingID.get());
//
//            if (samplePersonFromBackend!=null && samplePersonFromBackend.isPresent()) {
//                populateForm(samplePersonFromBackend.get());
//
//            } else {
//                Notification.show(
//                        String.format("The requested samplePerson was not found, ID = %s", onboardingID.get()), 3000,
//                        Notification.Position.BOTTOM_START);
//                // when a row is selected but the data is no longer available,
//                // refresh grid
//                refreshGrid();
//                event.forwardTo(MasterDetailView.class);
//            }
//        }
//
//    }
//
//    private void refreshGrid() {
//        grid.select(null);
//        grid.getLazyDataView().refreshAll();
//    }
//
//    private void clearForm() {
//        populateForm(null);
//    }
//
//    private void populateForm(AccountOwnerResponseDto value) {
//        this.accountOwner = value;
//        binder.readBean(this.accountOwner);
//
//    }
//}
//
