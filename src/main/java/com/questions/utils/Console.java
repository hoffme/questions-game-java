package com.questions.utils;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class Console {

    private static final Scanner reader = new Scanner(System.in);
    private static final PrintStream writer = System.out;

    public static int select(String title, String[] options) {
        String text = title + " [" + String.join(", ", options) + "]: ";

        while (true) {
            String selected = Console.input(text);

            for (int i = 0; i < options.length; i++) {
                if (selected.toLowerCase().equals(options[i].toLowerCase())) return i;
            }

            Console.println("invalid option");
        }
    }

    public static String input(String title) {
        Console.print(title);
        return Console.reader.nextLine();
    }

    public static int inputInt(String title, boolean emptyValid) {
        while (true) {
            String input = Console.input(title);
            if (emptyValid && input.equals("")) return 0;

            try { return Integer.parseInt(input); }
            catch (Exception ignored) {
                Console.println("invalid int");
            }
        }
    }

    public static void print(String text) { Console.writer.print(text); }

    public static void println(String text) { Console.writer.println(text); }

    public static int selectNumber(String title, List<String> options) {
        while (true) {
            Console.println(title);
            for (int i = 0; i < options.size(); i++) Console.println("[" + i + "] " + options.get(i));

            int selected = Console.inputInt("> ", false);

            if (selected >= 0 && selected < options.size()) return selected;

            Console.println("invalid option");
        }
    }
}
