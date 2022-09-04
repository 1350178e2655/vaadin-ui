package com.partior.client.views.integrations;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.security.AuthenticatedUser;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.layout.size.Horizontal;
import com.partior.client.ui.layout.size.Right;
import com.partior.client.ui.layout.size.Top;
import com.partior.client.ui.util.UIUtils;
import com.partior.client.ui.util.css.BorderRadius;
import com.partior.client.ui.util.css.BoxSizing;
import com.partior.client.ui.util.css.Display;
import com.partior.client.views.MainLayout;
import com.partior.client.views.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@Route(value = "cdbc-integration-view", layout = MainLayout.class)
@PageTitle("CDBC Integration")
@PermitAll
public class CdbcIntegrationView extends SplitViewFrame {

    @Autowired
    final CantonDataService cantonDataService;
    @Autowired
    final AuthenticatedUser authenticatedUser;

    public CdbcIntegrationView(CantonDataService cantonDataService, AuthenticatedUser authenticatedUser) throws Exception {
        this.cantonDataService = cantonDataService;
        this.authenticatedUser = authenticatedUser;
       // setViewHeader( new H3("CBDC Domain Connectivity") );
        setViewContent(createContent());
    }

    private Component createContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(createForm());
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setDisplay(Display.BLOCK);
        content.setMargin(Top.L);
        content.setPadding(Horizontal.RESPONSIVE_L);
        content.setWidthFull();
        return content;

    }

    private Component createForm() throws Exception {
        FlexBoxLayout form = new FlexBoxLayout(
                new CdbcIntegrationForm(cantonDataService, authenticatedUser)
        );
        form.setHeightFull();
        form.setWidthFull();
        form.setBoxSizing(BoxSizing.BORDER_BOX);
        form.setMargin(Top.L);
        form.setSpacing(Right.L);
        form.setAlignItems(FlexComponent.Alignment.CENTER);
        form.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        form.setBackgroundColor(UIUtils.BG_COLOR);
        form.setBorderRadius(BorderRadius.S);
        return form;
    }

}
