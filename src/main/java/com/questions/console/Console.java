package com.questions.console;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class Console {

    public static final Scanner reader = new Scanner(System.in);
    public static final PrintStream writer = System.out;

    public static void select(String title, List<OptionSelect> options) {
        while (true) {
            String input = Console.input(title);

            if (input.equals("return")) break;

            for (OptionSelect option : options) {
                if (input.toLowerCase().equals(option.getTitle().toLowerCase())) {
                    option.getCallback().selected();
                    return;
                }
            }
            Console.writer.println("invalid option");
        }
    }

    public static String input(String title) {
        Console.writer.print(title);
        return Console.reader.nextLine();
    }

    public static int inputInt(String title) {
        String input = Console.input(title);
        try { return Integer.parseInt(input); } catch (Exception ignored) {}
        return 0;
    }
}
