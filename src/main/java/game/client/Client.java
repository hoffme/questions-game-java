package game.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private final PrintWriter printer;
    private final BufferedReader reader;

    public Client(ClientConfig config) throws ClientError {
        Socket socket;

        try {
            socket = new Socket(config.getHost(), config.getPort());
        } catch (IOException e) {
            throw new ClientError("cannot connect to: " + config.getHost() + ":" + config.getPort());
        }

        try {
            this.printer = new PrintWriter(socket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ClientError("cannot connect to: " + config.getHost() + ":" + config.getPort());
        }

        this.send(config.getUserName());
        if (!this.receive().equals("ok")) throw new ClientError("error in conn");
    }

    public void send(String msg) {
        this.printer.println(msg);
    }

    public String receive() {
        try {
            return this.reader.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }
}
