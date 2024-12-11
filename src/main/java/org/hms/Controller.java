package org.hms;

import org.hms.input.handlers.LandingPageInputHandler;
import org.hms.pages.PageBase;

public class Controller {
    private final View view;
    private LandingPageInputHandler landingPageInputHandler;

    private void initializeInputHandlers(View view) {
        landingPageInputHandler = new LandingPageInputHandler(view);
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

    }

    public void start() {
        while(true) {
            handleUserInput();
        }
    }
}
