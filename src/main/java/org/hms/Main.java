package org.hms;

import org.hms.pages.LandingPage;

public class Main {
    public static void main(String[] args) {
        View view = new View();
        Controller controller = new Controller(view);
        controller.start();
    }
}
