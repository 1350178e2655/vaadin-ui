package com.partior.client.views.onboarding;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.AccountOwnerResponseDto;
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
import com.partior.client.views.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
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

@Route(value = "onboarding_account_owner", layout = MainLayout.class)
@PageTitle("AccountOwner Onboarding")
@PermitAll
public class OnboardAccountOwnerView extends ViewFrame {

    @Autowired
    final CantonDataService cantonDataService;

    public OnboardAccountOwnerView(CantonDataService cantonDataService) throws Exception {

        this.cantonDataService = cantonDataService;
        setViewContent(createContent());
    }

    private Component createContent() throws Exception {
        FlexBoxLayout content = new FlexBoxLayout(new AccountOwnerForm(cantonDataService, cantonDataService.getCdbcOperator()));
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setHeightFull();
        content.setPadding(Horizontal.RESPONSIVE_X, com.partior.client.ui.layout.size.Top.RESPONSIVE_X);
        return content;
    }



}
