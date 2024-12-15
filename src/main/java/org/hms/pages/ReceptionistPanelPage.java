package org.hms.pages;

import java.util.HashMap;

public class ReceptionistPanelPage extends PageBase {

    public ReceptionistPanelPage(String name) {
        super(name, new HashMap<>());
        super.getOptions().put(1, "Add New Reservation");
        super.getOptions().put(2, "View Bookings and Reservations");
        super.getOptions().put(3, "Delete Reservation");
        super.getOptions().put(4, "Assign Housekeeping Task");
        super.getOptions().put(5, "View All Housekeepers and Their Availability");
        super.getOptions().put(6, "Process Payment");
        super.getOptions().put(8, "Log Out and Return to Main Menu");
        super.getOptions().put(9, "Exit");
    }
}




