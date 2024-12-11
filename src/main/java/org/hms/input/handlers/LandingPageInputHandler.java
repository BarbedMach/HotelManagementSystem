package org.hms.input.handlers;

import org.hms.View;

import java.util.Scanner;

public class LandingPageInputHandler extends InputHandlerBase {

    public LandingPageInputHandler(View view) {
        super(view);
    }

    @Override
    public void handleInput() {
        boolean terminated = false;
        Scanner scanner = new Scanner(System.in);

        while (!terminated) {
            System.out.print("Enter number: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Enter number: ");
                scanner.next();
            }

            int number = scanner.nextInt();

            switch (number) {
                case 1 -> {
                    view.display("LOGIN");
                    terminated = true;
                }
                case 2 -> {
                    view.display("SIGN UP");
                    terminated = true;
                }
                case 9 -> System.exit(0);
            }
        }
    }
}
