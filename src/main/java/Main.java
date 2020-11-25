import com.questions.peer.Client;
import com.questions.peer.Host;
import com.questions.peer.Peer;
import com.questions.peer.Config;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Config config = Config.fromArgs(args);

        Peer peer = null;
        try { peer = new Peer(config.username, config.host, config.port); }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Host hostUI = new Host(peer, config.questions);
        Client clientUI = new Client(peer);

        hostUI.clientUI = clientUI;
        clientUI.hostUI = hostUI;

        if (config.modeClient) clientUI.start();
        else hostUI.start();

        System.exit(0);
    }
}
