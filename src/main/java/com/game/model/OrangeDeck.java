package com.game.model;

import java.util.List;
import java.util.stream.Collectors;

import com.game.sets.OrangeCardsTestSet;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrangeDeck {

    private List<OrangeCard> cards;

    public static int getRandomCard(String initCardId, OrangeCard.Type type) {
        if (type.equals(OrangeCard.Type.YELLOW)) {
            // TODO 0: случайно
            // TODO 0: нужно вернуть ту карту, которая есть в колоде, а не на руках
            return 49;
            //            return 47;
        } else {
            return 23;
        }
    }

    public static int calcTensionEffectOfLocation(Location location, GameContext gameContext, int ownerPlayerId, int actorPlayerId) {
        var player = gameContext.getPlayer(actorPlayerId).updatedVersion();
        var cardsIds = player.getOrangeCards().cards
            .stream().map(OrangeCard::getId).collect(Collectors.toList());

        if (cardsIds.contains(26)) {    //"Домосед"
            boolean outsideLocation = Location.OUTSIDE.equals(location);
            boolean fromPartner = ownerPlayerId != actorPlayerId;
            if (outsideLocation && fromPartner) {
//                За каждое мероприятие на выход от партнера - получите 1 напряг
//                (синие и зеленые)
                return 1;
            }
        }
        if (cardsIds.contains(28)) {    //"Тусовщик"
            boolean homeLocation = Location.HOME.equals(location);
            int homeEveningsCount = gameContext.cardsOnTable.getHomeEveningsCount(actorPlayerId);
            //                Нужны мероприятия на "выход" как минимум 4 вечера в неделю.
            //                За каждый недопроведенный вечер получаете 3 напряга)
            //                Психокоррекция: 3 вечера вместо 4

            if (homeLocation && homeEveningsCount > 3) {
                return OrangeCardsTestSet.findO(28).get(0).getCustomization();
            }
        }

        return 0;
    }
}
