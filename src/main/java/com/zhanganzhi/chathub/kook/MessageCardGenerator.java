package com.zhanganzhi.chathub.kook;

import net.deechael.khl.message.cardmessage.Card;
import net.deechael.khl.message.cardmessage.CardMessage;
import net.deechael.khl.message.cardmessage.Size;
import net.deechael.khl.message.cardmessage.element.Image;
import net.deechael.khl.message.cardmessage.element.PlainText;
import net.deechael.khl.message.cardmessage.module.Divider;
import net.deechael.khl.message.cardmessage.module.Module;
import net.deechael.khl.message.cardmessage.module.Section;

import com.zhanganzhi.chathub.core.Config;

public class MessageCardGenerator {
    private static Card getCard() {
        Card card = new Card();
        card.setSize(Size.SM);
        return card;
    }

    private static Card getCard(Module module) {
        Card card = getCard();
        card.append(module);
        return card;
    }

    private static Section getPlainTextSection(String content) {
        Section section = new Section();
        PlainText text = new PlainText();
        text.setContent(content);
        section.setText(text);
        return section;
    }

    public static CardMessage getChatCardMessage(String avatarUrl, String server, String name, String message) {
        Card card = getCard();

        // first module
        Section firstModule = getPlainTextSection(Config.getInstance().getKookChatMessage(server, name));
        firstModule.setMode(Section.Mode.LEFT);
        Image image = new Image();
        image.setSrc(avatarUrl);
        firstModule.setAccessory(image);
        card.append(firstModule);

        // second module
        card.append(new Divider());

        // third module
        card.append(getPlainTextSection(message));

        return new CardMessage().append(card);
    }

    public static CardMessage getJoinCardMessage(String server, String name) {
        return new CardMessage().append(getCard(getPlainTextSection(Config.getInstance().getKookJoinMessage(server, name))));
    }

    public static CardMessage getLeaveCardMessage(String name) {
        return new CardMessage().append(getCard(getPlainTextSection(Config.getInstance().getKookLeaveMessage(name))));
    }

    public static CardMessage getSwitchMessage(String name, String serverFrom, String serverTo) {
        return new CardMessage().append(getCard(getPlainTextSection(Config.getInstance().getKookSwitchMessage(name, serverFrom, serverTo))));
    }
}
