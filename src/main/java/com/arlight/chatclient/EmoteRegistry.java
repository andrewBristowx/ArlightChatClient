package com.arlight.chatclient;

import java.util.List;

public final class EmoteRegistry {
    private EmoteRegistry() {
    }

    public static final List<EmoteDefinition> DEFAULT_EMOTES = List.of(
            new EmoteDefinition(":somi:", "\uE101", false, "default", "Gratis"),
            new EmoteDefinition(":ponicalva:", "\uE102", false, "default", "Gratis"),
            new EmoteDefinition(":ponino:", "\uE103", false, "default", "Gratis"),
            new EmoteDefinition(":ponisi:", "\uE104", false, "default", "Gratis"),
            new EmoteDefinition(":ponisix:", "\uE105", false, "default", "Gratis"),
            new EmoteDefinition(":poniuvu:", "\uE106", false, "default", "Gratis"),
            new EmoteDefinition(":somiseven:", "\uE107", false, "default", "Gratis"),
            new EmoteDefinition(":angy:", "\uE108", false, "default", "Gratis"),
            new EmoteDefinition(":love:", "\uE109", false, "default", "Gratis"),
            new EmoteDefinition(":jeje:", "\uE10A", false, "default", "Gratis"),
            new EmoteDefinition(":mad:", "\uE10B", false, "default", "Gratis"),
            new EmoteDefinition(":amen:", "\uE10C", false, "default", "Gratis"),
            new EmoteDefinition(":pony0na:", "\uE10D", false, "default", "Gratis"),
            new EmoteDefinition(":bote:", "\uE200", true, "default", "Gratis")
    );
}
