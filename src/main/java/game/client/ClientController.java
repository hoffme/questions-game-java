package game.client;

import console.Console;
import game.EventChangeMode;

import java.io.IOException;

public class ClientController {

    public EventChangeMode changeMode;
    public ClientConfig config = new ClientConfig();

    public void start() {
        this.config.configure();
        this.startClient();
    }

    public void start(ClientConfig config) {
        this.config = config;
        this.startClient();
    }

    private void startClient() {
        System.out.println("starting login");

        Client client;
        try {
            client = new Client(this.config);
        } catch (Exception clientError) {
            System.out.println("Can not connect to client" + clientError.getMessage());
            return;
        }

        while (true) {
            String[] status;

            System.out.println("waiting question ...");
            String question = client.receive();

            String answer = Console.input("> " + question + ": ");
            System.out.println("sending answer ...");
            client.send(answer);

            switch (client.receive()) {
                case "0" -> System.out.println("Answer correct");
                case "1" -> System.out.println("Answer incorrect");
                case "2" -> System.out.println("Answer later");
            }

            status = client.receive().split(";");

            if (status[0].equals("finish")) break;
        }

        System.out.println("changing");
    }
}
