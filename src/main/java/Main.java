import com.questions.App;
import com.questions.utils.Console;

public class Main {
    public static void main(String[] args) {
        App app = new App();

        String selected = Console.select("> Select method: ", new String[]{"Host", "Client"});
        switch (selected.toLowerCase()) {
            case "host" -> app.startHost();
            case "client" -> app.startClient();
        }
    }
}
