package game.host;

import console.Console;
import game.questions.Loader;
import game.questions.Question;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class HostConfig {

    private String questionsPath = "/home/hoffme/Freelancer/questions-server-java/data.json";
    private List<Question> questions = new LinkedList<>();
    private String host = "localhost";
    private int port = 3000;

    public void configure() throws IOException {
        String defaultQuestionsPath = (this.questionsPath.length() > 0) ? "[" + this.questionsPath + "]" : "";
        this.questionsPath = Console.input("> question path "+defaultQuestionsPath+": ");

        String defaultHost = (this.host.length() > 0) ? "[" + this.host + "]" : "";
        this.host = Console.input("> host "+defaultHost+": ");

        String defaultPort = (this.port > 0) ? "[" + String.valueOf(this.port) + "]" : "";
        this.port = Console.inputInt("> port "+defaultPort+": ");

        this.questions = new Loader().load(this.questionsPath);
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
