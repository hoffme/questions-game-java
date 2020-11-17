import com.questions.utils.Console;
import com.questions.client.ClientController;
import com.questions.host.HostController;

public class Main {
    public static void main(String[] args) {
        HostController host = new HostController();
        ClientController client = new ClientController();

        String selected = Console.select("> Select method: ", new String[]{"Host", "Client"});
        switch (selected.toLowerCase()) {
            case "host" -> host.start();
            case "client" -> client.start();
        }
    }
}
