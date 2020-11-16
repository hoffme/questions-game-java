import com.questions.console.Console;
import com.questions.console.OptionSelect;
import com.questions.game.client.ClientController;
import com.questions.game.host.HostController;

import java.util.ArrayList;

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
