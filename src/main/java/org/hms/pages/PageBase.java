package org.hms.pages;

import java.util.HashMap;

public abstract class PageBase {

    private final String name;
    private final HashMap<Integer, String> options;

    public PageBase(String name, HashMap<Integer, String> options) {
        this.name = name;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public HashMap<Integer, String> getOptions() {
        return options;
    }

    public void display() {
        int totalWidth = 29;
        int leftPad = (totalWidth - name.length()) / 2;
        int rightPad = totalWidth - name.length() - leftPad;

        System.out.println("""
                *******************************
                *   HOTEL MANAGEMENT SYSTEM   *
                *******************************"""
        );
        System.out.printf("*%" + leftPad + "s%s%" + rightPad + "s*%n", "", name, "");
        System.out.println("*******************************");
        for (Integer key : options.keySet()) {
            System.out.println("- " + key + ": " + options.get(key));
        }
        System.out.println();
    }
}
