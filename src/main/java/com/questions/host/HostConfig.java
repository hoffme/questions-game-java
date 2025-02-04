package com.questions.host;

import com.questions.utils.Console;
import com.questions.utils.Parser;
import com.questions.host.questionnaire.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HostConfig {

    private String questionsPath = "./data.json";
    private List<Question> questions = new ArrayList<>();
    private String host = "localhost";
    private int port = 3000;

    public void configure() {
        String input;
        int inputInt;

        while (true) {
            String defaultQuestionsPath = (this.questionsPath.length() > 0) ? "[" + this.questionsPath + "]" : "";
            input = Console.input("> question path "+defaultQuestionsPath+": ");
            if (input.length() > 0) this.questionsPath = input;

            try {
                this.loadQuestions();
                break;
            } catch (IOException e) {
                Console.writer.println("error on load questions: " + e.getMessage());
            }
        }

        String defaultHost = (this.host.length() > 0) ? "[" + this.host + "]" : "";
        input = Console.input("> host "+defaultHost+": ");
        if (input.length() > 0) this.host = input;

        String defaultPort = (this.port > 0) ? "[" + this.port + "]" : "";
        inputInt = Console.inputInt("> port "+defaultPort+": ");
        if (inputInt > 0) this.port = inputInt;
    }

    public void loadQuestions() throws IOException {
        this.questions = Parser.load(this.questionsPath);
    }

    public String getQuestionsPath() {
        return questionsPath;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
