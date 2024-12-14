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
        super.getOptions().put(7, "Add Room");
        super.getOptions().put(8, "Display Rooms");
        super.getOptions().put(9, "Delete Room");
        super.getOptions().put(10, "Manage Room Status");
        super.getOptions().put(11, "Add Room Type");
        super.getOptions().put(12, "Display Room Types");
        super.getOptions().put(13, "Delete Room Type");
        super.getOptions().put(14, "Log Out and Return to Main Menu");
        super.getOptions().put(15, "Exit");
    }
}
