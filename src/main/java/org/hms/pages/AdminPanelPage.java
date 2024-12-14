package org.hms.pages;

import java.util.HashMap;

public class AdminPanelPage extends PageBase {
    public AdminPanelPage(String name) {
        super(name, new HashMap<>());
        super.getOptions().put(1, "Add Hotel");
        super.getOptions().put(2, "Delete Hotel");
        super.getOptions().put(3, "Add Room");
        super.getOptions().put(4, "Delete Room");
        super.getOptions().put(5, "Manage Room Status");
        super.getOptions().put(6, "Add User Account");
        super.getOptions().put(7, "Delete User Account");
        super.getOptions().put(8, "View User Accounts");
        super.getOptions().put(9, "Generate Revenue Report");
        super.getOptions().put(10, "View All Booking Records");
        super.getOptions().put(11, "View All Housekeeping Records");
        super.getOptions().put(12, "View Most Booked Room Types");
        super.getOptions().put(13, "View All Employees");
        super.getOptions().put(14, "Log Out and Return to Main Menu");
        super.getOptions().put(15, "Exit");
    }
}
