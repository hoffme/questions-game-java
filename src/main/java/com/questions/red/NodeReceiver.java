package com.questions.red;

import com.questions.CommandOuterClass.Command;

public interface NodeReceiver {
    void command(Neighbour neighbour, Command cmd);
}
