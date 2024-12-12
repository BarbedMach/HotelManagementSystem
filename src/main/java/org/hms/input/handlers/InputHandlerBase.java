package org.hms.input.handlers;

import org.hms.View;

public abstract class InputHandlerBase {
    protected View view;

    public InputHandlerBase(View view) {
        this.view = view;
    }

    public abstract void handleInput();
}
