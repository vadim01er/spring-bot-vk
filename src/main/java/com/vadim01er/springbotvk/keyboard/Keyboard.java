package com.vadim01er.springbotvk.keyboard;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Keyboard {
    @JsonProperty("one_time")
    private boolean oneTime = false;
    @JsonProperty("buttons")
    private List<List<ButtonLink>> buttons;

    @JsonAutoDetect
    public class Button extends ButtonLink {
        @JsonProperty("color")
        private String color;

        public Button(Action action, String color) {
            super(action);
            this.color = color;
        }
    }

    @JsonAutoDetect
    public class ButtonLink {
        @JsonProperty("action")
        private Action action;

        public ButtonLink(Action action) {
            this.action = action;
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

    public enum Color {
        NEGATIVE("negative"),
        POSITIVE("positive"),
        PRIMARY("primary"),
        SECONDARY("secondary");

        Color(String color) {
            this.color = color;
        }

        private final String color;

        public String  getColor() {
            return color;
        }
    }

    public Keyboard() {
        this.buttons = new ArrayList<>();
    }

    public Keyboard addButtons(String[] names, boolean haveBack) {
        for (String name : names) {
            ArrayList<ButtonLink> list = new ArrayList<>();
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
            ArrayList<ButtonLink> list = new ArrayList<>();
            list.add(new ButtonLink(new ActionLink("open_link", names[i], links[i])));
            buttons.add(list);
        }
        return this;
    }

    public Keyboard addButtonsInLine(String[][] names, boolean haveBack) {
        for (String[] name : names) {
            ArrayList<ButtonLink> list = new ArrayList<>();
            for (String s : name) {
                list.add(new Button(new Action("text", s), Color.POSITIVE.getColor()));
            }
            buttons.add(list);
        }
        if (haveBack) {
            addButtonBack();
        }
        return this;
    }

    public Keyboard addButtonNegative(String text, Color color) {
        buttons.add(new ArrayList<>() {{add(new Button(new Action("text", text), color.getColor()));}});
        return this;
    }

    public Keyboard addButtonBack() {
        buttons.add(
                new ArrayList<>() {{
                    add(new Button(new Action("text", "Назад"), Color.NEGATIVE.getColor()));
                }}
                );
        return this;
    }

}
