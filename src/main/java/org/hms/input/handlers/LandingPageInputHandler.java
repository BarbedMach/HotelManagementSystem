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
                    terminated = true;
                    view.display("LOGIN");
                }
                case 2 -> {
                    terminated = true;
                    view.display("SIGN UP");
                }
                case 9 -> System.exit(0);
            }
        }
    }
}
