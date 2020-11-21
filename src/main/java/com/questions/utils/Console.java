package com.questions.utils;

import java.io.PrintStream;
import java.util.Scanner;

public class Console {

    public static final Scanner reader = new Scanner(System.in);
    public static final PrintStream writer = System.out;

    public static String select(String title, String[] options) {
        String text = title + " [" + String.join(", ", options) + "]: ";

        while (true) {
            String selected = Console.input(text);

            for (String option: options) {
                if (selected.toLowerCase().equals(option.toLowerCase())) return option;
            }

            Console.writer.println("invalid option");
        }
    }

    public static String input(String title) {
        Console.writer.print(title);
        return Console.reader.nextLine();
    }

    public static int inputInt(String title) {
        while (true) {
            String input = Console.input(title);
            if (input.equals("")) return 0;

            try { return Integer.parseInt(input); }
            catch (Exception ignored) {
                Console.writer.println("invalid int");
            }
        }
    }
}
