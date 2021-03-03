package com.vadim01er.springbotvk.client.answers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public Message(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attachment {
        private String type;
        private Photo photo;
        private Video video;
        private Doc doc;
        private Wall wall;

        public static class Builder {
            private Attachment attachment;

            public Builder() {
                this.attachment = new Attachment();
            }

            public Builder addType(String type) {
                this.attachment.setType(type);
                return this;
            }

            public Builder addPhoto(Photo photo) {
                this.attachment.setPhoto(photo);
                return this;
            }
            public Builder addPhoto(int ownerId, int mediaId) {
                this.attachment.setPhoto(new Photo(ownerId, mediaId));
                return this;
            }
            public Builder addVideo(Video video) {
                this.attachment.setVideo(video);
                return this;
            }
            public Builder addDoc(int ownerId, int mediaId) {
                this.attachment.setDoc(new Doc(ownerId, mediaId));
                return this;
            }
            public Builder addWall(Wall wall) {
                this.attachment.setWall(wall);
                return this;
            }
            public Attachment createAttachment() {
                return this.attachment;
            }
        }

        @Data
        public static class Photo {
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

            public Photo(int ownerId, int mediaId) {
                this.ownerId = ownerId;
                this.id = mediaId;
            }
        }

        @Data
        public class Video {
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

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Doc {
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

            public Doc(int ownerId, int mediaId) {
                this.ownerId = ownerId;
                this.id = mediaId;
            }
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public class Wall {
            private int id;
            @JsonProperty("to_id")
            private int toId;
            @JsonProperty("access_key")
            private String accessKey;
        }
    }


}
