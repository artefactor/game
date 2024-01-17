package com.game.model;

import lombok.Data;

@Data
public class Players {

    final Player player1;
    final Player player2;

    public Player getPlayer(int i) {
        switch (i) {
            case 1: return player1;
            case 2: return player2;
        }
        throw new IllegalStateException(i + " is not right argument!");
    }

    public Player getPlayerPartner(int i) {
        return i == 2 ? player1 : player2;
    }

    public int getMinMoneyPlayer() {
        return player1.getMoney() <= player2.getMoney() ? 1 : 2;
    }

    public int getMostEnergeticPlayer() {
        if (player1.getEnergy() >= 2 && player2.getEnergy() >= 2) {
            if (player1.isB() && !player2.isB()) {
                return 1;
            }
            if (!player1.isB() && player2.isB()) {
                return 2;
            }
        }

        if (player1.getEnergy() >= player2.getEnergy()) {
            return 1;
        }
        return 2;
    }

    public Players copy() {
        return new Players(player1.copy(), player2.copy());
    }

    public Players copy(int player1Id) {
        if (player1Id == player1.id) {
            return new Players(player1.copy(), player2.copy());
        } else {
            return new Players(player2.copy(), player1.copy());
        }
    }

    public void borrowMoneyToCoverCost(int id, int cost) {
        Player player = getPlayer(id);
        int borrowAmount = cost - player.getMoney();
        Player partner = getPlayerPartner(id);
        if (partner.getMoney() >= 2) {
            // можно например сделать так что отдолжает если есть еще свободные деньги
        }
        if (partner.getMoney() >= borrowAmount) {
            partner.setMoney(partner.getMoney() - borrowAmount);
            player.setMoney(player.getMoney() + borrowAmount);
        }
    }
}
