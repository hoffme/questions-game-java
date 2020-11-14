package game.client;

import console.Console;

public class ClientConfig {
    private String userName = "";
    private String connectionHost = "";
    private int connectionPort = 0;

    public void configure() {
        String defaultUsername = (this.userName.length() > 0) ? "[" + this.userName + "]" : "";
        this.userName = Console.input("> username "+defaultUsername+": ");

        String defaultHost = (this.connectionHost.length() > 0) ? "[" + this.connectionHost + "]" : "";
        this.connectionHost = Console.input("> host "+defaultHost+": ");

        String defaultPort = (this.connectionPort > 0) ? "[" + String.valueOf(this.connectionPort) + "]" : "";
        this.connectionPort = Console.inputInt("> port "+defaultPort+": ");
    }

    public String getUserName() {
        return userName;
    }

    public String getHost() {
        return connectionHost;
    }

    public int getPort() {
        return connectionPort;
    }
}
