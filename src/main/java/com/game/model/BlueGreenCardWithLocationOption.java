package com.game.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class BlueGreenCardWithLocationOption extends GameCard {

    private final BlueGreenCard cardBase;
    protected Location location;
    boolean unknownLocationForGreen;

    public BlueGreenCardWithLocationOption(BlueGreenCard cardBase, Location location) {
        this.cardBase = cardBase;
        this.location = location;
    }

    public BlueGreenCardWithLocationOption(BlueGreenCard cardBase) {
        this.cardBase = cardBase;
        location = cardBase.location;
        unknownLocationForGreen = (cardBase instanceof GreenCard) && GreenCard.SpecialGreenCards.Собеседование.equals(
            ((GreenCard) cardBase).getSpecial());
    }

    public Location getLocation() {
        return location;
    }

    public void affectPlayer(GameContext gameContext, int ownerPlayerId, int actorPlayerId, Players copy) {
        // выбираем из двух вариантов
        if (unknownLocationForGreen){
            Location location1 = Location.HOME;
            Location location2 = Location.OUTSIDE;
            int i1 = OrangeDeck.calcTensionEffectOfLocation(location1, gameContext, ownerPlayerId, actorPlayerId);
            int i2 = OrangeDeck.calcTensionEffectOfLocation(location2, gameContext, ownerPlayerId, actorPlayerId);
            if (i1 < i2) {
                location = location1;
            } else {
                location = location2;
            }
        }
        cardBase.affectPlayer(location, gameContext, ownerPlayerId, actorPlayerId, copy);
    }

    public void affectBothPlayers(GameContext gameContext, int playerId, Players copy) {
        cardBase.affectBothPlayers(location, gameContext, playerId, copy);
    }

    public void playCard(GameContext gameContext, int ownerId, int actorId) {
        cardBase.playCard(location, gameContext, ownerId, actorId);
    }

    public void playCardBoth(GameContext gameContext, int ownerId, int partnerId) {
        cardBase.playCardBoth(location, gameContext, ownerId, partnerId);
    }

    @Override
    Serializable getId() {
        return cardBase.getId();
    }

    //    protected final Participants participants;
    //    public Participants getParticipants() {
    //        if (this.participants != null) {
    //            return this.participants;
    //        }
    //        return cardBase.participants;
    //    }


}
