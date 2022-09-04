package com.partior.client;

import com.partior.client.dto.RtgsIntegration;
import com.partior.client.ui.util.UIUtils;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
//@CssImport(value = "./styles/components/charts.css", themeFor = "vaadin-chart", include = "vaadin-chart-default-theme")
//@CssImport(value = "./styles/components/floating-action-button.css", themeFor = "vaadin-button")
//@CssImport(value = "./styles/components/grid.css", themeFor = "vaadin-grid")
@CssImport("./styles/lumo/border-radius.css")
@CssImport("./styles/lumo/icon-size.css")
@CssImport("./styles/lumo/margin.css")
@CssImport("./styles/lumo/padding.css")
@CssImport("./styles/lumo/shadow.css")
@CssImport("./styles/lumo/spacing.css")
@CssImport("./styles/lumo/typography.css")
//@CssImport("./styles/misc/box-shadow-borders.css")
@CssImport(value = "./styles/styles.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge")
@PWA(name = "partior-cbdc", shortName = "partior-cbdc",
        iconPath = UIUtils.IMG_PATH + "logos/18xxxx.png")
@Push
@Tag("iron-icon")
@NpmPackage(value="@polymer/iron-icon", version="3.0.1")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@Theme("partior-ui")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    UnicastProcessor<RtgsIntegration> rtgsStatusPublisher(){
        return UnicastProcessor.create();
    }

    @Bean
    Flux<RtgsIntegration> rtgsStatusMessages(UnicastProcessor<RtgsIntegration> rtgsStatusPublisher) {
        return rtgsStatusPublisher.replay(1).autoConnect();
    }

}
