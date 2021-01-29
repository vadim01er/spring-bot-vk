package com.vadim01er.springbotvk.client.answers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class VkResponse {
    private String type;
    @JsonProperty("object")
    private VkObject vkObject;

    @Data
    public static class VkObject {
        private Message message;
        @JsonProperty("client_info")
        private ClientInfo clientInfo;

        @Data
        public class ClientInfo {
            @JsonProperty("button_actions")
            private List<String> buttonActions;
            private boolean keyboard;
            @JsonProperty("inline_keyboard")
            private boolean inlineKeyboard;
            private boolean carousel;
            @JsonProperty("lang_id")
            private int langId;
        }
    }

}
