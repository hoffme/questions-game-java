package com.questions.client;

import com.questions.game.Commands;
import com.questions.utils.Console;

public class ClientController implements EventsClient {

    public EventWinner eventWinner;
    public ClientConfig config = new ClientConfig();

    private Client client;
    private int points;

    public void start(ClientConfig config) {
        this.config = config;

        Console.writer.print("login ... ");
        try { this.client = new Client(this.config, this); }
        catch (Exception err) {
            Console.writer.println(err.getMessage());
            return;
        }
        Console.writer.println("successfully");

        this.points = 0;

        Console.writer.println("waiting the round ...");
        this.client.start();
    }

    public void waitFinish() {
        try { this.client.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    public void notifyHostReady(String host, Integer port) throws ClientError {
        this.client.sendHostRound(host, port);
    }

    @Override
    public void finish(Commands.Finish finish) {
        Console.writer.println(finish.getWin() ? "You have won, Congratulations!!!" : "You lost, nice try");

        if (finish.getWin() && this.eventWinner != null) this.eventWinner.win();
    }

    @Override
    public void changeHostRound(Commands.ChangeHostRound change) {
        try { client.close(); }
        catch (ClientError clientError) {
            Console.writer.println("failed to close client: " + clientError.getMessage());
        }

        this.config.setConnectionHost(change.getHost());
        this.config.setConnectionPort(change.getPort());

        Console.writer.println("new round in " + change.getHost() + ":" + change.getPort());
        this.start(this.config);
    }

    @Override
    public void question(Commands.Question question) {
        Console.writer.println("ALERT!!! new question with id["+question.getId()+"]: \n\n\t" + question.getTitle() + "\n");

        String answer = Console.input("answer: ");

        try {
            this.client.sendAnswer(question.getId(), answer);
            Console.writer.println("answer sent\n");
        } catch (ClientError err) {
            Console.writer.println("error: " + err.getMessage() + "\n");
        }
    }

    @Override
    public void result(Commands.Result result) {
        Console.writer.println("the answer correct is " + result.getAnswer());

        if (result.getMe()) {
            this.points++;
            Console.writer.println("You have response correct [" + this.points + " points]");
        }
    }

    public void stop() {
        try {
            this.client.close();
        } catch (ClientError clientError) {
            Console.writer.println("error to close client: " + clientError.getMessage());
        }
    }
}
