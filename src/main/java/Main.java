import console.Console;
import console.OptionSelect;
import game.client.Client;
import game.client.ClientController;
import game.host.HostController;
import game.host.RoundHost;
import game.questions.Loader;
import game.questions.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args)  {
        HostController host = new HostController();
        ClientController client = new ClientController();

        host.changeMode = client::start;
        client.changeMode = host::start;

        ArrayList<OptionSelect> options = new ArrayList<>();
        options.add(new OptionSelect("Host", host::start));
        options.add(new OptionSelect("Client", client::start));

        Console.select("> Select method (Host, Client): ", options);
    }
}
