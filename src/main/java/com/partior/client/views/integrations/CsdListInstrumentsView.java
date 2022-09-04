package com.partior.client.views.integrations;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.layout.size.Bottom;
import com.partior.client.ui.layout.size.Horizontal;
import com.partior.client.ui.layout.size.Right;
import com.partior.client.ui.layout.size.Top;
import com.partior.client.ui.util.IconSize;
import com.partior.client.ui.util.TextColor;
import com.partior.client.ui.util.UIUtils;
import com.partior.client.ui.util.css.BoxSizing;
import com.partior.client.ui.util.css.Display;
import com.partior.client.views.MainLayout;
import com.partior.client.views.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;

@Route(value = "csd-connectivity-view", layout = MainLayout.class)
@PageTitle("AccountOwner List")
@PermitAll
public class CsdListInstrumentsView extends SplitViewFrame {

    private Grid<CsdIntegration> grid;
    private List<CsdIntegration> listCsdConnections;
    private ListDataProvider<CsdIntegration> dataProvider;


    @Autowired
    final CantonDataService cantonDataService;

    public CsdListInstrumentsView(CantonDataService cantonDataService) throws Exception {
        this.cantonDataService = cantonDataService;
     //   setViewHeader(  UIUtils.createTitle( VaadinIcon.CREDIT_CARD,  "CSD Connectivity"));
        setViewContent(createContent());
        setViewDetailsPosition(Position.BOTTOM);
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
    private Component createContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(
                createHeader(VaadinIcon.USER, "CSD Connectivity"),
                createGrid());
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();
        return content;
    }

    private Grid createGrid() throws Exception {
        grid = new Grid<>();
        listCsdConnections = new ArrayList<>();
        listCsdConnections.add(new CsdIntegration("BI", "08:00 - 17:00", "ONLINE"));

        //cantonDataService.listAccountOwners(cantonDataService.getCdbcOperator() );

        dataProvider = DataProvider.ofCollection(listCsdConnections);
        grid.setDataProvider(dataProvider);

        grid.addColumn( CsdIntegration::getCsd)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("CSD")
                .setSortable(true);

        grid.addColumn(  CsdIntegration::getOperatingHours)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("OPERATING HOURS")
                .setSortable(true);

        grid.addComponentColumn(this::stopLight)
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("STATUS")
                .setSortable(true);

        return grid;
    }

    private Component createTitle(String title) {
        FlexBoxLayout content = new FlexBoxLayout( new H3(title));
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, com.partior.client.ui.layout.size.Top.RESPONSIVE_X);
        return content;
    }

    private Component stopLight(CsdIntegration csdIntegration){
        return UIUtils.stopLightIcon(csdIntegration.getStatus());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CsdIntegration{
        private String csd;
        private String operatingHours;
        private String status;
    }





}
