package org.hms;

import org.hms.database.DataSource;
import org.hms.database.User;
import org.hms.input.handlers.LandingPageInputHandler;
import org.hms.input.handlers.LoginPageInputHandler;
import org.hms.input.handlers.SignupPageInputHandler;
import org.hms.pages.PageBase;

public class Controller {
    private final View view;

    private LandingPageInputHandler landingPageInputHandler;
    private LoginPageInputHandler loginPageInputHandler;
    private SignupPageInputHandler signupPageInputHandler;

    private User userType = User.NULL;
    private DataSource currentDataSource = null;

    private void initializeInputHandlers(View view) {
        landingPageInputHandler = new LandingPageInputHandler(view);
        loginPageInputHandler = new LoginPageInputHandler(view);
        signupPageInputHandler = new SignupPageInputHandler(view);
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
