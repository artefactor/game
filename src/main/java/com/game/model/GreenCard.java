package com.game.model;

import static com.game.sets.OrangeCardsTestSet.findO;

import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.game.strategy.Profit;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GreenCard extends BlueGreenCard {

    @Deprecated
    public static boolean isNullOrFree(GreenCard card1) {
        if (card1 == null) {
            return true;
        }
        switch (card1.consequencesOfRefusal) {
            case REFUSAL_FREE:
            case ADDITIONAL:
                return true;
            case TENSION_4:
            case TENSION_3:
            case TENSION_1:
            case REFUSAL_IMPOSSIBLE:
            default:
                return false;
        }
    }
    @JsonIgnore
    public boolean is_REFUSAL_IMPOSSIBLE() {
        return getConsequencesOfRefusal() == ConsequencesOfRefusal.REFUSAL_IMPOSSIBLE;
    }

    @JsonIgnore
    public boolean isNotIllnes() {
        return type != Type.ILLNESS;
    }

    public void affectPlayerRefusal(GameContext gameContext, Player player) {
        switch (consequencesOfRefusal) {
            case TENSION_1:
                player.setTension(player.getTension() + 1);
                return;
            case TENSION_3:
                player.setTension(player.getTension() + 3);
                return;
            case TENSION_4:
                player.setTension(player.getTension() + 4);
                return;
            case REFUSAL_IMPOSSIBLE:  // todo 5: как бы оттестировать, что она играется??
                //                throw new IllegalStateException("MUST play GREEN CARD!");
        }
    }

    // нужно на того игрока, на кого играет карта
    public void affectPlayerRefusal(GameContext gameContext, Profit totalProfit, int playerId) {
        affectPlayerRefusal(gameContext, totalProfit.getPlayer(playerId));
    }

    public void revealEvent(Player player) {
        switch (special) {
            case Сломалась_машина:
                player.state.hasBrokenCar = true;
                break;
            case ушиб_TILL_END_OF_WEEK:
                player.state.setHit(true);
                break;
            case заболел_SEVEN_DAYS:
                player.state.gotIll();
                break;
        }
    }

    public enum Type {
        NORMAL,
        ILLNESS,
        WORK
    }

    public enum ActionTime {
        TODAY,
        MOVABLE,
    }

    public enum ConsequencesOfRefusal {
        TENSION_4,
        TENSION_3,
        TENSION_1,
        REFUSAL_IMPOSSIBLE,
        REFUSAL_FREE,
        ADDITIONAL,
    }

    public enum SpecialGreenCards {
        none,
        /**
         * Цена: -1 к физ силе (если нет силы, то -2 к напрягу)
         */
        Задержали_на_работе ,    // +
        /**
         * Стоимость до 50% в этот день (для всех)
         */
        Скидки,     // +

        /**
         * Можно пригласить партнера, если его уровень удовлетворенности >30 Тогда партнер - кубик
         */
        Концерт_любимой_группы,       // +

        /**
         * Цена 30 (подарок с одного или с пары) Бонус: кубик /чел
         */
        Пригласили_на_ДР,  // +
        /**
         * При поездке получите +4  money
         */
        Выигрыш_в_лотерею,  // +

        /**
         * Участие партнера дает Вам +5 бонуса Иначе +2 напряга.
         */
        Прорвало_трубу,  // +

        /**
         * Двигает либо вы либо партнер (но в этом случае все равно идёте оба) Цена -2 физ силы с того, кто двигает (нет
         * сил - значит отказываетесь)
         */
        Передвинуть_шкаф,  // +

        /**
         * Нужно потратить 2 вечера (отвезти в ремонт и забрать машину из ремонта) Карта остается в руку и играется на
         * след неделю как синяя Цена: 100 на второй вечер. До починки каждая поездка напрягает. поездка = синяя на
         * выход или зеленая с car
         */
        Сломалась_машина, // +/-

        /**
         * Зеленые карточки работы не играются (сбрасываются без последствий) физ сила = 0 и выносливость = 0 до конца
         * раунда
         */
        ушиб_TILL_END_OF_WEEK,   //  +/-
        /**
         * физ сила = 0 и выносливость = 0 на 7 дней Синие карты на выход не разыгрываете Зеленые карточки работы не
         * играются (сбрасываются без последствий)
         */
        заболел_SEVEN_DAYS,       //  +/-

        /**
         * При участии партнёра получите бонус +5
         * Бросьте кубик 2 раза. Сумма меньше 5: ребенок закатывает истерику
         * (равносильно карте "скандал"
         */
        Посидеть_с_крестником,    // +/-
        /**
         * Бросьте кубик: 1,2: получите 1 напряг 3,4: ничего 5,6: увеличьте зп на 2
         */
        Собеседование,            // +/-

        /**
         * Цена: 20 Бросьте кубик: 1,2: плюс случайная оранжевая черта 3,4: плюс случайная желтая черта 5,6: одна
         * оранжевая черта неактивна Длительность применения : 2 раунда
         */
        // временный эффект  - вроде сделал
        Временная_психотерапия,    // +/-

        // осталось 8 зеленых карт
    }

    @Override
    public void playCardBoth(Location location, GameContext gameContext, int ownerId, int partnerId) {
        int currentCubeResult;
        switch (special) {
            case Сломалась_машина:
                // она только 1 раз играется, так что флаг обратно не обязательно нужно выставлять
                gameContext.setCarInService(true);
                break;
            case Посидеть_с_крестником:
                affect_Посидеть_с_крестником(gameContext, ownerId, partnerId);
                break;
            case Временная_психотерапия:
                throw new IllegalStateException(SpecialGreenCards.Временная_психотерапия + " can't be played both!");
        }
    }

    public void playCard(Location location, GameContext gameContext, int ownerId, int actorId) {
        Player actorPlayer = gameContext.getPlayers().getPlayer(ownerId);
        int currentCubeResult;
        switch (special) {
            case Временная_психотерапия:
            // * Бросьте кубик:
                // 1,2: плюс случайная оранжевая черта
                // 3,4: плюс случайная желтая черта
                // 5,6: одна оранжевая черта неактивна
                // Длительность применения : 2 раунда
                currentCubeResult = gameContext.getEventDeck().getNextCube(id);
                LinkedList<OrangeCard> cards = actorPlayer.getOrangeCards().cards;
                switch (currentCubeResult){
                    case 1:
                    case 2:
                        int randomOrange  = OrangeDeck.getRandomCard(id, OrangeCard.Type.ORANGE);
                        actorPlayer.state.setTemporaryOrangeCardForPlayer(randomOrange, 2);
                        cards.addAll(findO(randomOrange));
                        break;
                    case 3:
                    case 4:
                        int randomYellow  = OrangeDeck.getRandomCard(id, OrangeCard.Type.YELLOW);
                        actorPlayer.state.setTemporaryOrangeCardForPlayer(randomYellow, 2);
                        cards.addAll(findO(randomYellow));
                        break;
                    case 5:
                    case 6:
                        cards.stream().filter(r -> OrangeCard.Type.ORANGE.equals(r.getType())).findFirst()
                            .ifPresent(c -> c.setActive(false, 2));
                        break;
                    default:
                        throw new IllegalArgumentException("illegal cube result " + currentCubeResult);
                }
                break;
            case Сломалась_машина:
                if (gameContext.isCarInService){
                    gameContext.setCarInService(false);
                    actorPlayer.state.hasBrokenCar = false;
                }else{
                    gameContext.setCarInService(true);
                }
                break;
//            case ушиб_TILL_END_OF_WEEK:
//                actorPlayer.state.setHit(true);
//                break;
//            case заболел_SEVEN_DAYS:
//                actorPlayer.state.gotIll();
//                break;
            case Посидеть_с_крестником:
                affect_Посидеть_с_крестником(gameContext, ownerId, -1);
                break;
            case Собеседование:
                //  Бросьте кубик: 1,2: получите 1 напряг 3,4: ничего 5,6: увеличьте зп на 2
                currentCubeResult = gameContext.getEventDeck().getNextCube(id);
                switch (currentCubeResult){
                    case 1:
                    case 2:
                        actorPlayer.addTension(1);
                        break;
                    case 5:
                    case 6:
                        actorPlayer.setSalary(actorPlayer.getSalary() + 2);
                        break;
                    default:
                }
                break;
        }
    }

    private void affect_Посидеть_с_крестником(GameContext gameContext, int ownerId, int partnerId) {
        int sum2currentCubeResult = gameContext.getEventDeck().getNext2Cubes(id);
        if (sum2currentCubeResult < 5) {
// При участии партнёра получите бонус +5 Бросьте кубик 2 раза. Сумма меньше 5: ребенок закатывает истерику
// равносильно карте "скандал"
            gameContext.playScandal(false, ownerId, partnerId);
        }
    }

    @Override
    public boolean canBePlayedBy(GameContext gameContext, int ownerId, int actorId, Players copy,
        boolean borrowFromPartner) {
        switch (special) {
            case Передвинуть_шкаф:
                if (copy.getPlayer(actorId).getEnergy() < 2) {
                    return false;
                }
                break;
        }
        return super.canBePlayedBy(gameContext, ownerId, actorId, copy, borrowFromPartner);
    }

    @Override
    public boolean canBePlayedBoth(GameContext gameContext, int ownerId, Players copy, boolean borrowFromPartner) {
        switch (special) {
            case Концерт_любимой_группы:
                if (copy.getPlayerPartner(ownerId).getSatisfaction() <= 30) {
                    return false;
                }
                break;
            case Передвинуть_шкаф:
                if (copy.getPlayer(ownerId).getEnergy() < 2 && copy.getPlayerPartner(ownerId).getEnergy() < 2) {
                    return false;
                }
                break;
        }
        return super.canBePlayedBoth(gameContext, ownerId, copy, borrowFromPartner);
    }

    @Override
    public void affectPlayer(Location location, GameContext gameContext, int ownerPlayerId, int actorPlayerId, Players copy) {
        Player actorPlayer = copy.getPlayer(actorPlayerId);
        Player ownerPlayer = copy.getPlayer(ownerPlayerId);
        super.affectPlayer(location, gameContext, ownerPlayerId, actorPlayerId, copy);
        switch (special) {
            case Задержали_на_работе:
                if (actorPlayer.getEnergy() > 0) {
                    actorPlayer.setEnergy(actorPlayer.getEnergy() - 1);
                } else {
                    actorPlayer.setTension(actorPlayer.getTension() + 1);
                }
                break;
            case Концерт_любимой_группы:
                copy.getPlayer(ownerPlayerId).addSatisfaction(10);
                break;
            case Прорвало_трубу:
                copy.getPlayer(ownerPlayerId).addTension(2);
                break;
            case Передвинуть_шкаф:
                actorPlayer.setEnergy(actorPlayer.getEnergy() - 2);
                break;
            case Выигрыш_в_лотерею:
                copy.getPlayer(ownerPlayerId).addMoney(4);
                break;
            // TODO 1: преимущество карты в сортировке
            case Сломалась_машина:
                // первый раз - 0, второй раз - 100
                int constMoney =
                    gameContext.isCarInService ?
                        gameContext.discountable(10) : 0;
                copy.getPlayer(ownerPlayerId).addMoney(-constMoney);
                break;
        }
    }

    public void affectBothPlayers(Location location, GameContext gameContext, int ownerPlayerId, Players copy) {
        super.affectBothPlayers(location, gameContext, ownerPlayerId, copy);
        switch (special) {
            case Пригласили_на_ДР:
                // тут уже посчитали с обоих 30. Нужно вернуть тому у кого меньше денег - 2, а у кого больше - 1
                int minMoneyPlayer = copy.getMinMoneyPlayer();
                copy.getPlayer(minMoneyPlayer).addMoney(2).addSatisfaction(3.5);
                copy.getPlayerPartner(minMoneyPlayer).addMoney(1).addSatisfaction(3.5);
                break;
            case Концерт_любимой_группы:
                copy.getPlayer(ownerPlayerId).addSatisfaction(10);
                copy.getPlayerPartner(ownerPlayerId).addSatisfaction(3.5);
                break;
            case Прорвало_трубу:
            case Посидеть_с_крестником:
                copy.getPlayer(ownerPlayerId).addSatisfaction(5);
                break;
            case Выигрыш_в_лотерею:
                copy.getPlayer(ownerPlayerId).addMoney(4);
                break;
            case Сломалась_машина:
                // первый раз - 0, второй раз - 100
                int constMoney =
                    gameContext.isCarInService ?
                        gameContext.discountable(10) : 0;
                copy.getPlayer(ownerPlayerId).addMoney(-constMoney);
                break;
            case Передвинуть_шкаф:
                var actorPlayer = copy.getPlayer(copy.getMostEnergeticPlayer());
                actorPlayer.setEnergy(actorPlayer.getEnergy() - 2);
                break;

        }
    }

    public int getCostMoney(GameContext context) {
        switch (special) {
            case Пригласили_на_ДР:
                // тут нужно в зависимости от человека
                return context.discountable(3);
            case Временная_психотерапия:
                return context.discountable(2);
            case Концерт_любимой_группы:
                return context.discountable(2);
        }
        return super.getCostMoney(context);
    }

    public int getCostEnergy(GameContext context) {
        return super.getCostEnergy(context);
    }

    public double getSatisfaction(GameContext context) {
        return super.getSatisfaction(context);
    }

    public int getTension(GameContext context) {
        return super.getTension(context);
    }

    public String getActionType() {
        return favouriteAction.name();
    }

    private Type type;
    private boolean car;
    private FavouriteAction favouriteAction;
    private ActionTime actionTime;
    private ConsequencesOfRefusal consequencesOfRefusal;
    private SpecialGreenCards special;

    public GreenCard(String id, String name,
        Type type, boolean car, Location location, Participants participants, FavouriteAction favouriteAction,
        ActionTime actionTime, ConsequencesOfRefusal consequencesOfRefusal, SpecialGreenCards special) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.car = car;
        this.location = location;
        this.participants = participants;
        this.favouriteAction = favouriteAction;
        this.actionTime = actionTime;
        this.consequencesOfRefusal = consequencesOfRefusal;
        this.special = special;
    }


}
