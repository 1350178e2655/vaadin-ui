package com.partior.client.views.integrations;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.AccountOwnerResponseDto;
import com.partior.client.dto.RtgsIntegration;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.layout.size.*;
import com.partior.client.ui.util.IconSize;
import com.partior.client.ui.util.TextColor;
import com.partior.client.ui.util.UIUtils;
import com.partior.client.ui.util.css.BoxSizing;
import com.partior.client.ui.util.css.Display;
import com.partior.client.views.MainLayout;
import com.partior.client.views.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import javax.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;



@Route(value = "rtgs-connectivity-view", layout = MainLayout.class)
@PageTitle("AccountOwner List")
@PermitAll
public class RtgsConnectivityView extends SplitViewFrame {

    private Grid<RtgsIntegration> grid;
    private List<RtgsIntegration> listRtgsConnections;
    private ListDataProvider<RtgsIntegration> dataProvider;

    private final UnicastProcessor<RtgsIntegration> rtgsStatusPublisher;
    private  final Flux<RtgsIntegration> rtgsStatusMessages;


    @Autowired
    final CantonDataService cantonDataService;

    public RtgsConnectivityView(UnicastProcessor<RtgsIntegration> rtgsStatusPublisher, CantonDataService cantonDataService
            , Flux<RtgsIntegration> rtgsStatusMessages
    ) throws Exception {
        this.rtgsStatusPublisher = rtgsStatusPublisher;
        this.rtgsStatusMessages = rtgsStatusMessages;
        this.cantonDataService = cantonDataService;
    //    setViewHeader( UIUtils.createTitle( VaadinIcon.CREDIT_CARD, "RTGS Connectivity"));
        setViewContent(createContent());
        setViewDetailsPosition(Position.BOTTOM);
        subscribe();
    }

    private Component createContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.CONNECT, "RTGS Connectivity"),
                createGrid());
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();
        return content;
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

    private Grid createGrid() throws Exception {
        grid = new Grid<>();
        listRtgsConnections = new ArrayList<>();
                //cantonDataService.listAccountOwners(cantonDataService.getCdbcOperator() );

        listRtgsConnections.add(new RtgsIntegration("BI", "BI-RTGS", "08:00 - 17:00", "ONLINE"));


        dataProvider = DataProvider.ofCollection(listRtgsConnections);
        grid.setDataProvider(dataProvider);



        grid.addColumn( RtgsIntegration::getRtgs)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("CBDC")
                .setSortable(true);

        grid.addColumn( RtgsIntegration::getRtgs)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("RTGS")
                .setSortable(true);

        grid.addColumn(  RtgsIntegration::getOperatingHours)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("OPERATING HOURS")
                .setSortable(true);

        grid.addComponentColumn(this::createStopLight)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("STATUS")
                .setSortable(true);
        return grid;
    }


    private Component createStopLight(RtgsIntegration rtgsIntegration) {
        return UIUtils.stopLightIcon(rtgsIntegration.getStatus());

    }

    public void subscribe(){
        System.out.println("rtgsStatusMessages:" + rtgsStatusMessages.hashCode());
        rtgsStatusMessages.subscribe(message -> {
            getUI().ifPresent(
                    ui -> ui.access(
                            () -> {
                                                Notification.show(
                                        "Hello Message From RTGS:" +  message, 3000,
                        Notification.Position.BOTTOM_START);
                            }

                    )
            );

        });
    }





}
