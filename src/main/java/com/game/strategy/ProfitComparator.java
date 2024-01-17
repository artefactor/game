package com.game.strategy;

import static com.game.strategy.PlayersTurnEnum2.BOTH_1;
import static com.game.strategy.PlayersTurnEnum2.SELF_SELF_AS_BOTH_2;
import static java.lang.String.valueOf;
import static java.util.function.Predicate.not;
import static org.apache.commons.lang3.StringUtils.leftPad;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.game.model.GameContext;
import com.game.model.Player;

public class ProfitComparator implements Comparator<PTOption> {

    protected static final int KOEFF_TENS = -8;
    protected static final int KOEFF_MONEY = 1;
    protected static final int KOEFF_SATISF = 1;
    private final boolean useExtraTension;

    public ProfitComparator(boolean useExtraTension) {
        this.useExtraTension = useExtraTension;
    }

    @Override
    public int compare(PTOption oA, PTOption oB) {
        if (GameStrategy.STUPID) {
            return 0;
        }
        /**
         *  played options:
         * BOTH_1: 1985
         * SELF_SELF_2: 85,
         * SELF_PARTNER_1: 49,
         * PARTNER_PARTNER_2: 9,
         *
         * SELF_SELF_2: 674,
         * BOTH_1: 354,
         * SELF_PARTNER_1: 213,
         * PARTNER_PARTNER_2: 47
         */
        return calcIntegrated(oA) - calcIntegrated(oB);
    }

    private int calcIntegrated(PTOption oA) {
        Player pA1 = oA.totalProfit.newPlayer1Status;
        Player pA2 = oA.totalProfit.newPlayer2Status;

        var totalTension = pA1.get("tension     ") + pA2.get("tension     ");
        var totalSatisfaction = pA1.get("satisfaction") + pA2.get("satisfaction");
        var totalMoney = pA1.get("money       ") + pA2.get("money       ");

        // optional: add преимущество to 'both' strategy
        if (useExtraTension) {
            Map<PTOption, Integer> extraTension = Map.of(
                // при таком варианте два скандала будут всегда проигрывать 1 скандалу
                oA, oA.value.equals(SELF_SELF_AS_BOTH_2) ? -1 : oA.value.equals(BOTH_1) ? 1 : 2
            );
            totalTension += extraTension.get(oA);
            totalTension += extraTension.get(oA);
        }

        int integratedRes = KOEFF_TENS(oA.gameContext) * totalTension + KOEFF_MONEY(oA.gameContext) * totalMoney
            + KOEFF_SATISF(oA.gameContext) * totalSatisfaction;
        return integratedRes;
    }

    private PTOption printProfit(PTOption e) {
        StringBuilder sb = new StringBuilder();
        Player player1 = e.gameContext.getPlayer(1);
        Player player2 = e.gameContext.getPlayer(2);

        Player pA1 = e.totalProfit.newPlayer1Status;
        Player pA2 = e.totalProfit.newPlayer2Status;
        boolean DEBUG_PROFIT = false;
        if (DEBUG_PROFIT) {
            sb.append("\nProfit for ").append(e.getClass().getSimpleName()).append("\n").append(e.getCardIds())
                .append("\n");
            sb.append("             player1").append(" |  player2 |").append("both\n");
            getAppend(sb, player1, pA1, player2, pA2, "tension     ", e);
            getAppend(sb, player1, pA1, player2, pA2, "satisfaction", e);
            getAppend(sb, player1, pA1, player2, pA2, "money       ", e);
            sb.append("calcIntegrated: ").append(calcIntegrated(e)).append("\n");

            System.out.println(sb);
        }
        return e;
    }

    private void getAppend(StringBuilder sb, Player old1, Player new1, Player old2, Player new2, String prop,
        PTOption e) {
        int i1 = old1.get(prop); int i2 = new1.get(prop);
        int i11 = old2.get(prop);int i12 = new2.get(prop);


        // optional: add преимущество to 'both' strategy
        if (useExtraTension && "tension" .equals(prop.trim())) {
            Map<PTOption, Integer> extraTension = Map.of(
                // при таком варианте два скандала будут всегда проигрывать 1 скандалу
                e, e.value.equals(SELF_SELF_AS_BOTH_2) ? -1 : e.value.equals(BOTH_1) ? 1 : 2
            );
            i2 += extraTension.get(e);
            i12 += extraTension.get(e);
        }

        boolean firstChanged = i1 != i2;
        boolean secondChanged = i11 != i12;
        int total = i2 + i12 - i1 - i11;
        sb.append(prop).append(": ")
            .append(format3(i1, firstChanged)).append("\t")
            .append(firstChanged ? "->  " : "    ").append(format3(i2, firstChanged))
            .append(firstChanged?"; ":"  ")
            .append(format3(i11, secondChanged)).append("\t").append(secondChanged ? "-> " : "   ").append(format3(i12, secondChanged))
            .append(secondChanged?"; ":"  ")
            .append("\t")
            .append(i1 + i11)
            .append(" -> ").append(i2 + i12)
            .append("  (").append(total > 0 ? "+" : "").append(total).append(")")
            .append("\n");
    }

    private String format3(int i1, boolean firstChanged) {
        return firstChanged ? leftPad(valueOf(i1), 2) : "  ";
    }

    private int KOEFF_SATISF(GameContext gameContext) {
        Player player1 = gameContext.getPlayer(1);
        Player player2 = gameContext.getPlayer(2);
        int currentRound = gameContext.getCurrentRound();
        int perRound = 100 * (currentRound - 1) / 8;
        if (player1.getSatisfaction() >= perRound && player2.getSatisfaction() >= perRound) {
            // ok with it
            return KOEFF_SATISF;
        } else {
            return 2 * KOEFF_SATISF;
        }
    }

    private int KOEFF_MONEY(GameContext gameContext) {
        Player player1 = gameContext.getPlayer(1);
        Player player2 = gameContext.getPlayer(2);
        int day = gameContext.getCurrentDay();
        if (player1.getMoney() + player2.getMoney() >= 2 * day) {
            // ok with it
            return KOEFF_MONEY;
        } else {
            return 2 * KOEFF_MONEY;
        }
    }

    private int KOEFF_TENS(GameContext gameContext) {
        Player player1 = gameContext.getPlayer(1);
        Player player2 = gameContext.getPlayer(2);
        int currentRound = gameContext.getCurrentRound();
        int perRound = 20 * (currentRound - 1) / 8;
        if (player1.getTension() <= perRound && player2.getTension() <= perRound) {
            // ok with it
            return KOEFF_TENS;
        } else {
            return 2 * KOEFF_TENS;
        }
    }

    PTOption findWinner(List<PTOption> options) {
        Optional<PTOption> winner = options.stream()
            .filter(not(PTOption::noCards))
            .map(this::printProfit)
            .max(this);
        return winner.get();
    }
}
