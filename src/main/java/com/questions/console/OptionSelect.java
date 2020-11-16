package com.questions.console;

public class OptionSelect {

    private final String title;
    private final EventSelect callback;

    public OptionSelect(String title, EventSelect callback) {
        this.title = title;
        this.callback = callback;
    }

    public String getTitle() {
        return title;
    }

    public EventSelect getCallback() {
        return callback;
    }
}
