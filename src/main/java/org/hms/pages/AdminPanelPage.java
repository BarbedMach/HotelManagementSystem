package org.hms.pages;

import java.util.HashMap;

public class AdminPanelPage extends PageBase {
    public AdminPanelPage(String name) {
        super(name, new HashMap<>());
        super.getOptions().put(1, "Add Hotel");
        super.getOptions().put(2, "Display Hotels");
        super.getOptions().put(3, "Delete Hotel");
        super.getOptions().put(4, "Add User");
        super.getOptions().put(5, "Display Users");
        super.getOptions().put(6, "Delete User");
        super.getOptions().put(14, "Log Out and Return to Main Menu");
        super.getOptions().put(15, "Exit");
    }
}
