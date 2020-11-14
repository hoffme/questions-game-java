package game.host;

import java.util.List;

public interface EventWinner {
    void win(Peer peer, List<Answer> answers);
}
