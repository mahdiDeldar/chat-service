package com.chubock.chatservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatStatisticsModel {

    private long unseenChatsCount;

}
