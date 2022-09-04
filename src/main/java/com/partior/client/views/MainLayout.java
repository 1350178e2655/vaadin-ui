package com.partior.client.views;

import com.partior.client.data.service.CantonDataService;
import com.partior.client.dto.AccountOwnerDto;
import com.partior.client.security.AuthenticatedUser;
import com.partior.client.ui.components.FlexBoxLayout;
import com.partior.client.ui.components.navigation.bar.AppBar;
import com.partior.client.ui.components.navigation.bar.TabBar;
import com.partior.client.ui.components.navigation.drawer.NaviDrawer;
import com.partior.client.ui.components.navigation.drawer.NaviItem;
import com.partior.client.ui.components.navigation.drawer.NaviMenu;
import com.partior.client.ui.util.UIUtils;
import com.partior.client.ui.util.css.Overflow;
// import com.partior.client.views.accounts.ListAccountIdView;
import com.partior.client.views.accounts.ListAccountOwnerView;
import com.partior.client.views.accounts.ListCsdAccountOwnerView;
import com.partior.client.views.dashboard.Statistics;
import com.partior.client.views.integrations.CdbcIntegrationView;
import com.partior.client.views.integrations.CsdListInstrumentsView;
import com.partior.client.views.onboarding.*;
import com.partior.client.views.integrations.RtgsConnectivityView;
import com.partior.client.views.transactions.ListCsdTransactionsView;
import com.partior.client.views.transactions.ListRtgsTransactionsView;
import com.partior.client.views.transactions.ListTrancationsAccountOwnerView;
import com.partior.client.views.transfers.*;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends FlexBoxLayout
        implements RouterLayout, AfterNavigationObserver {


    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    private static final String CLASS_NAME = "root";
    private NaviDrawer naviDrawer;

    private FlexBoxLayout column;
    private Div appHeaderInner;
    private FlexBoxLayout viewContainer;
    private Div appFooterInner;

    private Div appFooterOuter;
    private FlexBoxLayout row;

    private TabBar tabBar;
    private AppBar appBar;
    private boolean navigationTabs = false;
    private final CantonDataService cantonDataService;


    public MainLayout(AuthenticatedUser authenticatedUser,
                      AccessAnnotationChecker accessChecker,
                      CantonDataService cantonDataService) throws Exception {

        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.cantonDataService = cantonDataService;

        addClassName( CLASS_NAME );
        setFlexDirection(FlexDirection.COLUMN);
        setSizeFull();

        initStructure();
        initNaviItems();
        initHeadersAndFooters();
    }



    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if (navigationTabs) {
            afterNavigationWithTabs(event);
        } else {
            afterNavigationWithoutTabs(event);
        }
    }

    private void afterNavigationWithoutTabs(AfterNavigationEvent e) {
        NaviItem active = getActiveItem(e);
        if (active != null) {
            getAppBar().setTitle(active.getText());
        }
    }
    private void afterNavigationWithTabs(AfterNavigationEvent e) {
        NaviItem active = getActiveItem(e);
        if (active == null) {
            if (tabBar.getTabCount() == 0) {
                tabBar.addClosableTab("", Home.class);
            }
        } else {
            if (tabBar.getTabCount() > 0) {
                tabBar.updateSelectedTab(active.getText(),
                        active.getNavigationTarget());
            } else {
                tabBar.addClosableTab(active.getText(),
                        active.getNavigationTarget());
            }
        }
        appBar.getMenuIcon().setVisible(false);
    }

    private NaviItem getActiveItem(AfterNavigationEvent e) {
        for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
            if (item.isHighlighted(e)) {
                return item;
            }
        }
        return null;
    }



    /**
     * Initialise the required components and containers.
     */
    private void initStructure() {
        naviDrawer = new NaviDrawer(cantonDataService.getCdbcOperator());

        viewContainer = new FlexBoxLayout();
        viewContainer.addClassName(CLASS_NAME + "__view-container");
        viewContainer.setOverflow(Overflow.HIDDEN);

        column = new FlexBoxLayout(viewContainer);
        column.addClassName(CLASS_NAME + "__column");
        column.setFlexDirection(FlexDirection.COLUMN);
        column.setFlexGrow(1, viewContainer);
        column.setOverflow(Overflow.HIDDEN);

        row = new FlexBoxLayout(naviDrawer, column);
        row.addClassName(CLASS_NAME + "__row");
        row.setFlexGrow(1, column);
        row.setOverflow(Overflow.HIDDEN);
        add(row);
        setFlexGrow(1, row);
    }

    /**
     * Initialise the navigation items.
     */
    private void initNaviItems() throws Exception {

        NaviMenu menu = naviDrawer.getMenu();

        if(hasRole("ROLE_ADMIN")) {
            generateAdminMenu(menu);
        } else if(hasRole("ROLE_USER")){
            generateCommercialBankMenu(menu);
        } else if(hasRole("ROLE_RTGS")){
            generateRtgsMenu(menu);
        } else if(hasRole("ROLE_CSD")){
            generateCsdMenu(menu);
        }


        /*
        String userName= ((org.springframework.security.core.userdetails.User)SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getUsername();

        if(hasRole("ROLE_USER")) {
            if(!userName.equals("MAS") && !userName.equals("BOI")) {
                NaviItem createAccountId = menu.addNaviItem(VaadinIcon.USERS, "Create New", null);
                menu.addNaviItem(createAccountId, "Account", OnboardAccountIdView.class);

                NaviItem transfers = menu.addNaviItem(VaadinIcon.CREDIT_CARD, "Transactions", null);
                menu.addNaviItem(transfers, "Deposit", DepositView.class);
                menu.addNaviItem(transfers, "Withdraw", WithdrawView.class);
                menu.addNaviItem(transfers, "Transfer", TransferView.class);
                menu.addNaviItem(transfers, "Balance", BalanceView.class);
            }
        }

        NaviItem transactions = menu.addNaviItem(VaadinIcon.COINS, "Reporting", null);
        menu.addNaviItem(transactions, "AccountOwner", ListTrancationsAccountOwnerView.class); // copy the dashboard transaction screen, depending on the account login

        if(hasRole("ROLE_ADMIN")) {
            NaviItem rtgs = menu.addNaviItem(VaadinIcon.BUILDING_O, "RTGS", null);

            menu.addNaviItem(rtgs, "Monitoring", ListRtgsTransactionsView.class); // copy the dashboard transaction screen, depending on the account login
        }


        menu.addNaviItem(VaadinIcon.USER, userName, AboutView.class); */
    }

    public void generateAdminMenu(NaviMenu menu){

            menu.addNaviItem(VaadinIcon.HOME, "Dashboard", ListTrancationsAccountOwnerView.class);

            NaviItem cbdc = menu.addNaviItem(VaadinIcon.USER_STAR, "CBDC Management", null);
            menu.addNaviItem(cbdc, "Domains", CdbcIntegrationView.class);
            menu.addNaviItem(cbdc, "Participants", ListAccountOwnerView.class);


            NaviItem rtgs = menu.addNaviItem(VaadinIcon.WALLET, "RTGS", null);
            menu.addNaviItem(rtgs, "Transactions", ListRtgsTransactionsView.class);
            menu.addNaviItem(rtgs, "Connectivity", RtgsConnectivityView.class);


            NaviItem csd = menu.addNaviItem(VaadinIcon.MONEY, "CSD", null);
         //   menu.addNaviItem(csd, "Monitoring", ListCsdTransactionsView.class);
            menu.addNaviItem(csd, "Connectivity", CsdListInstrumentsView.class);


//            NaviItem reporting = menu.addNaviItem(VaadinIcon.USERS, "Reporting", null);
//            menu.addNaviItem(reporting, "Transaction", ListTrancationsAccountOwnerView.class);


    }

    private void generateCommercialBankMenu(NaviMenu menu) throws Exception {

       User user = getUser();

       String userName = user.getUsername();
        AccountOwnerDto accountOwnerDto = null;

        try {
            accountOwnerDto = cantonDataService.getAccountOwner(userName);
       } catch(Exception e){
            e.printStackTrace();
       }
        if(!userName.equals("MAS") && !userName.equals("BI")) {
            menu.addNaviItem(VaadinIcon.HOME, "Dashboard", ListTrancationsAccountOwnerView.class);

            NaviItem transfers = menu.addNaviItem(VaadinIcon.CREDIT_CARD, "Transactions", null);

            if(accountOwnerDto!=null && accountOwnerDto.getBank().equalsIgnoreCase(accountOwnerDto.getSponsor())) {
                menu.addNaviItem(transfers, "CBDC Deposit", DepositView.class);
                menu.addNaviItem(transfers, "CBDC Withdraw", WithdrawView.class);
            }
            menu.addNaviItem(transfers, "CBDC Transfer", TransferView.class);

            menu.addNaviItem(transfers, "PVP Trade", PvpTradesView.class);
            menu.addNaviItem(transfers, "DVP Trade", DvpTradesView.class);
        }

        NaviItem reporting = menu.addNaviItem(VaadinIcon.COINS, "Reporting", null);
        menu.addNaviItem(reporting, "CBDC Balance", BalanceView.class);
        menu.addNaviItem(reporting, "CSD", BalanceViewCsd.class);

        NaviItem rtgs = menu.addNaviItem(VaadinIcon.USERS, "RTGS", null);
        menu.addNaviItem(rtgs, "Transactions", ListRtgsTransactionsView.class);

    }



    private void generateRtgsMenu(NaviMenu menu) throws Exception {
        User user = getUser();
        String userName = user.getUsername();
        AccountOwnerDto accountOwnerDto =  cantonDataService.getAccountOwner(userName);

        NaviItem transactions = menu.addNaviItem(VaadinIcon.COINS, "Reporting", null);
        menu.addNaviItem(transactions, "Transaction", ListTrancationsAccountOwnerView.class);
    }

    private void generateCsdMenu(NaviMenu menu) throws Exception {
        User user = getUser();
        String userName = user.getUsername();

        NaviItem cbdc = menu.addNaviItem(VaadinIcon.USERS, "CSD Management", null);
        menu.addNaviItem(cbdc, "Participants", ListCsdAccountOwnerView.class);
     //   RouteConfiguration configuration = RouteConfiguration.forApplicationScope();
       // configuration.setRoute("csd-domain-account-overview", ListCsdAccountOwnerView.class);
    }


    private User getUser(){
        return ((org.springframework.security.core.userdetails.User)SecurityContextHolder
                .getContext().getAuthentication().getPrincipal());
    }
    public void oldMenu(NaviMenu menu){


        NaviItem onboarding = menu.addNaviItem(VaadinIcon.USERS, "Create New", null);
        menu.addNaviItem(onboarding, "Bank", OnboardAccountOwnerView.class);
//
//            menu.addNaviItem(onboarding, "Account", OnboardAccountIdView.class);

        //    menu.addNaviItem(onboarding, "Approval Request", AccountOwnerApprovalView.class);
        //  menu.addNaviItem(onboarding, "Central Bank", OnboardCentralBankView.class);


        NaviItem networkManagement = menu.addNaviItem(VaadinIcon.CREDIT_CARD, "Network Management",
                null);  // disable account

        menu.addNaviItem(networkManagement, "AccountOwner", ListAccountOwnerView.class);

        NaviItem cbdcConnection = menu.addNaviItem(VaadinIcon.CREDIT_CARD, "CBDC Connection",
                null);  // disable account

        menu.addNaviItem(cbdcConnection, "Connectivity", CdbcIntegrationView.class);
        //  menu.addNaviItem(cbdcConnection, "View Status", CbdcConnectivityView.class);



        NaviItem centralSecDeposit = menu.addNaviItem(VaadinIcon.CREDIT_CARD, "Central Sec Deposit",
                null);  // disable account

        menu.addNaviItem(centralSecDeposit, "Operating Hours", CsdListInstrumentsView.class);
        menu.addNaviItem(centralSecDeposit, "Instrument Type", DepositView.class);


        NaviItem rtgsIntegration = menu.addNaviItem(VaadinIcon.CREDIT_CARD, "RTGS Integration",
                null);  // disable account
        menu.addNaviItem(rtgsIntegration, "Operating Hours", RtgsConnectivityView.class);
        //   menu.addNaviItem(rtgsIntegration, "Currency Code", DepositView.class);

        NaviItem reportOnTransactions = menu.addNaviItem(VaadinIcon.CREDIT_CARD, "Report on transactions",
                null);  // disable account
        menu.addNaviItem(reportOnTransactions, "Domestic", DepositView.class);
        menu.addNaviItem(reportOnTransactions, "Foreign", DepositView.class);
        menu.addNaviItem(reportOnTransactions, "PvP", DepositView.class);
        menu.addNaviItem(reportOnTransactions, "DvP", DepositView.class);

    }

    public AppBar getAppBar() {
        return appBar;
    }


    /**
     * Configure the app's inner and outer headers and footers.
     */
    private void initHeadersAndFooters() {
        appBar = new AppBar("", authenticatedUser, getUser(), cantonDataService.getCdbcOperator());

        // Tabbed navigation
        if (navigationTabs) {
            tabBar = new TabBar();
            UIUtils.setTheme(Lumo.DARK, tabBar);

            // Shift-click to add a new tab
            for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
                item.addClickListener(e -> {
                    if (e.getButton() == 0 && e.isShiftKey()) {
                        tabBar.setSelectedTab(tabBar.addClosableTab(item.getText(), item.getNavigationTarget()));
                    }
                });
            }
            appBar.getAvatar().setVisible(false);
            setAppHeaderInner(tabBar, appBar);

            // Default navigation
        } else {
            UIUtils.setTheme(Lumo.DARK, appBar);
            setAppHeaderInner( appBar);
        }
    }

    public NaviDrawer getNaviDrawer() {
        return naviDrawer;
    }

    private void setAppHeaderInner(Component... components) {
        if (appHeaderInner == null) {
            appHeaderInner = new Div();
            appHeaderInner.addClassName("app-header-inner");
            column.getElement().insertChild(0, appHeaderInner.getElement());
        }
        appHeaderInner.removeAll();
        appHeaderInner.add( components);
    }

    public static MainLayout get() {
        return (MainLayout) UI.getCurrent().getChildren()
                .filter(component -> component.getClass() == MainLayout.class)
                .findFirst().get();
    }


    @Override
    public void showRouterLayoutContent(HasElement content) {
        this.viewContainer.getElement().appendChild(content.getElement());
    }

    public static boolean hasRole (String roleName)
    {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
    }
}
