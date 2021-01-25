package com.vadim01er.springbotvk.keyboard;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Keyboard {
    @JsonProperty("one_time")
    private boolean oneTime = false;
    @JsonProperty("buttons")
    private List<List<Button>> buttons;

    @JsonAutoDetect
    public class Button {
        @JsonProperty("action")
        private Action action;
        @JsonProperty("color")
        private String color;

        public Button(Action action, String color) {
            this.action = action;
            this.color = color;
        }
    }

    @JsonAutoDetect
    public class Action {
        @JsonProperty("type")
        private String type;
        @JsonProperty("label")
        private String label;
        @JsonProperty("payload")
        private String payload = "{\"button\": \"1\"}";

        public Action(String type, String label) {
            this.type = type;
            this.label = label;
        }
    }

    @JsonAutoDetect
    public class ActionLink extends Action {
        @JsonProperty("link")
        private String link;

        public ActionLink(String type, String label, String link) {
            super(type, label);
            this.link = link;
        }
    }

    public Keyboard() {
        this.buttons = new ArrayList<>();
    }

    public Keyboard addButtons(String[] names, boolean haveBack) {
        for (String name : names) {
            ArrayList<Button> list = new ArrayList<>();
            list.add(new Button(new Action("text", name), "positive"));
            buttons.add(list);
        }
        if (haveBack) {
            addButtonBack();
        }
        return this;
    }

    public Keyboard addButtons(String[] names, String[] links, boolean haveBack) {
        for (int i = 0; i < links.length; i++) {
            ArrayList<Button> list = new ArrayList<>();
            list.add(new Button(new ActionLink("open_link", names[i], links[i]), "positive"));
            buttons.add(list);
        }
        System.arraycopy(names, links.length, names, 0, names.length - links.length);
        return addButtons(names, haveBack);
    }

    public Keyboard addButtonsInLine(String[][] names, boolean haveBack) {
        for (String[] name : names) {
            ArrayList<Button> list = new ArrayList<>();
            for (String s : name) {
                list.add(new Button(new Action("text", s), "positive"));
            }
            buttons.add(list);
        }
        if (haveBack) {
            addButtonBack();
        }
        return this;
    }

    private void addButtonBack() {
        buttons.add(new ArrayList<Button>() {{add(new Button(new Action("text", "Назад"), "negative"));}});
    }

}
