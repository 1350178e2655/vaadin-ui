package com.partior.client.views.integrations;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.*;
import com.partior.client.dto.enums.Currency;
import com.partior.client.security.AuthenticatedUser;
import com.partior.client.ui.util.UIUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class CdbcIntegrationForm extends VerticalLayout {

    private ComboBox<String> centralBank = new ComboBox<>("CDBC DOMAIN");
    private Button save = new Button("Integrate");





    private Grid<DomainResponseDto> grid;

    private List<DomainResponseDto> listCbdcConnections;
    private ListDataProvider<DomainResponseDto> dataProvider;


    private CantonDataService cantonDataService;
    private AuthenticatedUser authenticatedUser;


    public CdbcIntegrationForm(CantonDataService cantonDataService, AuthenticatedUser authenticatedUser) throws Exception {
        this.cantonDataService = cantonDataService;
        this.authenticatedUser =authenticatedUser;

        add( UIUtils.createTitle( VaadinIcon.CREDIT_CARD, "CBDC Connectivity"));
      // add(createFormLayout());
        add(createGrid());
    }








    private Component createTitle() {
        return new H3("CBDC DOMAIN CONNECTIVITY");
    }


    private Grid<DomainResponseDto> createGrid() throws Exception {

        grid = new Grid<>();
        listCbdcConnections = cantonDataService.listAllDomains();
        dataProvider = DataProvider.ofCollection( listCbdcConnections );
        grid.setDataProvider(dataProvider);


        grid.addColumn(  DomainResponseDto::getDomainAlias)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("DOMAIN ID")
                .setSortable(true);

        grid.addComponentColumn(this::isConnected)

                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("CONNECTED")
                .setSortable(true);



        grid.addComponentColumn((accountOwnerResponseDto) -> {
            return createConnectivityButtonLayout(accountOwnerResponseDto, listCbdcConnections.indexOf(accountOwnerResponseDto));
        }).setHeader("Connectivity");

        return grid;
    }

    private Component isConnected(DomainResponseDto domainResponseDto){
        return UIUtils.stopLightIcon(domainResponseDto.getConnected().toString().toUpperCase());
    }

    private String cbdcName(CbdcIntegration cbdcIntegration) {
        return cbdcIntegration.getCbdc();
    }

    private String status(CbdcIntegration cbdcIntegration) {
        return cbdcIntegration.getStatus();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CbdcIntegration{
        private String cbdc;
        private String operatingHours;
        private String status;
    }

    private Component createConnectivityButtonLayout(DomainResponseDto domainResponseDto, int idx) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");

        Button connect = new Button("Connect");
        Button disConnect = new Button("DisConnect");

         Icon connectIcon = new Icon(VaadinIcon.CONNECT);
         connect.addThemeVariants(ButtonVariant.LUMO_SMALL);
         Icon disconIcon = new Icon(VaadinIcon.WARNING);
         disConnect.addThemeVariants(ButtonVariant.LUMO_SMALL);

        if(domainResponseDto.getConnected()){
            connect.setEnabled(false);
            connectIcon.setColor("gray");
            connect.setIcon(connectIcon);

            disConnect.setEnabled(true);
            disconIcon.setColor("red");
            disConnect.setIcon(disconIcon);

        } else {
            connect.setEnabled(true);
            connectIcon.setColor("green");
            connect.setIcon(connectIcon);

            disConnect.setEnabled(false);
            disconIcon.setColor("gray");
            disConnect.setIcon(disconIcon);
        }

        connect.addClickListener(
                e -> {
                    String response =  cantonDataService.connectDomains(new DomainDto(domainResponseDto.getDomainAlias()));
                        if(response!=null){
                            Notification.show("Domain " + domainResponseDto.getDomainId() + " was successfully connected", 3000,
                                    Notification.Position.TOP_CENTER);

                            domainResponseDto.setConnected(Boolean.TRUE);

                            if(domainResponseDto.getConnected()){
                                connect.setEnabled(false);
                                connectIcon.setColor("gray");
                                connect.setIcon(connectIcon);

                                disConnect.setEnabled(true);
                                disconIcon.setColor("red");
                                disConnect.setIcon(disconIcon);

                            } else {
                                connect.setEnabled(true);
                                connectIcon.setColor("green");
                                connect.setIcon(connectIcon);

                                disConnect.setEnabled(false);
                                disconIcon.setColor("gray");
                                disConnect.setIcon(disconIcon);
                            }
                            listCbdcConnections.set(idx, domainResponseDto);
                            dataProvider = DataProvider.ofCollection( listCbdcConnections );
                            dataProvider.refreshItem(domainResponseDto);
                             grid.setDataProvider(dataProvider);

                        }

                }
        );


        disConnect.addClickListener(e -> {
                    String response = cantonDataService.disConnectDomains(new DomainDto(domainResponseDto.getDomainAlias()));
                    if(response!=null){
                        Notification.show("Domain " + domainResponseDto.getDomainId() + " was successfully disconnected", 3000,
                                Notification.Position.TOP_CENTER);
                        domainResponseDto.setConnected(Boolean.FALSE);

                        if(domainResponseDto.getConnected()){
                            connect.setEnabled(false);
                            disConnect.setEnabled(true);
                        } else {
                            connect.setEnabled(true);
                            disConnect.setEnabled(false);
                        }
                        listCbdcConnections.set(idx, domainResponseDto);
                        dataProvider = DataProvider.ofCollection( listCbdcConnections );
                        dataProvider.refreshItem(domainResponseDto);
                        grid.setDataProvider(dataProvider);


                    }

                }
        );

       // setButtonStatus(connect, disConnect,  domainResponseDto.getConnected());

        if(domainResponseDto.getConnected()){
            connect.setEnabled(false);
            disConnect.setEnabled(true);
        } else {
            connect.setEnabled(true);
            disConnect.setEnabled(false);
        }
        buttonLayout.add(connect, disConnect);

        return buttonLayout;
    }

    private void setButtonStatus(Button connect, Button disConnect, boolean connected){
        if(connected){
            connect.setEnabled(false);
            disConnect.setEnabled(true);
        } else {
            connect.setEnabled(true);
            disConnect.setEnabled(false);
        }

    }

}
