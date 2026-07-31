package com.arlight.chatclient;

public enum CosmeticAnchor {
    ROOT("root_anchor"),
    HEAD("head_anchor"),
    BODY("body_anchor"),
    LEFT_ARM("left_arm_anchor"),
    RIGHT_ARM("right_arm_anchor"),
    LEFT_LEG("left_leg_anchor"),
    RIGHT_LEG("right_leg_anchor");

    private final String key;

    CosmeticAnchor(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
