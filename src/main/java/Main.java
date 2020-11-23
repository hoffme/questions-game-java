import com.questions.game.Peer;
import com.questions.game.PeerConfig;
import com.questions.utils.Console;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        PeerConfig config = PeerConfig.fromArgs(args);

        try {
            new Peer(config).waitToExit();
        } catch (IOException e) {
            Console.println("error to initialize peer: " + e.getMessage());
        }
    }
}
