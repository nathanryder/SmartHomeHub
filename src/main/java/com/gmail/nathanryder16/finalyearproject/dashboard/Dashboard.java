package com.gmail.nathanryder16.finalyearproject.dashboard;

import com.gmail.nathanryder16.finalyearproject.SpringContext;
import com.gmail.nathanryder16.finalyearproject.cards.Card;
import com.gmail.nathanryder16.finalyearproject.cards.CardType;
import com.gmail.nathanryder16.finalyearproject.model.Device;
import com.gmail.nathanryder16.finalyearproject.repository.DeviceRepository;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard {

    private static @Getter Map<String, Dashboard> dashboards = new HashMap<>();

    private File dashboard;

    public static Dashboard findDashboard(String userid) {
        if (dashboards.containsKey(userid)) {
            return dashboards.get(userid);
        }

        return new Dashboard(userid);
    }

    private Dashboard(String userid) {
        File folder = new File("dashboards");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        this.dashboard = new File(folder.getPath() + File.separator + userid + ".yml");
        if (!dashboard.exists()) {
            try {
                dashboard.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dashboards.put(userid, this);
    }

    @SuppressWarnings("unchecked cast")
    public List<Card> getCards() throws FileNotFoundException {

        List<Card> cards = new ArrayList<>();
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(new FileInputStream(dashboard));
        if (root == null) {
            return cards;
        }

        Map<String, Object> rawCards = (Map<String, Object>) root.get("cards");

        for (String key : rawCards.keySet()) {
            Map<String, Object> cardData = (Map<String, Object>) rawCards.get(key);

            String rawType = (String) cardData.get("type");
            CardType cardType = CardType.valueOf(rawType.toUpperCase());

            DeviceRepository deviceRepo = SpringContext.getBean(DeviceRepository.class);
            Device device = deviceRepo.findByDeviceID((String) cardData.get("id"));
            if (device == null) {
                deleteCard(key);
            }

            Card card = new Card(cardType, device);
            cards.add(card);
        }

        return cards;
    }

    @SuppressWarnings("unchecked cast")
    public void moveCardUp(String cardID) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(new FileInputStream(dashboard));
        if (root == null) {
            root = new HashMap<>();
        }

        Map<String, Object> cards = (Map<String, Object>) root.get("cards");
        if (root.get("cards") == null) {
            return;
        }

        Map<String, Object> lastCard = null;
        String lastId = "";
        for (String key : cards.keySet()) {
            Map<String, Object> card = (Map<String, Object>) cards.get(key);

            if (lastCard == null) {
                lastCard = card;
                lastId = key;
                continue;
            }

            if (key.equals(cardID)) {
                cards.put(cardID, lastCard);
                cards.put(lastId, card);
                break;
            }

            lastCard = card;
            lastId = key;
        }

        root.put("cards", cards);

        yaml.dump(root, new PrintWriter(dashboard));
    }

    @SuppressWarnings("unchecked cast")
    public void moveCardDown(String cardID) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(new FileInputStream(dashboard));
        if (root == null) {
            root = new HashMap<>();
        }

        Map<String, Object> cards = (Map<String, Object>) root.get("cards");
        if (root.get("cards") == null) {
            return;
        }

        Map<String, Object> lastCard = null;
        boolean found = false;
        for (String key : cards.keySet()) {
            Map<String, Object> card = (Map<String, Object>) cards.get(key);

            if (found) {
                if (lastCard == null) {
                    break;
                }

                cards.put(key, lastCard);
                cards.put(cardID, card);
                break;
            }

            if (key.equals(cardID)) {
                found = true;
            }

            lastCard = card;
        }

        root.put("cards", cards);

        yaml.dump(root, new PrintWriter(dashboard));
    }

    public void deleteCard(String cardID) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(new FileInputStream(dashboard));
        if (root == null) {
            return;
        }

        if (root.get("cards") == null) {
            return;
        }
        Map<String, Object> cards = (Map<String, Object>) root.get("cards");;
        cards.remove(cardID);

        root.put("cards", cards);

        yaml.dump(root, new PrintWriter(dashboard));
    }

    public void addCard(Card card) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(new FileInputStream(dashboard));
        if (root == null) {
            root = new HashMap<>();
        }

        Map<String, Object> cards = null;
        if (root.get("cards") == null) {
            cards = new HashMap<>();
        } else {
            cards = (Map<String, Object>) root.get("cards");
        }

        Map<String, Object> cardMap = new HashMap<>();
        cardMap.put("type", card.getType().toString());
        cardMap.put("id", card.getDevice().getDeviceID());
        cards.put(getNextId(cards), cardMap);

        root.put("cards", cards);

        yaml.dump(root, new PrintWriter(dashboard));
    }

    public String getNextId(Map<String, Object> cards) {
        int value = 0;
        for (String id : cards.keySet()) {
            int check = Integer.parseInt(id);
            if (check > value)
                value = check;
        }

        return String.valueOf(value+1);
    }

}
