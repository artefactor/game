package com.game.model;

import java.util.List;

public class CardsOnTable {

    private final TurnHistory[] history = new TurnHistory[7];

    public CardSet<BlueGreenCardWithLocationOption> cardsOnTable = new CardSet<>();

    public CardsOnTable() {
        clear();
    }

    public void playCard(BlueGreenCardWithLocationOption card, GameContext gameContext, int ownerId, int playerId) {
        cardsOnTable.cards.add(card);
        int currentDay = gameContext.getCurrentDay();
        Participants participants1 = ownerId == playerId ? Participants.SELF : Participants.PARTNER;
        history[day(currentDay)].set(ownerId, participants1, card);
    }

    public void playCardBoth(BlueGreenCardWithLocationOption card, GameContext gameContext, int ownerId) {
        cardsOnTable.cards.add(card);
        int currentDay = gameContext.getCurrentDay();
        Participants participants1 = Participants.BOTH;
        BlueGreenCard cardBase = card.getCardBase();
        history[day(currentDay)].set(ownerId, participants1, card);
    }

    void clear() {
        cardsOnTable.cards.clear();
        for (int i = 0; i < 7; i++) {
            history[i] = new TurnHistory();
        }
    }

    public boolean playedWeekend(int playerId) {
        TurnHistory saturday = history[day(6)];
        boolean hasHistory =
            isWeekend(saturday.get(playerId, Participants.SELF))
                ||
                isWeekend(saturday.get(3 - playerId, Participants.PARTNER));
        return hasHistory;
    }

    private boolean isWeekend(BlueGreenCardWithLocationOption card) {
        if (card == null) {
            return false;
        } else {
            BlueGreenCard cardBase = card.getCardBase();
            if (!(cardBase instanceof BlueCard)) {
                return false;
            }
            Restriction restriction = ((BlueCard) cardBase).getRestriction();
            return restriction != null && List.of(Restriction.WEEKEND, Restriction.WEEKEND_ONE_TIME_ACTION)
                .contains(restriction);
        }
    }

    public boolean playedBothWeekend() {
        TurnHistory saturday = history[day(6)];
        boolean hasHistory =
            isWeekend(saturday.get(1, Participants.BOTH))
                ||
                isWeekend(saturday.get(2, Participants.BOTH));
        return hasHistory;
    }

    public boolean playedCar(GreenCard carOnTable, int playerId) {
        for (TurnHistory turnHistory : history) {
            boolean played = isCard(turnHistory.get(playerId, Participants.SELF), carOnTable) ||
                isCard(turnHistory.get(playerId, Participants.BOTH), carOnTable);
            if (played) {
                return true;
            }
        }
        return false;
    }

    private boolean isCard(BlueGreenCardWithLocationOption historyCard, GreenCard card) {
        return historyCard != null && historyCard.getId().equals(card.getId());
    }

    private int day(int currentDay) {
        return currentDay - 1;
    }

    public int getSeparatedEveningsCount() {
        int separatedEvenings = 0;
        for (TurnHistory turnHistory : history) {
            boolean separated =
                turnHistory.get(1, Participants.BOTH) == null &&
                    turnHistory.get(2, Participants.BOTH) == null;
            if (separated) {
                separatedEvenings++;
            }
        }
        return separatedEvenings;
    }
    public int getOutsideEveningsCount(int playerId) {
        return getLocationEveningsCount(playerId, Location.OUTSIDE);
    }

    public int getHomeEveningsCount(int playerId) {
        return getLocationEveningsCount(playerId, Location.HOME);
    }

    public int getLocationEveningsCount(int playerId, Location location) {
        int outsideEvenings = 0;
        for (TurnHistory turnHistory : history) {
            if (isLocation(turnHistory.get(1, Participants.BOTH), location)) {
                outsideEvenings++;
                continue;
            }
            if (isLocation(turnHistory.get(2, Participants.BOTH), location)) {
                outsideEvenings++;
                continue;
            }
            if (isLocation(turnHistory.get(playerId, Participants.SELF), location)) {
                outsideEvenings++;
                continue;
            }
            if (isLocation(turnHistory.get(3 - playerId, Participants.PARTNER), location)) {
                outsideEvenings++;
                continue;
            }
        }
        return outsideEvenings;
    }

    public int getOutsideEveningsFromPartnerCount(int playerId) {
        int outsideEveningsFromPartner = 0;
        for (TurnHistory turnHistory : history) {
            if (isLocation(turnHistory.get(3 - playerId, Participants.BOTH), Location.OUTSIDE)) {
                outsideEveningsFromPartner++;
                continue;
            }
            if (isLocation(turnHistory.get(3 - playerId, Participants.PARTNER), Location.OUTSIDE)) {
                outsideEveningsFromPartner++;
                continue;
            }
        }
        return outsideEveningsFromPartner;
    }

    private boolean isLocation(BlueGreenCardWithLocationOption card, Location location) {
        return (card != null && location.equals(card.getLocation()));
    }
}
