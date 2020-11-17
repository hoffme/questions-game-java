package com.questions.utils;

import java.io.PrintStream;
import java.util.Scanner;

public class Console {

    public static final Scanner reader = new Scanner(System.in);
    public static final PrintStream writer = System.out;

    public static String select(String title, String[] options) {
        Console.writer.println(title);

        for (int i = 0; i < options.length; i++) {
            Console.writer.println(i+1 + " " + options[i]);
        }

        int input = Console.inputInt("select: ", 1, options.length + 1);
        return options[input - 1];
    }

    private static int inputInt(String title, int min, int max) {
        while (true) {
            int input = Console.inputInt(title);
            if (input >= min && input < max) return input;

            Console.writer.println("invalid input");
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
