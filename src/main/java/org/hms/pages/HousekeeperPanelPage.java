package org.hms.pages;

import java.util.HashMap;

public class HousekeeperPanelPage extends PageBase {
    public HousekeeperPanelPage(String name) {
        super(name, new HashMap<>());
        super.getOptions().put(1, "View Pending Housekeeping Tasks");
        super.getOptions().put(2, "View Completed Housekeeping Tasks");
        super.getOptions().put(3, "Update Task Status to Completed");
        super.getOptions().put(4, "View My Cleaning Schedule");
        super.getOptions().put(9, "Exit");
    }
}
