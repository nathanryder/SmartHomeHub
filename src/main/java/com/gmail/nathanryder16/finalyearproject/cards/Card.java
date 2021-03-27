package com.gmail.nathanryder16.finalyearproject.cards;

import lombok.Getter;

import java.io.InputStream;
import java.util.Scanner;

public class Card {

    private @Getter String file;-

    public Card(String file) {
        this.file = file;
    }

    public String getFileContents() {

        InputStream is = getClass().getClassLoader().getResourceAsStream("static/cards/" + file + ".html");
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        String data = scanner.hasNext() ? scanner.next() : "";

        return data;
    }


}
