package com.questions.red;

import com.questions.CommandOuterClass.Command;

public interface NeighbourListener {
    void peerCommand(Neighbour neighbour, Command cmd);
    void peerDisconnected(Neighbour neighbour);
}
