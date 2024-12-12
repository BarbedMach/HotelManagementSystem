package org.hms;

import org.hms.pages.LandingPage;
import org.hms.pages.LoginPage;
import org.hms.pages.PageBase;
import org.hms.pages.SignupPage;

import java.util.HashMap;

public class View {
    private HashMap<String, PageBase> pages;
    private PageBase currentPage;

    private void insertPage(PageBase page) {
        pages.put(page.getName(), page);
    }

    private void initializePages() {
        pages = new HashMap<>();
        insertPage(new LandingPage("LANDING"));
        insertPage(new LoginPage("LOGIN"));
        insertPage(new SignupPage("SIGN UP"));
    }

    public View() {
        initializePages();
        currentPage = pages.get("LANDING");
        display();
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
