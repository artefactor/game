package com.game.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrangeCard extends GameCard {

    public void finish(GameContext gameContext, int playerId) {
        CardsOnTable cardsOnTable = gameContext.cardsOnTable;
        Player player = gameContext.getPlayers().getPlayer(playerId);
        switch (this.id) {
            case 26:  //"Домосед"
                //       За каждое мероприятие на выход от партнера - получите 1 напряг
                //       (синие и зеленые)
                int eveningsOutsideFromPartnerCount = cardsOnTable.getOutsideEveningsFromPartnerCount(playerId);
                player.addTension(eveningsOutsideFromPartnerCount);
//                player.addTension(1000);  // test что карта считается и сбрасывается
                break;
            case 28:  // Тусовщик
                //      Нужны мероприятия на "выход" как минимум 4 вечера в неделю.
                //      За каждый недопроведенный вечер получаете 3 напряга)
                //      Психокоррекция: 3 вечера вместо 4
                int outsideEveningsCount = cardsOnTable.getOutsideEveningsCount(playerId);
                int underGotEvening = Math.max(4 - outsideEveningsCount, 0);
//                                player.addTension(1000);  // test что карта считается и сбрасывается
                player.addTension(customization * underGotEvening);
                break;

        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, sphere, id, name, customization);
    }

    @JsonIgnore
    public boolean isOrange() {
        return OrangeCard.Type.ORANGE.equals(this.type);
    }

    public enum Type {
        ORANGE,
        YELLOW
    }

    public enum Sphere {
        G,
        T,
        H,
        V,
        U,
        L,
        D,
    }

    private Type type;
    private Sphere sphere;
    private Integer id;
    private String name;
    private Integer customization;

    public OrangeCard(Type type, Sphere sphere, Integer id, String name, Integer customization) {
        this.type = type;
        this.sphere = sphere;
        this.id = id;
        this.name = name;
        this.customization = customization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrangeCard that = (OrangeCard) o;
        return type == that.type && sphere == that.sphere && Objects.equals(id, that.id)
            && Objects.equals(name, that.name) && Objects.equals(customization, that.customization);
    }

    @JsonIgnore
    boolean isActive;
    @JsonIgnore
    int roundsInActive;

    public void setActive(boolean active, int rounds) {
        isActive = active;
        roundsInActive = rounds;
    }


}
