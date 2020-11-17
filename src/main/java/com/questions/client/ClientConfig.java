package com.questions.client;

import com.questions.utils.Console;

public class ClientConfig {

    private String userName = "myUsername";
    private String connectionHost = "localhost";
    private int connectionPort = 3000;

    public void configure() {
        String input;
        int inputInt;

        String defaultUsername = (this.userName.length() > 0) ? "[" + this.userName + "]" : "";
        input = Console.input("> username "+defaultUsername+": ");
        if (input.length() > 0) this.userName = input;

        String defaultHost = (this.connectionHost.length() > 0) ? "[" + this.connectionHost + "]" : "";
        input = Console.input("> host "+defaultHost+": ");
        if (input.length() > 0) this.connectionHost = input;

        String defaultPort = (this.connectionPort > 0) ? "[" + this.connectionPort + "]" : "";
        inputInt = Console.inputInt("> port "+defaultPort+": ");
        if (inputInt > 0) this.connectionPort = inputInt;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setConnectionHost(String connectionHost) {
        this.connectionHost = connectionHost;
    }

    public void setConnectionPort(int connectionPort) {
        this.connectionPort = connectionPort;
    }
}
