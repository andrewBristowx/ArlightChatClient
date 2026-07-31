package com.arlight.chatclient;

public record EmoteDefinition(String alias, String glyph, boolean animated,
                              String groupId, String groupName) {
}
