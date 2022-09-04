package com.partior.client.views.onboarding;

import com.partior.client.data.service.CantonDataService;

import com.partior.client.dto.CentralBankDto;
import com.partior.client.dto.TransactionResponseDto;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.components.detailsdrawer.DetailsDrawer;
import com.partior.client.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.partior.client.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.partior.client.ui.layout.size.Horizontal;
import com.partior.client.ui.layout.size.Right;
import com.partior.client.ui.layout.size.Vertical;
import com.partior.client.ui.util.LumoStyles;
import com.partior.client.ui.util.UIUtils;
import com.partior.client.ui.util.css.BoxSizing;
import com.partior.client.views.MainLayout;
import com.partior.client.views.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

@Route(value = "onboarding_cdbc", layout = MainLayout.class)
@PageTitle("CBDC Onboarding")
@PermitAll
public class CentralBankView extends SplitViewFrame {

    private Grid<CentralBankDto> grid;
    private ListDataProvider<CentralBankDto> dataProvider;

    private DetailsDrawer detailsDrawer;
    private DetailsDrawerHeader detailsDrawerHeader;


    private final Binder<CentralBankDto> binder;

    private ComboBox currency;
    private TextField centralBankParty;
    private CentralBankDto centralBankDto;



    @Autowired
    final CantonDataService cantonDataService;

    public CentralBankView(CantonDataService cantonDataService) throws Exception {
        this.cantonDataService = cantonDataService;
        setViewContent(includeButton() , createContent());
        setViewDetails(createDetailsDrawer());
        setViewDetailsPosition(Position.BOTTOM);

        binder = new BeanValidationBinder<>(CentralBankDto.class);

      //  filter();
    }

    private HorizontalLayout includeButton() {


        Button addContactButton = new Button("Add Account Owner");
        addContactButton.addClickListener(click -> showDetails(new CentralBankDto("New Central Bank Party", "Currency")) );

        HorizontalLayout toolbar = new HorizontalLayout( addContactButton );
       // toolbar.se
        toolbar.setBoxSizing( com.vaadin.flow.component.orderedlayout.BoxSizing.CONTENT_BOX);

        toolbar.setMargin(true);

        toolbar.setMaxWidth("1024px");
        toolbar.setPadding( true );
       // toolbar.addClassName("add_cdbc");
        return toolbar;
    }

    private Component createContent() throws Exception {

        FlexBoxLayout content = new FlexBoxLayout(createGrid());
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X,
                com.partior.client.ui.layout.size.Top.RESPONSIVE_X);

        return content;
    }

    private Grid createGrid() throws Exception {
        grid = new Grid<>();

        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(this::showDetails));

        dataProvider = DataProvider.ofCollection(cantonDataService.listCentralBanks());
        grid.setDataProvider(dataProvider);
        grid.setSizeFull();

        grid.addColumn(CentralBankDto::getCentralBankParty)
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setFrozen(true)
                .setHeader("CBDC")
                .setSortable(true);

        grid.addColumn(new ComponentRenderer<>(this::createCentralBankInfoInfo))
                .setAutoWidth(true)
                .setHeader("Details");

//        grid.addColumn(new ComponentRenderer<>(this::createEnabled))
//                .setAutoWidth(true)
//                .setFlexGrow(0)
//                .setHeader("IsLocal")
//                .setTextAlign(ColumnTextAlign.END);


        return grid;
    }

    private Component createCentralBankInfoInfo(CentralBankDto centralBankDto) {
        com.partior.client.ui.components.ListItem item = new com.partior.client.ui.components.ListItem(
                new com.partior.client.ui.components.Initials( centralBankDto.getCentralBankParty().substring(0,1)),
                centralBankDto.getCentralBankParty(),
                centralBankDto.getCurrency());
        item.setPadding(Vertical.XS);
        item.setSpacing(Right.M);
        return item;
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
            binder.writeBeanIfValid(this.centralBankDto);

       TransactionResponseDto transactionResponseDto = cantonDataService.onboardCentralbank( this.centralBankDto );

            if( transactionResponseDto!=null &&  transactionResponseDto !=null
                    &&  transactionResponseDto.getTransactionId() != null) {
                UIUtils.showNotification("Changes saved.");
            } else {
                UIUtils.showNotification("Something went wrong");
            }



        });
        footer.addCancelListener(e -> detailsDrawer.hide());
        detailsDrawer.setFooter(footer);

        return detailsDrawer;
    }

    private void showDetails(CentralBankDto centralBankDto) {
        detailsDrawerHeader.setTitle(centralBankDto.getCentralBankParty());
        detailsDrawer.setContent(createFormDetails(centralBankDto));
        detailsDrawer.show();
    }



    private FormLayout createFormDetails(CentralBankDto centralBankDto) {

        this.centralBankDto = centralBankDto;

        centralBankParty = new TextField();
        centralBankParty.setValue(centralBankDto.getCentralBankParty());
        centralBankParty.setWidthFull();


        currency = new ComboBox();
        currency.setItems(Arrays.asList("SGD", "USD", "IDR"));
        currency.setValue(Arrays.asList(centralBankDto.getCurrency()));
        currency.setWidthFull();

        binder.forField(centralBankParty).bind("centralBankParty");
        binder.forField(currency).bind("currency");
        binder.bindInstanceFields(this);

//        binder.addStatusChangeListener(event -> {
//            final boolean isValid = !event.hasValidationErrors();
//            final boolean hasChanges = binder.hasChanges();
//            save.setEnabled(hasChanges && isValid);
//            discard.setEnabled(hasChanges);
//        });


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

        form.addFormItem(centralBankParty, "Central Bank Party Name");
        form.addFormItem(currency, "Currency");

        return form;
    }


}
