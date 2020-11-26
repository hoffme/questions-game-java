package com.questions.utils;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
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

    public static int selectWithIndex(String title, List<String> options) {
        StringBuilder text = new StringBuilder(title);
        for (int i = 0; i < options.size(); i++) {
            text.append("\t[").append(i).append("] ").append(options.get(i)).append("\n");
        }

        while (true) {
            int selected = Console.inputInt(text + "> ", false);

            if (selected >= 0 && selected < options.size()) return selected;

            Console.println("invalid option");
        }
    }

    public static void commander(Map<String, Command> commands) {
        while (true) {
            String[] cmd = Console.input("cmd: ").split(" ", 2);

            if (cmd[0].equals("exit")) break;
            if (cmd[0].equals("list")) {
                Console.println("\t> " + String.join("\n\t> ", commands.keySet()));
                continue;
            }

            if (commands.containsKey(cmd[0])) commands.get(cmd[0]).command(cmd.length == 1 ? "" : cmd[1]);
            else Console.println("invalid command");
        }
    }
}
