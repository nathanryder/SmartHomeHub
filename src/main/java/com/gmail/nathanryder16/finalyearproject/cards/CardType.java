package com.gmail.nathanryder16.finalyearproject.cards;

import lombok.Getter;

public enum CardType {

    BUTTON("test.html", "/css/testcard.css", "/js/testcard.js");

    private @Getter String filename;
    private @Getter String css;
    private @Getter String js;

    CardType(String filename, String css, String js) {
        this.filename = filename;
        this.css = css;
        this.js = js;
    }

    @Override
    public String toString() {
        String name = name().replace("_", " ");
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

        return name;
    }

}
