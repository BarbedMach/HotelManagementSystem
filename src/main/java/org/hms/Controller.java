package org.hms;

import org.hms.database.DataSource;
import org.hms.database.User;
import org.hms.input.handlers.*;
import org.hms.pages.PageBase;

public class Controller {
    private final View view;

    private LandingPageInputHandler landingPageInputHandler;
    private LoginPageInputHandler loginPageInputHandler;
    private SignupPageInputHandler signupPageInputHandler;
    private AdminPanelPageInputHandler adminPanelPageInputHandler;
    private GuestPanelInputHandler guestPanelInputHandler;
    private ReceptionistPanelInputHandler receptionistPanelInputHandler;

    private User userType = User.NULL;

    private String username = "";
    private String password = "";

    private DataSource currentDataSource = null;

    private void initializeInputHandlers(View view) {
        landingPageInputHandler = new LandingPageInputHandler(view);
        loginPageInputHandler = new LoginPageInputHandler(view);
        signupPageInputHandler = new SignupPageInputHandler(view);
        adminPanelPageInputHandler = new AdminPanelPageInputHandler(view);
        guestPanelInputHandler = new GuestPanelInputHandler(view);
        receptionistPanelInputHandler = new ReceptionistPanelInputHandler(view);
    }

    public Controller(View view) {
        this.view = view;
        initializeInputHandlers(this.view);
    }

    private void handleUserInput() {
        PageBase currentPage = view.getCurrentPage();

        if (currentPage.equals(view.getPage("LANDING"))) {
            landingPageInputHandler.handleInput();
        }

        if (currentPage.equals(view.getPage("LOGIN"))) {
            loginPageInputHandler.handleInput();
        }

        if (currentPage.equals(view.getPage("SIGN UP"))) {
            signupPageInputHandler.handleInput();
        }

        if (currentPage.equals(view.getPage("ADMINISTRATOR"))) {
            adminPanelPageInputHandler.handleInput();
        }

        if (currentPage.equals(view.getPage("GUEST"))){
            guestPanelInputHandler.handleInput();
        }

        if (currentPage.equals(view.getPage("RECEPTIONIST"))) {
            receptionistPanelInputHandler.handleInput();
        }

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserType(User userType) {
        this.userType = userType;
    }

    public User getUserType() {
        return userType;
    }

    public DataSource getCurrentDataSource() {
        return currentDataSource;
    }

    public void setCurrentDataSource(DataSource currentDataSource) {
        this.currentDataSource = currentDataSource;
    }

    public void start() {
        while(true) {
            handleUserInput();
        }
    }
}
