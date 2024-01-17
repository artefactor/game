package com.game.strategy;

import static com.game.model.FavouriteAction.O;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.game.model.BlueCard;
import com.game.model.BlueGreenCard;
import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.GameContext;
import com.game.model.Player;
import com.game.model.Players;
import org.junit.jupiter.api.Test;

class GameStrategyTest {

    Player player =
        new Player(1, 3, 4,
            O).initRound();

    @Test
    void testSorting1() {
        List<BlueCard> blueCards = List.of(
            new BlueCard("O2", 5, 0, 1, 200),
            new BlueCard("O1", 5, 0, 1, 1)
        );
        assertt(blueCards);
    }

    @Test
    void testSorting2() {
        List<BlueCard> blueCards = List.of(
            new BlueCard("O2", 5, 0, 1, 10),
            new BlueCard("O1", 5, 0, 500, 10)
        );
        assertt(blueCards);
    }

    @Test
    void testSorting3() {
        List<BlueCard> blueCards = List.of(
            new BlueCard("O2", 5, 0, 1, 1),
            new BlueCard("O1", 4, 0, 1, 1)
        );
        assertt(blueCards);
    }

    @Test
    void testSorting4() {
        List<BlueCard> blueCards = List.of(
            new BlueCard("O2", 5, 8, 1, 10),
            new BlueCard("O1", 5, 0, 1, 10)
        );
        assertt(blueCards);
    }

    private void assertt(List<BlueCard> blueCards) {
        var g = new GameContext(null, null, null, null,
            new Players(player, player));
        var cards = new LinkedList<BlueGreenCard>(blueCards);
        var sorted = cards.stream()
            .map(c->new BlueGreenCardWithLocationOption(c))
            .sorted(new StrategyComparator(g, 1, 1))
            .collect(Collectors.toList());
        System.out.println(sorted);

        assertEquals("O1", sorted.get(0).getCardBase().getId());
    }
}