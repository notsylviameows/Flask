package io.github.sylviameows.flask.editor.book.components;

import net.kyori.adventure.text.Component;

public class BookLabel implements BookComponent {
    private final Component label;

    public BookLabel(Component label) {
        this.label = label;
    }

    @Override
    public Component label() {
        return label;
    }
}
