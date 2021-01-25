package com.vadim01er.springbotvk.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageResponse {
    @JsonProperty("peer_id")
    private long peerId;
}
