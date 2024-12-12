package org.hms;

import org.hms.database.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {

        try {
            DatabaseInitializer.initializeDatabase();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        View view = new View();
        Controller controller = new Controller(view);
        view.setController(controller);

        controller.start();
    }
}
