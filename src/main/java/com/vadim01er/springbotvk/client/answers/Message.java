package com.vadim01er.springbotvk.client.answers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class Message {
    private long date;
    @JsonProperty("from_id")
    private int fromId;
    private int id;
    private int out;
    @JsonProperty("peer_id")
    private int peerId;
    private String text;
    @JsonProperty("conversation_message_id")
    private int conversationMessageId;
    private List<Attachment> attachments;
    private String payload;

    @Data
    public static class Attachment {
        private String type;
        private Photo photo;

        @Data
        public class Photo {
            @JsonProperty("album_id")
            private int albumId;
            private long date;
            private int id;
            @JsonProperty("owner_id")
            private int ownerId;
            @JsonProperty("has_tags")
            private boolean hasTags;
            @JsonProperty("access_key")
            private String accessKey;
            private List<Object> sizes;
        }
    }


}
