package org.hms.pages;

import java.util.HashMap;

public class GuestPanelPage extends PageBase {
    public GuestPanelPage(String name) {
        super(name, new HashMap<>());
        super.getOptions().put(1, "Add New Booking");
        super.getOptions().put(2, "View Available Rooms");
        super.getOptions().put(3, "View My Bookings");
        super.getOptions().put(4, "Cancel Booking");
        super.getOptions().put(8, "Log Out And Return To Main Menu");
        super.getOptions().put(9, "Exit");
    }
}
