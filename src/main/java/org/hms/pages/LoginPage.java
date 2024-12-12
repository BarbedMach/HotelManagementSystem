package org.hms.pages;

import java.util.HashMap;

public class LoginPage extends PageBase {
    public LoginPage(String name) {
        super(name, new HashMap<>());
        super.getOptions().put(1, "Enter Username");
        super.getOptions().put(2, "Enter Password");
        super.getOptions().put(3, "Proceed to Login");
        super.getOptions().put(8, "Return to Main Menu");
        super.getOptions().put(9, "Exit");
    }
}
