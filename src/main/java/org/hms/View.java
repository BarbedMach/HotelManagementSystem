package org.hms;

import org.hms.pages.*;

import java.util.HashMap;

public class View {
    private HashMap<String, PageBase> pages;
    private PageBase currentPage;

    private Controller controller = null;

    private void insertPage(PageBase page) {
        pages.put(page.getName(), page);
    }

    private void initializePages() {
        pages = new HashMap<>();
        insertPage(new LandingPage("LANDING"));
        insertPage(new LoginPage("LOGIN"));
        insertPage(new SignupPage("SIGN UP"));
        insertPage(new AdminPanelPage("ADMINISTRATOR"));
        insertPage(new GuestPanelPage("GUEST"));
        insertPage(new HousekeeperPanelPage("HOUSEKEEPER"));
        insertPage(new ReceptionistPanelPage("RECEPTIONIST"));
    }

    public View() {
        initializePages();
        currentPage = pages.get("LANDING");
        display();
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public PageBase getPage(String name) {
        return pages.get(name);
    }

    public PageBase getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(PageBase currentPage) {
        this.currentPage = currentPage;
    }

    public void display() {
        currentPage.display();
    }

    public void display(String pageName) {
        currentPage = pages.get(pageName);
        display();
    }
}
