package com.questions.peer;

import com.questions.utils.Console;
import com.questions.utils.Parser;
import com.questions.quesionnaire.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {

    public boolean modeClient = true;
    public String questionsPath = "./data.json";
    public List<Question> questions = new ArrayList<>();
    public String username = "user";
    public String host = "localhost";
    public int port = 3000;

    public static Config fromArgs(String[] args) {
        Config config = new Config();

        boolean mode = false;
        boolean questions = false;
        boolean username = false;
        boolean host = false;
        boolean port = false;

        for (int i = 0; i < args.length; i += 2) {
            String key = args[i];
            String value = args[i+1];

            switch (key) {
                case "-m" -> {
                    mode = true;
                    config.modeClient = value.equals("client");
                }
                case "-u" -> {
                    username = true;
                    config.username = value;
                }
                case "-h" -> {
                    host = true;
                    config.host = value;
                }
                case "-p" -> {
                    port = true;
                    config.port = Integer.parseInt(value);
                }
                case "-q" -> {
                    questions = true;
                    config.questionsPath = value;
                    try {
                        config.loadQuestions();
                    } catch (IOException e) {
                        Console.println("error on load questions: " + e.getMessage());
                        System.exit(1);
                    }
                }
            }
        }

        if (!mode) config.configureMode();
        if (!questions) config.configureQuestionsPath();
        if (!username) config.configureUser();
        if (!host) config.configureHost();
        if (!port) config.configurePort();

        return config;
    }

    public void configureMode() {
        int indexOption = Console.select("> mode: ", new String[]{"host", "client"});
        this.modeClient = indexOption == 1;
    }

    public void configureQuestionsPath() {
        while (true) {
            String defaultQuestionsPath = (this.questionsPath.length() > 0) ? "[" + this.questionsPath + "]" : "";
            String input = Console.input("> question path "+defaultQuestionsPath+": ");
            if (input.length() > 0) this.questionsPath = input;

            try {
                this.loadQuestions();
                break;
            } catch (Exception e) {
                Console.println("error on load questions: " + e.getMessage());
            }
        }
    }

    public void configureUser() {
        String defaultUsername = (this.username.length() > 0) ? "[" + this.username + "]" : "";
        String input = Console.input("> username "+defaultUsername+": ");
        if (input.length() > 0) this.username = input;
    }

    public void configureHost() {
        String defaultHost = (this.host.length() > 0) ? "[" + this.host + "]" : "";
        String input = Console.input("> host "+defaultHost+": ");
        if (input.length() > 0) this.host = input;
    }

    public void configurePort() {
        String defaultPort = (this.port > 0) ? "[" + this.port + "]" : "";
        int input = Console.inputInt("> port "+defaultPort+": ", true);
        if (input > 0) this.port = input;
    }

    public void loadQuestions() throws IOException {
        this.questions = Parser.load(this.questionsPath);
    }
}
