package com.game.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlueGreenCard extends GameCard {

    protected String id;
    protected String name;

    @Getter(AccessLevel.PRIVATE)
    private int costMoney;
    private int costEnergy;
    private double satisfaction;
    private int tension;

    protected Location location;
    protected Participants participants;

    public void affectPlayer(Location location, GameContext gameContext, int ownerPlayerId, int actorPlayerId, Players copy) {
        affectCostAndFavouriteAction(gameContext, copy, actorPlayerId);
        affectCarAndLocation(location, gameContext, copy, ownerPlayerId, actorPlayerId);
        Player ownerPlayer = copy.getPlayer(ownerPlayerId);
        ownerPlayer.addSatisfaction(satisfaction);
    }

    public void affectBothPlayers(Location location, GameContext gameContext, int ownerPlayerId, Players copy) {
        int partnerId = copy.getPlayerPartner(ownerPlayerId).getId();
        affectCostAndFavouriteAction(gameContext, copy, partnerId);
        affectCostAndFavouriteAction(gameContext, copy, ownerPlayerId);
        affectCarAndLocation(location, gameContext, copy, ownerPlayerId, partnerId);
        affectCarAndLocation(location, gameContext, copy, ownerPlayerId, ownerPlayerId);

        Player ownerPlayer = copy.getPlayer(ownerPlayerId);
        ownerPlayer.addSatisfaction(satisfaction);
    }

    private void affectCarAndLocation(Location location, GameContext gameContext, Players copy, int ownerPlayerId, int actorPlayerId) {
        Player actorPlayer = copy.getPlayer(actorPlayerId);
        boolean isTrip = Location.OUTSIDE.equals(location);
        if (this instanceof GreenCard){
            isTrip = isTrip || ((GreenCard) this).isCar();
        }
        if (actorPlayer.state.hasBrokenCar && isTrip){
            actorPlayer.addTension(1);
        }
        // TODO 5:  пока это сделаю в конце раунда
        boolean CALC_TENSION_ORANGE_DAILY = false;
        if (CALC_TENSION_ORANGE_DAILY) {
            int effectTension = OrangeDeck.calcTensionEffectOfLocation(location, gameContext, ownerPlayerId,
                actorPlayerId);
            actorPlayer.addTension(effectTension);
        }
    }

    private void affectCostAndFavouriteAction(GameContext gameContext, Players copy, int actorPlayerId) {
        Player actorPlayer = copy.getPlayer(actorPlayerId);
        actorPlayer.setEnergy(actorPlayer.getEnergy() - costEnergy);
        int cost = this.getCostMoney(gameContext);
        if (cost > actorPlayer.getMoney()) {
            copy.borrowMoneyToCoverCost(actorPlayerId, cost);
        }
        actorPlayer.setMoney(actorPlayer.getMoney() - cost);
        if (tension > 0) {
            actorPlayer.addTension(tension);
        }
        boolean isFavouriteActionType = actorPlayer.isFavouriteActionType(this);
        actorPlayer.addSatisfaction(isFavouriteActionType ? 2 : 0);
    }

    public double getFullSatisfaction(Player player) {
        boolean isFavouriteActionType = player.isFavouriteActionType(this);
        return satisfaction + (isFavouriteActionType ? 2.0 : 0.0);
    }

    public double getFullSatisfactionPerCost(GameContext gameContext, Player player) {
        double fullSatisfaction = getFullSatisfaction(player);
        if (costMoney > 0) {
            return fullSatisfaction /this.getCostMoney(gameContext);
        }
        return fullSatisfaction;
    }

    @JsonIgnore
    public String getActionType() {
        return getId().replaceFirst("_.*", "");
    }

    public boolean canBePlayedBy(GameContext gameContext, int ownerId, int actorId, Players copy, boolean borrowFromPartner) {
        int cost = this.getCostMoney(gameContext);
        Player ownerPlayer = copy.getPlayer(ownerId);
        Player actor = copy.getPlayer(actorId);
        boolean enoughMoney = actor.getMoney() >= cost;
        boolean enoughEnergy = actor.getEnergy() >= this.costEnergy;

        boolean location = true;
        if (actor.state.isUnwell()) {
            // нужно разграничить руку от сидеть дома - пока "рука тоже больной"
            boolean homeLocation =
                Location.HOME.equals(getLocation())
                    || Location.ANY.equals(getLocation());

            if (!homeLocation){
                return false;
            }
        }

        boolean participantsPlay =
            participants.isPossibleToPlayByActor(ownerPlayer, actor);
        boolean allExceptMoney = location && enoughEnergy && participantsPlay;
        if (allExceptMoney && !enoughMoney && borrowFromPartner){
            // попробуем отдолжить у партнера

            // TODO 4: partner can control borrow money
            /**
             * сейчас я могу просто взять забрать деньги у партнера если не достаточно.
             * но в таком случае партнеру может тоже не хватить
             * и если будет такая черта как "не любит давать деньги партнеру", то при такой реализации я не отслежу
             * будет сложная реализация. но это будет не скоро, так что можно потом придумать более подходящую
             *
             * TODO 1: сделать перебор на копии партнера если не нашлось вариантов без отдалживания
             * скажем, можно так: начать выбор с партнёра у которого меньше денег.
             * если у него нечем сыграть без отдалживания, то тогда отдолжить.
             * это все на копии.
             *
             * проблема в том, что если мы отдолжаем деньги, то у партнера меняется количество денег. и он не сможет сыграть карту, которую отложил.
             * если просто проверять можем отдолжить но не отдалживать, то тогда может получиться случай, когда оба выбрали карты с учетом партнера, но не могут реально их сыграть и нужно выбирать другие.
             */
            copy.borrowMoneyToCoverCost(actor.id, cost);
            enoughMoney = actor.getMoney() >= cost;
        }
        return allExceptMoney && enoughMoney;
    }

    public boolean canBePlayedBoth(GameContext gameContext, int ownerId, Players copy, boolean borrowFromPartner) {
        Player ownerPlayer = copy.getPlayer(ownerId);
        Player partner = copy.getPlayerPartner(ownerId);
        boolean enoughResources =
            partner.getEnergy() >= this.costEnergy &&
                partner.getMoney() >= this.getCostMoney(gameContext)
            && ownerPlayer.getEnergy() >= this.costEnergy
                && ownerPlayer.getMoney() >= this.getCostMoney(gameContext);

        //        boolean location = getLocation()
        boolean participantsPlay = participants.isPossibleToPlayBoth();
        return enoughResources && participantsPlay;
    }

    public int getCostMoney(GameContext context) {
        return context.discountable(costMoney);
    }

    public int getCostEnergy(GameContext context) {
        return costEnergy;
    }

    public double getSatisfaction(GameContext context) {
        return satisfaction;
    }

    public int getTension(GameContext context) {
        return tension;
    }

    public void playCard(Location location, GameContext gameContext, int ownerId, int actorId) {

    }

    public void playCardBoth(Location location, GameContext gameContext, int ownerId, int partnerId) {

    }

//    @Deprecated
//    List<BlueGreenCardOption> getOptionsFull(){
//        List<BlueGreenCardOption> cardOptions = new ArrayList<>();
//        List<Participants> optionsParticipants = participants.getOptions();
//        List<Location> optionsLocations = this.location.getOptions();
//        for (Participants optP: optionsParticipants) {
//            for (Location optL : optionsLocations) {
//                cardOptions.add(new BlueGreenCardOption(this,
//                    optL, optP));
//            }
//        }
//        return cardOptions;
//    }

    public List<BlueGreenCardWithLocationOption> getOptionsLocations(){
        List<BlueGreenCardWithLocationOption> cardOptions = new ArrayList<>();
        List<Location> optionsLocations = this.location.getOptions();
            for (Location optL : optionsLocations) {
                cardOptions.add(new BlueGreenCardWithLocationOption(this, optL));
            }
        return cardOptions;
    }
}
