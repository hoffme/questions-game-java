import com.questions.game.Peer;
import com.questions.game.PeerConfig;

public class Main {
    public static void main(String[] args) {
        PeerConfig config = PeerConfig.fromArgs(args);

        Peer peer = new Peer(config);
        peer.waitToExit();
    }
}
