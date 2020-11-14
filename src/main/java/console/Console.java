package console;

import java.util.List;
import java.util.Scanner;

public class Console {
    private static final Scanner scan = new Scanner(System.in);

    public static void select(String title, List<OptionSelect> options) {
        String input = "";
        while (true) {
            System.out.print(title);

            input = Console.scan.nextLine();
            if (input.equals("return")) break;

            for (OptionSelect option : options) {
                if (input.toLowerCase().equals(option.getTitle().toLowerCase())) {
                    try { option.getCallback().selected(); }
                    catch (Exception err) { System.out.println("error: " + err.getMessage()); }
                    return;
                }
            }
            System.out.println("invalid option");
        }
    }

    public static String input(String title) {
        System.out.print(title);
        return Console.scan.nextLine();
    }

    public static Integer inputInt(String title) {
        try {
            return Integer.parseInt(Console.input(title));
        } catch (Exception err) {
            System.out.println("invalid option");
        }
        return inputInt(title);
    }
}
