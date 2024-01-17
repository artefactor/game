package com.game.strategy;

import java.util.Comparator;

import com.game.model.BlueGreenCard;
import com.game.model.BlueGreenCardWithLocationOption;
import com.game.model.GameContext;
import com.game.model.OrangeDeck;
import com.game.model.Player;

public class StrategyComparator implements Comparator<BlueGreenCardWithLocationOption> {
    private final GameContext gameContext;
    private final Player actor;
    private final Player owner;

    public StrategyComparator(GameContext gameContext, int ownerId, int actorId) {
        this.gameContext = gameContext;
        actor = gameContext.getPlayer(actorId).updatedVersion();
        owner = gameContext.getPlayer(ownerId).updatedVersion();
    }

    public int compare(BlueGreenCardWithLocationOption b1O, BlueGreenCardWithLocationOption b2O) {
        BlueGreenCard b1 = b1O.getCardBase();
        BlueGreenCard b2 = b2O.getCardBase();
        if (GameStrategy.STUPID) {
            return 0;
        }
        int res;

        // нужно добавить психотерапию в преимущество  // TODO 1:
//        if (b1.getId().equals("U_06")){
//            return -1;
//        }
//        if (b2.getId().equals("U_06")){
//            return 1;
//        }

        // if little money - win with less money
        if (actor.getMoney() <= 5) {
            res = Integer.compare(b1.getCostMoney(gameContext), b2.getCostMoney(gameContext));
            if (res != 0) {
                return res;
            }
        }
        // if little energy - win with less energy
        if (actor.getEnergy() <= 2) {
            res = Integer.compare(b1.getCostEnergy(), b2.getCostEnergy());
            if (res != 0) {
                return res;
            }
        }
        if (actor.getTension() >= 15) {
            res = Integer.compare(b1.getTension(), b2.getTension());
            if (res != 0) {
                return res;
            }
        }
        res = Double.compare(b1.getFullSatisfactionPerCost(gameContext, actor), b2.getFullSatisfactionPerCost(
            gameContext, actor));
        if (res != 0) {
            return -res;  //reversed
        }
        res = Integer.compare(b1.getCostEnergy(), b2.getCostEnergy());
        if (res != 0) {
            return res;
        }
        res = Integer.compare(b1.getCostMoney(gameContext), b2.getCostMoney(gameContext));
        if (res != 0) {
            return res;
        }
        res = Integer.compare(b1.getTension(), b2.getTension());
        if (res != 0) {
            return res;
        }

        // если игроку навредит локейшен
        int effectB1  = OrangeDeck.calcTensionEffectOfLocation(b1O.getLocation(), gameContext, owner.getId(), actor.getId());
        int effectB2  = OrangeDeck.calcTensionEffectOfLocation(b2O.getLocation(), gameContext, owner.getId(), actor.getId());
        res = Integer.compare(effectB1, effectB2);
        if (res != 0) {
            return res;
        }

        return 0;
    }

}
