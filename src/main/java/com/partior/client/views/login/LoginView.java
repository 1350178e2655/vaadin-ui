package com.partior.client.views.login;

import com.partior.client.security.SecurityUtils;
import com.partior.client.views.MainLayout;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.*;

@PageTitle("Login")
@Route(value = "login")
@JsModule("./styles/shared-styles.js")
public class LoginView extends LoginOverlay { //} implements BeforeEnterObserver{
    public LoginView() {

        setAction("login");
        
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        // i18n.getHeader().setTitle("Partior");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(false);
        setOpened(true);
    }

//    @Override
//    public void beforeEnter(BeforeEnterEvent event) {
//        if (SecurityUtils.isUserLoggedIn()) {
//            event.forwardTo(MainLayout.class);
//        } else {
//            setOpened(true);
//        }
//    }

//    @Override
//    public void afterNavigation(AfterNavigationEvent event) {
//        setError(
//                event.getLocation().getQueryParameters().getParameters().containsKey(
//                        "error"));
//    }
}
