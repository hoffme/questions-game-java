package com.questions.red;

import com.questions.CommandOuterClass.Command;

public interface EventReceive {
    void command(Neighbour neighbour, Command cmd);
}
