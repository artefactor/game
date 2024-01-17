package com.game.model;

import static com.game.model.BlueCard.SpecialBlueCards.none;
import static com.game.sets.OrangeCardsTestSet.findO;
import static java.util.Arrays.stream;

import java.util.Set;
import java.util.stream.Collectors;

import com.game.strategy.GameStrategy;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlueCard extends BlueGreenCard {

    // T - тело
    // B - быт
    // BB - быт
    // O - общение
    // H - хобби
    // R - развлечение
    // U - учеба
    // N - нейтральное

    public static final Set<String> ALLOWED_IDS =
        stream(FavouriteAction.values()).map(Enum::name).collect(Collectors.toSet());

    private Restriction restriction;
    private SpecialBlueCards special;

    public BlueCard(String id, String name, int cost, int costEnergy, double bonus, int tension, Location location,
        Participants participants, Restriction restriction, SpecialBlueCards special) {
        super(id, name, cost, costEnergy, bonus, tension, location, participants);
        this.restriction = restriction;
        this.special = special;
    }

    public BlueCard(String id, int cost, int costEnergy, int bonus, int tension) {
        super(id, id, cost, costEnergy, bonus, tension, Location.ANY, Participants.SELF);
    }


    public enum SpecialBlueCards {
        none,
        //        кубик 5/6: +1 к вынос
        increase_stamina_56,  //+
        increase_stamina_6,  //+
        energy_bonus,    //+
        // bonus партнёр - кубик,
        partner_cube,   // +
        money_bonus,    // +

        choose_person,          // +
        scandal,                // +

        //        Цена 4 за вечер (2 вечера)
        //        Эффект +способность на выбор или ЗП +20
        training_skill,  // +/-
        // кубик 6: убрать черту на выбор
        therapy,           // +

        // эффект + способность случайно,
        psycho_training,        // +
        //  кубик 6: убрать черту на выбор
        book,               // +

        shopping,               //  ?   TODO 1:
        shopping_clothes, // (тоже самое что partner_cube),

    }


    public void playCard(Location location, GameContext gameContext, int ownerId, int actorId) {
        Player ownerPlayer = gameContext.getPlayers().getPlayer(ownerId);
        Player actorPlayer = gameContext.getPlayers().getPlayer(actorId);
        switch (special) {

            case increase_stamina_56:
                int currentCubeResult = gameContext.getBlueDecks().getNextCube(id);
                if (currentCubeResult == 5 || currentCubeResult == 6) {
                    actorPlayer.setStamina(actorPlayer.getStamina() + 1);
                }
                break;
            case increase_stamina_6:
                currentCubeResult = gameContext.getBlueDecks().getNextCube(id);
                if (currentCubeResult == 6) {
                    actorPlayer.setStamina(actorPlayer.getStamina() + 1);
                }
                break;


            case psycho_training:
                // эффект + способность случайно,
                int randomYellow  = OrangeDeck.getRandomCard(id, OrangeCard.Type.YELLOW);
                actorPlayer.getOrangeCards().cards.addAll(findO(randomYellow));
                break;

            case book:
            case therapy:
                //  кубик 6: убрать черту на выбор
                currentCubeResult = gameContext.getBlueDecks().getNextCube(id);
                if (currentCubeResult == 6) {
                    gameContext.getStrategy().removeTheWorstOrangeCard(actorPlayer.getOrangeCards().cards);
                }
                break;
            case training_skill:
                // она только 1 раз играется, так что флаг обратно не обязательно нужно выставлять
                if (gameContext.isFirstTimeTrainingSkillDone()) {
                    gameContext.getStrategy().playAffectTrainingSkill(ownerId);
                } else {
                    gameContext.setFirstTimeTrainingSkillDone(true);
                }
                break;
            case scandal:
                throw new IllegalStateException("it is impossible to play scandal for one player!");
//                break;
        }
    }

    @Override
    public void playCardBoth(Location location, GameContext gameContext, int ownerId, int partnerId) {
        Player ownerPlayer = gameContext.getPlayers().getPlayer(ownerId);
        Player partner = gameContext.getPlayers().getPlayer(partnerId);
        int currentCubeResult;
        switch (special) {
            // TODO 4: вроде бы за компанию не идут в бассейн или тренажерный зал
            // если специальная черта или специальный этап
            case increase_stamina_56:
                currentCubeResult = gameContext.getBlueDecks().getNextCube(id);
                if (currentCubeResult == 5 || currentCubeResult == 6) {
                    ownerPlayer.setStamina(ownerPlayer.getStamina() + 1);
                }
                 currentCubeResult = gameContext.getBlueDecks().getNextCube(id);
                if (currentCubeResult == 5 || currentCubeResult == 6) {
                    partner.setStamina(partner.getStamina() + 1);
                }
                break;
            case increase_stamina_6:
                currentCubeResult = gameContext.getBlueDecks().getNextCube(id);
                if (currentCubeResult == 6) {
                    ownerPlayer.setStamina(ownerPlayer.getStamina() + 1);
                }
                currentCubeResult = gameContext.getBlueDecks().getNextCube(id);
                if (currentCubeResult == 6) {
                    partner.setStamina(partner.getStamina() + 1);
                }
                break;

            case scandal:
                gameContext.playScandal(true, ownerId, partnerId);
                break;
            case therapy:
                //  кубик 6: убрать черту на выбор
                currentCubeResult = gameContext.getBlueDecks().getNextCube(id);
                if (currentCubeResult == 6) {
                    gameContext.getStrategy().removeTheWorstOrangeCard(ownerPlayer.getOrangeCards().cards);
                }
                currentCubeResult = gameContext.getBlueDecks().getNextCube(id);
                if (currentCubeResult == 6) {
                    gameContext.getStrategy().removeTheWorstOrangeCard(partner.getOrangeCards().cards);
                }
                break;
        }
    }

    @Override
    public void affectPlayer(Location location, GameContext gameContext, int ownerPlayerId, int actorPlayerId, Players copy) {
        super.affectPlayer(location, gameContext, ownerPlayerId, actorPlayerId, copy);
        Player actorPlayer = copy.getPlayer(actorPlayerId);
        Player ownerPlayer = copy.getPlayer(ownerPlayerId);
        switch (special) {
            case energy_bonus:
                if (!ownerPlayer.state.isUnwell()) {
                    ownerPlayer.setEnergy(ownerPlayer.getEnergy() + 1);
                }
                break;
            case money_bonus:
                //ownerPlayer.setMoney(ownerPlayer.getMoney() + 2);  - это уже подсчитывается как и было
                break;
            case training_skill:
                // TODO 1: преимущество карты в сортировке и др

        }
    }

    @Override
    public void affectBothPlayers(Location location, GameContext gameContext, int ownerPlayerId, Players copy) {
        Player partner = copy.getPlayerPartner(ownerPlayerId);
        Player ownerPlayer = copy.getPlayer(ownerPlayerId);
        super.affectBothPlayers(location, gameContext, ownerPlayerId, copy);
        switch (special) {
            case choose_person:
                // душевный разговор по умолчанию снимает у того, у кого больше напряжения. На выбор.
                // если равенство - то у того, у кого меньше удовлетворения (можно у автора карты, можно еще какой-то критерий),
                // тут этот выбор по идее должен быть у стратегии. Ее можно получить из контекста. Или отдельно.
                GameStrategy strategy = gameContext.getStrategy();
                Player affectedPlayer = strategy.choosePersonForHeartfeltTalk(copy, ownerPlayerId);
                affectedPlayer.addTension(getTension());
                break;
            case partner_cube:
            case shopping_clothes:
                partner.addSatisfaction(3.5);
                break;
        }
    }

    @Override
    public boolean canBePlayedBy(GameContext gameContext, int ownerId, int actorId, Players copy, boolean borrowFromPartner) {
        if (restriction != null) {
            switch (restriction) {
                case WEEKEND:
                case WEEKEND_ONE_TIME_ACTION:
                    if (6 != gameContext.getCurrentDay()) {
                        return false;
                    }
                default:
            }
        }
        return super.canBePlayedBy(gameContext, ownerId, actorId, copy, borrowFromPartner);
    }

    @Override
    public boolean canBePlayedBoth(GameContext gameContext, int ownerId, Players copy, boolean borrowFromPartner) {
        if (restriction != null) {
            switch (restriction) {
                case WEEKEND:
                case WEEKEND_ONE_TIME_ACTION:
                    if (6 != gameContext.getCurrentDay()) {
                        return false;
                    }
                default:
            }
        }
        return super.canBePlayedBoth(gameContext, ownerId, copy, borrowFromPartner);
    }

    @Override
    public String toString() {
        return super.toString()
            + (restriction != null ? restriction : " ")
            + (special != none ? special : "");
    }
}
