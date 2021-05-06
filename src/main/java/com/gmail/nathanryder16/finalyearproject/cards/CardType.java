package com.gmail.nathanryder16.finalyearproject.cards;

import lombok.Getter;

public enum CardType {

    BUTTON("cards/button.html", "/css/button.css", "/js/button.js"),
    BADGE("cards/badge.html", "/css/badge.css", "/js/badge.js");

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
