package com.game.model;

import static com.game.model.FavouriteAction.B;
import static com.game.model.FavouriteAction.BB;
import static com.game.sets.OrangeCardsTestSet.findO;

import java.util.Objects;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

@Data
public class Player {

    PlayerState state = new PlayerState();

    public int id;
    CardSet<OrangeCard> orangeCards = new CardSet<>();
    private double satisfaction;
    private int tension;

    private int stamina;
    private int energy;

    private int salary;
    private int money;
    private String favouriteActionType;

    public Player(int id, int salary, int stamina, FavouriteAction favouriteActionType) {
        this.id = id;
        this.salary = salary;
        this.stamina = stamina;
        this.favouriteActionType = favouriteActionType.name();
    }

    public Player copy() {
        Player player = new Player(id, this.salary, this.stamina,
            FavouriteAction.valueOf(this.favouriteActionType));
        player.setEnergy(this.energy);
        player.setMoney(this.money);
        player.setSatisfaction(this.satisfaction);
        player.setTension(this.tension);
        player.setState(this.state.copy());
        CardSet<OrangeCard> newOrangeCards = new CardSet<>();
        newOrangeCards.cards.addAll(this.orangeCards.cards);
        player.setOrangeCards(newOrangeCards);  // deep copy
        return player;
    }

    public Player updatedVersion() {
        return copy().initRound();
    }

    public void finishWeek() {
        state.isHit = false;
        state.temporaryCards.forEach((k, v) -> {
            int upV = v.decrementAndGet();
            if (upV == 0) {
                orangeCards.cards.removeIf(card -> Objects.equals(card.getId(), k));
                state.temporaryCards.remove(k);
            }
        });
    }

    public Player initRound() {
        energy = stamina;
        money += salary;
        return this;
    }

    public void addSatisfaction(double v) {
        setSatisfaction(satisfaction + v);
    }

    public void setSatisfaction(double satisfaction) {
        this.satisfaction = checkNotNegative(satisfaction, "satisfaction");
    }

    public void addTension(int add) {
        setTension(this.tension + add);
    }
    public void setTension(int tension) {
        this.tension = Math.max(tension, 0);
    }

    public void setStamina(int stamina) {
        this.stamina = checkNotNegative(stamina, "stamina");
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(checkNotNegative(energy, "energy"), stamina);
    }

    public void setSalary(int salary) {
        this.salary = checkNotNegative(salary, "salary");
    }

    public Player addMoney(int add) {
        setMoney(money + add);
        return this;
    }
    public void setMoney(int money) {
        this.money = checkNotNegative(money, "money");
    }

    private double checkNotNegative(double parameter, String name) {
        if (parameter < 0) {
            throw new IllegalArgumentException("[" + name + "]" + parameter + " should be >=0");
        }
        return parameter;
    }
    private int checkNotNegative(int parameter, String name) {
        if (parameter < 0) {
            throw new IllegalArgumentException("[" + name + "]" + parameter + " should be >=0");
        }
        return parameter;
    }

    public String getSatTensionString() {
        long round = Math.round(satisfaction);
        return " {" + round + " \uD83C\uDF82}, [" + tension + "\uD83D\uDCA5]";
    }

    public String getMoneyEnergyString() {
        return getMoney() + "\uD83D\uDCB0\t\t" + getEnergy() + " \uD83D\uDCAA";
    }

    public void add(Player player) {
        satisfaction += player.satisfaction;
        tension += player.tension;

        stamina += player.stamina;
        energy += player.energy;

        salary += player.salary;
        money += player.money;
    }

    public void divide(int size) {
        if (size > 0) {
            satisfaction /= size;
            tension /= size;

            stamina /= size;
            energy /= size;

            salary /= size;
            money /= size;
        } else {
            satisfaction = 0;
            tension = 0;

            stamina = 0;
            energy = 0;

            salary = 0;
            money = 0;
        }
    }

    boolean isFavouriteActionType(BlueGreenCard blueGreenCard) {
        boolean isFavouriteActionType = getFavouriteActionType().equalsIgnoreCase(blueGreenCard.getActionType().substring(0, 1));
        return isFavouriteActionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return id == player.id && Objects.equals(state, player.state) && satisfaction == player.satisfaction
            && tension == player.tension
            && stamina == player.stamina && energy == player.energy && salary == player.salary && money == player.money
            && Objects.equals(favouriteActionType, player.favouriteActionType)
        && CollectionUtils.isEqualCollection(orangeCards.cards, player.orangeCards.cards)
            ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, satisfaction, tension, stamina, energy, salary, money, favouriteActionType);
    }

    public int get(String property) {
        property = property.trim();
        if ("tension".equals(property)){
            return this.tension;
        }
        if ("money".equals(property)){
            return this.money;
        }
        if ("satisfaction" .equals(property)) {
            return (int) this.satisfaction;
        }
        if ("energy" .equals(property)) {
            return this.energy;
        }
        throw new IllegalArgumentException("Unknown property: " + property);
    }

    boolean isB() {
        FavouriteAction favouriteAction = FavouriteAction.valueOf(this.favouriteActionType);
        return B.equals(favouriteAction) || BB.equals(favouriteAction);
    }
}
