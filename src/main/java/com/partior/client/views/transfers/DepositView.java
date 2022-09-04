package com.partior.client.views.transfers;

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
import com.partior.client.views.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

import static com.partior.client.ui.util.UIUtils.MAX_WIDTH;

@Route(value = "deposit_transaction", layout = MainLayout.class)
@PageTitle("Transaction Deposit")
@PermitAll
public class DepositView extends SplitViewFrame {

    @Autowired
    final CantonDataService cantonDataService;
    @Autowired
    final AuthenticatedUser authenticatedUser;

    public DepositView(CantonDataService cantonDataService, AuthenticatedUser authenticatedUser) throws Exception {
        this.cantonDataService = cantonDataService;
        this.authenticatedUser = authenticatedUser;

        setViewContent(createContent());
    }

    private Component createContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(createForm());
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);

        return content;
    }

    private Component createForm() throws Exception {
        FlexBoxLayout form = new FlexBoxLayout(
                new DepositForm(cantonDataService, authenticatedUser)
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
