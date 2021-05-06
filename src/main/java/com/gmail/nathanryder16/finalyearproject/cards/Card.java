package com.gmail.nathanryder16.finalyearproject.cards;

import com.gmail.nathanryder16.finalyearproject.model.Device;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.Scanner;

public class Card {

    private @Getter CardType type;
    private @Getter Device device;
    private @Getter @Setter String id;

    public Card(CardType type, Device device) {
        this.type = type;
        this.device = device;
    }

//    public String getFileContents() {
//
//        InputStream is = getClass().getClassLoader().getResourceAsStream("static/cards/" + type.getFilename());
//        Scanner scanner = new Scanner(is).useDelimiter("\\A");
//        String data = scanner.hasNext() ? scanner.next() : "";
//
//        return data;
//    }


}
