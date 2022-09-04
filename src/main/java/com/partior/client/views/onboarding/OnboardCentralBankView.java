package com.partior.client.views.onboarding;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.layout.size.Horizontal;
import com.partior.client.ui.util.css.BoxSizing;
import com.partior.client.views.MainLayout;
import com.partior.client.views.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@Route(value = "onboarding_central_bank", layout = MainLayout.class)
@PageTitle("AccountOwner Onboarding")
@PermitAll
public class OnboardCentralBankView extends ViewFrame {

    @Autowired
    final CantonDataService cantonDataService;

    public OnboardCentralBankView(CantonDataService cantonDataService) throws Exception {
        this.cantonDataService = cantonDataService;
        setViewContent(createContent());
    }

    private Component createContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(new CentralBankForm(cantonDataService));
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, com.partior.client.ui.layout.size.Top.RESPONSIVE_X);
        return content;
    }



}
