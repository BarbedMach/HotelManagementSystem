package org.hms.pages;

import java.util.HashMap;

public class SignupPage extends PageBase {
    public SignupPage(String name) {
        super(name, new HashMap<>());
        super.getOptions().put(1, "Enter Username");
        super.getOptions().put(2, "Enter Password");
        super.getOptions().put(3, "Enter Phone Number");
        super.getOptions().put(4, "Proceed to Sign Up");
        super.getOptions().put(8, "Return to Main Menu");
        super.getOptions().put(9, "Exit");
    }
}
