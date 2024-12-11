package org.hms.pages;

import java.util.HashMap;

public class LandingPage extends PageBase {
    public LandingPage(String name) {
        super(name, new HashMap<>());
        super.getOptions().put(1, "Login");
        super.getOptions().put(2, "Sign Up");
        super.getOptions().put(9, "Exit");
    }
}
