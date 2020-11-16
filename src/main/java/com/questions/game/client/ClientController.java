package com.questions.game.client;

import com.questions.console.Console;
import com.questions.game.EventChangeMode;

public class ClientController {

    public EventChangeMode changeMode;
    public ClientConfig config = new ClientConfig();

    private Client client;
    private int points;

    public void start() {
        this.config.configure();
        this.startClient();
    }

    public void start(ClientConfig config) {
        this.config = config;
        this.startClient();
    }

    private void startClient() {
        Console.writer.print("login ...");
        try { this.client = new Client(this.config); }
        catch (ClientError err) {
            Console.writer.println(err.getMessage());
            return;
        }
        Console.writer.println(" successfully");
        this.points = 0;

        this.client.finishRound = this::finishRound;
        this.client.newQuestion = this::newQuestion;
        this.client.result = this::result;
        this.client.newRound = this::newRound;

        Console.writer.println("waiting the round ...");

        this.client.start();

        try { this.client.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    private void newQuestion(int id, String title, String type) {
        Console.writer.println("ALERT!!! new question with id["+id+"]: \n\n\t" + title + "\n");

        String answer = Console.input("answer: ");

        this.client.sendAnswer(id, answer);
        Console.writer.println("answer sent\n");
    }

    private void result(boolean correct, String username, String answerCorrect) {
        Console.writer.println("the user response correct is " + username + " and answer is " + answerCorrect);

        if (correct) {
            this.points++;
            Console.writer.println("You have response correct [" + this.points + " points]");
        }
    }

    private void finishRound(boolean win) {
        Console.writer.println(win ? "You have won, Congratulations!!!" : "You lost, nice try");
    }

    private void newRound(String host, Integer port) {
        try { client.close(); }
        catch (ClientError clientError) {
            Console.writer.println("failed to close client: " + clientError.getMessage());
        }

        this.config.setConnectionHost(host);
        this.config.setConnectionPort(port);

        Console.writer.println("new round in " + host + ":" + port);
        this.start(this.config);
    }
}
