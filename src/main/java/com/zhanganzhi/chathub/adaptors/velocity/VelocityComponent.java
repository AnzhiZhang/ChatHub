package com.zhanganzhi.chathub.adaptors.velocity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;

public class VelocityComponent {
    private Component component;

    public VelocityComponent(String rawText) {
        component = Component.text(rawText);
    }

    public VelocityComponent replaceComponent(String field, ComponentLike replaceTarget) {
        TextReplacementConfig replacementConfig = TextReplacementConfig
            .builder()
            .match("\\{"+ field +"\\}")
            .replacement(replaceTarget)
            .build();
        component = component.replaceText(replacementConfig);
        return this;
    }

    public VelocityComponent replaceString(String field, String replacement) {
        return replaceComponent(field, Component.text(replacement));
    }

    public VelocityComponent replaceServer(String field, String server, String serverText, Boolean clickable) {
        Component serverComponent = Component.text(serverText);
        if (clickable) 
            serverComponent = serverComponent.clickEvent(ClickEvent.runCommand("/server " + server));
        return replaceComponent(field, serverComponent);
    }

    public VelocityComponent replaceServer(String field, String server, String serverText) {
        return replaceServer(field, server, serverText, true);
    }

    public VelocityComponent replacePlayer(String field, String player, Boolean clickable) {
        Component playerComponent = Component.text(player);
        if(clickable)
            playerComponent = playerComponent.clickEvent(ClickEvent.suggestCommand("/chathub msg " + player + " "));
        return replaceComponent(field, playerComponent);
    }

    public VelocityComponent replacePlayer(String field, String player) {
        return replacePlayer(field, player, true);
    }
    public Component asComponent() {
        return component;
    }
}
