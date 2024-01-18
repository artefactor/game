package com.game.strategy;

import static com.game.sets.GreenCardsTestSet.findG;
import static com.game.sets.OrangeCardsTestSet.findO;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.game.model.BlueCard;
import com.game.model.BlueDeck;
import com.game.model.BlueGreenCard;
import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.CardSet;
import com.game.model.CardsOnTable;
import com.game.model.GameContext;
import com.game.model.GreenCard;
import com.game.model.OrangeCard;
import com.game.model.Player;
import com.game.model.Players;
import com.game.model.Restriction;
import org.apache.commons.collections4.ListUtils;

public class GameStrategy {

    static boolean STUPID = !true;

    final GameContext gameContext;

    public GameStrategy(GameContext gameContext) {
        this.gameContext = gameContext;
        gameContext.setStrategy(this);
    }

    //  ====----------  Drawing cards  begins   --------====
    public void drawCardsAtTheBeginningOfTheRound() {
        switch (gameContext.getCurrentRound()) {
            // ----------------- main rules begins  ---
            case 1:
                gameContext.drawInitialCards();
                return;
            // ----------------- main rules ends  ---
            case 7:
            case 8:
            default:
                drawPlayerCards(
                    6 - gameContext.getPlayer1Hand().cards.size(),
                    6 - gameContext.getPlayer2Hand().cards.size(),
                    false);
        }
    }

    public void drawCardsDuringDay() {
        switch (gameContext.getCurrentRound()) {
            // ----------------- main rules begins  ---
            case 1:
                gameContext.drawCardsDuringDay();
                return;
            // ----------------- main rules ends  ---
            default:
                drawPlayerCards(3, 3, true);
        }
    }

    private void drawPlayerCards(int amountForPlayer1, int amountForPlayer2, boolean duringDay) {
        if (STUPID) {
            for (int i = 0; i < amountForPlayer1; i++) {
                gameContext.getPlayer1Hand().cards.add(
                    gameContext.getNextRoutine()
                );
            }
            for (int i = 0; i < amountForPlayer2; i++) {
                gameContext.getPlayer2Hand().cards.add(
                    gameContext.getNextRoutine()
                );
            }
            return;
        }
        switch (gameContext.getCurrentRound()) {
            // TODO 4: create a rule for  personal Deck usage/ not usage
            case 7:
            case 8:
                int intermid = duringDay ? 1 : 2;
                // сначала в personal deck, затем как обычно
                for (int i = 0; i < intermid; i++) {
                    gameContext.getPlayer1Hand().cards.add(
                        gameContext.getNextPersonal(gameContext.getBlueDecks().getPlayer1DiscardDeck()));
                }
                for (int i = intermid; i < amountForPlayer1; i++) {
                    gameContext.getPlayer1Hand().cards.add(gameContext.getNextRoutine());
                }
                for (int i = 0; i < intermid; i++) {
                    gameContext.getPlayer2Hand().cards.add(
                        gameContext.getNextPersonal(gameContext.getBlueDecks().getPlayer2DiscardDeck()));
                }
                for (int i = intermid; i < amountForPlayer2; i++) {
                    gameContext.getPlayer2Hand().cards.add(gameContext.getNextRoutine());
                }
                return;
            default:
                // если слишком много напряга, тогда в развитии, затем - в обычной колоде
                for (int i = 0; i < amountForPlayer1; i++) {
                    gameContext.getPlayer1Hand().cards.add(
                        isMuchTension(gameContext.getPlayers().getPlayer1()) ?
                            gameContext.getNextProgress() :
                            gameContext.getNextRoutine()
                    );
                }
                for (int i = 0; i < amountForPlayer2; i++) {
                    gameContext.getPlayer2Hand().cards.add(
                        isMuchTension(gameContext.getPlayers().getPlayer2()) ?
                            gameContext.getNextProgress() :
                            gameContext.getNextRoutine()
                    );
                }
        }
    }

    private boolean isMuchTension(Player player) {
        // is better now.
        return true;
        //        return player.getSatisfaction() < 5 * player.getTension();
    }

    //  ====----------  Drawing cards  ends   ----------====

    //  ====----------  Play cards  begins   ----------=====
    PT pt;

    public void prePlan() {
        pt = new PT(gameContext);
    }

    public void playTurn(GreenCard card1, GreenCard card2) {
        // if no events - play the plan
        // if cancelable events - play the plan
        // if no cancelable events - change the plan and play events/cards
        //        turn.playTurn(gameContext, card1, card2);
        // alt version
        pt.max.playTurn(card1, card2);
        gameContext.addStatistics("played_option", pt.max.value);
        gameContext.addDay();
    }
    //  ====----------  Play cards  ends   ----------=====

    /*
    1)
    всего 19 карт
     раздали  6 6
     сыграли  3 3
     дотянули 3 3
     сыграли  4 4
     оставили 2 и 2
      раунд: по 9 взяли, 7 сыграли, 2 оставили
      в колоде осталась 1 карта, по 2 на руках
      сброс: 14
   2) нужно брать по 4 карты, т.е. 8
        1 взяли   - сброс (14) перетусовался.
         добрали 7, в колоде 7
         сыграли 6
         взяли по 3, в колоде 1
         сыграли по 4
         на руках: по 2
         сбросили все. В колоде 1 карта, по 2 на руках, сброс - 14

    19
     */

    //  ====----------  Saving cards  begins   ----------=====
    public void saveCards() {
        leave3Cards(gameContext);
    }

    private void leave3Cards(GameContext gameContext) {
        Player player1 = gameContext.getPlayers().getPlayer1();
        Player player2 = gameContext.getPlayers().getPlayer2();
        CardSet<BlueGreenCard> player1Hand = gameContext.getPlayer1Hand();
        CardSet<BlueGreenCard> player2Hand = gameContext.getPlayer2Hand();
        CardsOnTable cardsOnTable = gameContext.getCardsOnTable();
        if (STUPID || player1.getSatisfaction() > player2.getSatisfaction()) {
            leave3Cards(gameContext, player2, player2Hand, cardsOnTable,
                gameContext.getBlueDecks().getPlayer2DiscardDeck());
            leave3Cards(gameContext, player1, player1Hand, cardsOnTable,
                gameContext.getBlueDecks().getPlayer1DiscardDeck());
        } else {
            leave3Cards(gameContext, player1, player1Hand, cardsOnTable,
                gameContext.getBlueDecks().getPlayer1DiscardDeck());
            leave3Cards(gameContext, player2, player2Hand, cardsOnTable,
                gameContext.getBlueDecks().getPlayer2DiscardDeck());
        }
    }

    private void leave3Cards(GameContext gameContext, Player player, CardSet<BlueGreenCard> playerHand,
        CardsOnTable cardsOnTableOpt,
        BlueDeck playerDiscardDeck) {

        List<BlueGreenCard> cardsOnTable = cardsOnTableOpt.cardsOnTable.cards.stream()
            .map(BlueGreenCardWithLocationOption::getCardBase)
            // убрать карты, которые разовые
            .filter(c -> c instanceof BlueCard).map(c -> (BlueCard) c)
            .filter(not(this::isOneTimeCard))
            .collect(toList());
        // нашли 3 топовые карты
        List<BlueGreenCard> union = ListUtils.union(playerHand.cards, cardsOnTable);
            // машину и тренинг нужно оставить. если есть карты со скандалом - их нужно сыграть заранее. в стратегии так указать.
        // пока можно оставить вместе с машиной ,чтобы проще было

        List<BlueGreenCard> leaveCards = new ArrayList();
        GreenCard carOnTable = findG("5");  // нужно этого игрока
        if (cardsOnTableOpt.playedCar(carOnTable, player.getId())){
            leaveCards.add(carOnTable);
        }
        var cards = playerHand.cards.stream()
            .filter(c -> c instanceof BlueCard).map(c -> (BlueCard) c)
            .filter(c1 -> cantSkip(c1, player)).collect(toList());
        leaveCards.addAll(cards);


        int limit;
        boolean upToRound6 = gameContext.getCurrentRound() <= 6;
        switch (leaveCards.size()){
            case 0:
                if (upToRound6) {
                    limit = 4;
                } else {
                    limit = 3;
                }
                break;
            case 1:
                if (upToRound6) {
                    limit = 3;
                } else {
                    limit = 2;
                }
                break;
            case 2:
                if (upToRound6) {
                    limit = 2;
                } else {
                    limit = 1;
                }
                break;
            default:
                if (upToRound6) {
                    limit = 1;
                } else {
                    limit = 0;
                }
        }

        var top3Cards = union.stream()
            .filter(b -> b instanceof BlueCard)   // TODO 2: or машина
            .flatMap(b1 -> b1.getOptionsLocations().stream())  // todo sort
            .sorted(new StrategyComparator(gameContext, player.getId(), player.getId()))
            .map(BlueGreenCardWithLocationOption::getCardBase)
            .distinct()
            .limit(limit)
            .collect(Collectors.toCollection(LinkedList::new));

        // todo 4: проверка на особые карты/ ограничения на сброс
        // personal сброс
        if (upToRound6) {
            BlueCard poll = (BlueCard) (top3Cards.poll());
            playerDiscardDeck.getCards().add(poll);
            cardsOnTableOpt.cardsOnTable.cards
                .removeIf(cOpt->cOpt.getCardBase().getId().equals(poll.getId()));
            playerHand.cards.remove(poll);
        }
        // добавили несбрасываемое

        top3Cards.addAll(leaveCards);


        // сбросили с руки что не топ
        for (int i = 0; i < playerHand.cards.size(); i++) {
            BlueGreenCard blueGreenCard = playerHand.cards.get(i);
            if (!top3Cards.contains(blueGreenCard)) {
                gameContext.discardCard((BlueCard) blueGreenCard);
                playerHand.cards.remove(blueGreenCard);
            }
        }
        // взяли в руку что еще пока не взяли из топовых.
        for (int i = 0; i < top3Cards.size(); i++) {
            BlueGreenCard blueGreenCard = top3Cards.get(i);
            if (!playerHand.cards.contains(blueGreenCard)) {
                playerHand.cards.add(blueGreenCard);
                cardsOnTableOpt.cardsOnTable.cards
                    .removeIf(cOpt -> cOpt.getCardBase().getId().equals(blueGreenCard.getId()));
            }
        }

    }

    private boolean isOneTimeCard(BlueCard c) {
        return Restriction.ONE_TIME_ACTION.equals(c.getRestriction()) ||
            Restriction.MUST_BE_PLAYED_ONE_TIME_ACTION.equals(c.getRestriction()) ||
            Restriction.WEEKEND_ONE_TIME_ACTION.equals(c.getRestriction());
    }

    // TODO 3: или карта оранжевая не дает сбросить
    private boolean cantSkip(BlueCard c, Player player) {
        boolean must = Restriction.MUST_BE_PLAYED.equals(c.getRestriction()) ||
            Restriction.MUST_BE_PLAYED_ONE_TIME_ACTION.equals(c.getRestriction());
        boolean scandal = Restriction.SCANDAL.equals(c.getRestriction()) && player.getTension() >= 5;
        return must || scandal;
    }

    public Player choosePersonForHeartfeltTalk(Players copy, int ownerPlayerId) {
        Player ownerPlayer = copy.getPlayer(ownerPlayerId);
        Player partner = copy.getPlayerPartner(ownerPlayerId);
        // compare by tension
        if (partner.getTension() > ownerPlayer.getTension()) {
            return partner;
        }
        if (partner.getTension() < ownerPlayer.getTension()) {
            return ownerPlayer;
        }
        // is tension is equal - compare by satisfaction
        if (partner.getSatisfaction() < ownerPlayer.getSatisfaction()) {
            return partner;
        }
        return ownerPlayer;
    }

    public int findTheWorstWorstOrangeCard(LinkedList<OrangeCard> cards) {
        List<Integer> collect = cards.stream().map(OrangeCard::getId).collect(toList());
        if (collect.contains(14)){
            return 14;
        }
        if (collect.contains(5)){
            return 5;
        }
        return collect.isEmpty()? -1: collect.get(0);
    }

    public void removeTheWorstOrangeCard(LinkedList<OrangeCard> cards) {
        // TODO 1: придумать strategy для выбора карт
        boolean has = cards.removeIf(card -> card.getId() == 14);
        if (!has) {
            cards.removeIf(card -> card.getId() == 5);
        }

    }

    public void playAffectTrainingSkill(int ownerId) {
        // TODO 1:  Эффект +способность на выбор или ЗП +20 - стратегия
        Player actorPlayer = gameContext.getPlayers().getPlayer(ownerId);
        if (actorPlayer.getSalary() <= 5) {
            actorPlayer.setSalary(actorPlayer.getSalary() + 2);
        } else {
            actorPlayer.getOrangeCards().cards.addAll(findO(49));
        }
    }

    //  ====----------  Leave  cards  ends   ----------=====
}
// 1  true
//Run count: 10000
//WIN_WEDDING - 15.58% (1558),
//BROKE_UP_AS_FRIENDS - 0.0% (0),
//BROKE_UP_AS_ENEMIES - 0.0% (0),
//ERROR - 84.42% (8442)
//In average:
// Player1: {143 🎂}, [6💥]		14💰		2 💪
// Player2: {152 🎂}, [11💥]		5💰		3 💪
// 2  false
//Run count: 10000
//    WIN_WEDDING - 19.81% (1981),
//    BROKE_UP_AS_FRIENDS - 0.0% (0),
//    BROKE_UP_AS_ENEMIES - 2.75% (275),
//    ERROR - 77.44% (7744)
//    In average:
//    Player1: {160 🎂}, [16💥]		8💰		4 💪
//    Player2: {167 🎂}, [16💥]		3💰		3 💪
// 3  condition
//Run count: 10000
//WIN_WEDDING - 40.49% (4049),
//BROKE_UP_AS_FRIENDS - 0.0% (0),
//BROKE_UP_AS_ENEMIES - 0.45% (45),
//ERROR - 59.06% (5906)
//In average:
// Player1: {158 🎂}, [12💥]		11💰		3 💪
// Player2: {164 🎂}, [13💥]		6💰		3 💪

//=======  real deck (begin)  =======

//===============================
//Run count: 10000
//WIN_WEDDING - 66.3% (6630),
//BROKE_UP_AS_FRIENDS - 0.0% (0),
//BROKE_UP_AS_ENEMIES - 0.0% (0),
//ERROR - 33.7% (3370)
//In average:
// Player1: {158 🎂}, [-9💥]		29💰		2 💪
// Player2: {154 🎂}, [-1💥]		22💰		1 💪

//== random
//===============================
//    Run count: 10000
//    WIN_WEDDING - 11.86% (1186),
//    BROKE_UP_AS_FRIENDS - 0.0% (0),
//    BROKE_UP_AS_ENEMIES - 0.0% (0),
//    ERROR - 88.14% (8814)
//    In average:
//    Player1: {153 🎂}, [1💥]		7💰		2 💪
//    Player2: {167 🎂}, [1💥]		11💰		0 💪