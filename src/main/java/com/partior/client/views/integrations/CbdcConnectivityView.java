


package com.partior.client.views.integrations;

 import com.partior.client.data.service.CantonDataService;
 import com.partior.client.dto.AccountOwnerResponseDto;
 import com.partior.client.dto.DisableAccountOwnerResponseDto;
 import com.partior.client.dto.TransactionResponseDto;
 import com.partior.client.ui.components.FlexBoxLayout;
 import com.partior.client.ui.layout.size.Horizontal;
 import com.partior.client.ui.layout.size.Right;
 import com.partior.client.ui.layout.size.Vertical;
 import com.partior.client.ui.util.UIUtils;
 import com.partior.client.ui.util.css.BoxSizing;
 import com.partior.client.views.MainLayout;
 import com.partior.client.views.SplitViewFrame;
 import com.vaadin.flow.component.Component;
 import com.vaadin.flow.component.checkbox.Checkbox;
 import com.vaadin.flow.component.grid.Grid;
 import com.vaadin.flow.component.notification.Notification;
 import com.vaadin.flow.data.provider.DataProvider;
 import com.vaadin.flow.data.provider.ListDataProvider;
 import com.vaadin.flow.data.renderer.ComponentRenderer;
 import com.vaadin.flow.router.PageTitle;
 import com.vaadin.flow.router.Route;
 import org.springframework.beans.factory.annotation.Autowired;

 import javax.annotation.security.PermitAll;
 import java.util.List;

 @Route(value = "cdbc-connectivity-view", layout = MainLayout.class)
 @PageTitle("AccountOwner List")
 @PermitAll
 public class CbdcConnectivityView extends SplitViewFrame {

     private Grid<AccountOwnerResponseDto> grid;
     private ListDataProvider<AccountOwnerResponseDto> dataProvider;
     private List<AccountOwnerResponseDto> listAccountOwners;


     @Autowired
     final CantonDataService cantonDataService;

     public CbdcConnectivityView(CantonDataService cantonDataService) throws Exception {
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
         listAccountOwners = cantonDataService.listAccountOwners(cantonDataService.getCdbcOperator() );
         dataProvider = DataProvider.ofCollection(listAccountOwners);
         grid.setDataProvider(dataProvider);
         grid.setSizeFull();


         grid.addColumn(new ComponentRenderer<>(this::createAccountOwnerInfo))
                 .setAutoWidth(true)
                 .setFrozen(true)
                 .setHeader("CBDC");

         grid.addColumn( this::createParty  )
                 .setAutoWidth(true)
                 .setFlexGrow(0)
                 .setHeader("Opening Hour")
                 .setSortable(true);

         grid.addColumn(  this::sponsor)
                 .setAutoWidth(true)
                 .setFlexGrow(0)
                 .setHeader("Closing Hour")
                 .setSortable(true);

         List<AccountOwnerResponseDto>  disabledAccounts =cantonDataService.listDisabledParticipantBanks(cantonDataService.getCdbcOperator() );


         grid.addComponentColumn((accountOwnerResponseDto) -> {
             Checkbox checkBox = new Checkbox();
             checkBox.setValue( disabledAccounts.indexOf(accountOwnerResponseDto) < 0 ? true: false);
             checkBox.addValueChangeListener(
                     event ->  {

                         boolean disable = !event.getValue();

                         AccountOwnerResponseDto bank = cantonDataService.getBank(accountOwnerResponseDto.getShortName(), listAccountOwners);

                         TransactionResponseDto transactionResponseDto =  cantonDataService.enableOrDisableAccOwner(
                                 new DisableAccountOwnerResponseDto(accountOwnerResponseDto.getBic(), bank.getShortName(),
                                         cantonDataService.getCdbcOperator(),disable));


                         if(transactionResponseDto.getEffectiveAt() > 0){
                             Notification.show("Account was successfully updated.", 3000,
                                     Notification.Position.BOTTOM_CENTER);
                         } else {
                             Notification.show("Something went wrong.", 3000,
                                     Notification.Position.BOTTOM_CENTER);
                         }
                     }
             );
             return checkBox;
         }).setHeader("Status");

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

     private String createParty(AccountOwnerResponseDto accountOwnerResponseDto) {
         return UIUtils.CBDC_NAME.get( cantonDataService.getCdbcOperator() );
     }


     private Component createAccountOwnerInfo(AccountOwnerResponseDto accountOwnerResponseDto) {

         com.partior.client.ui.components.ListItem item = new com.partior.client.ui.components.ListItem(
                 new com.partior.client.ui.components.Initials( accountOwnerResponseDto.getBic().substring(0,1)),
                 accountOwnerResponseDto.getShortName() );

         item.setPadding(Vertical.XS);
         item.setSpacing(Right.M);
         return item;
     }

     private String createType(AccountOwnerResponseDto accountOwnerResponseDto ) {
         return accountOwnerResponseDto.isLocal() ? "Local" : "Foreign";
     }

     private String party(AccountOwnerResponseDto accountOwnerResponseDto){
         return accountOwnerResponseDto.getCentralBankParty().substring(0,3);
     }

     private String sponsor(AccountOwnerResponseDto accountOwnerResponseDto){
         return accountOwnerResponseDto.getSponsorParty() != null?
                 accountOwnerResponseDto.getSponsorParty().substring(0,3):accountOwnerResponseDto.getBank();
     }









 }
