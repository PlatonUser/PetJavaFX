package assigment.prog.pet123;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Pet {
    private static int nextId = 1;
    public final static int MAX_VALUE = 100;
    public final static int MIN_VALUE = 0;

    // Attributes
    private final SimpleIntegerProperty id;
    private SimpleStringProperty name;
    private final SimpleIntegerProperty happiness;
    private final SimpleIntegerProperty hunger;
    private final SimpleIntegerProperty energy;

    public Pet(String name, int happiness, int hunger, int energy) {
        this.id = new SimpleIntegerProperty(getNextId());
        this.name = new SimpleStringProperty(name);
        this.happiness = new SimpleIntegerProperty(happiness);
        this.hunger = new SimpleIntegerProperty(hunger);
        this.energy = new SimpleIntegerProperty(energy);
    }
    private int constrain(int value) {
        return Math.max(MIN_VALUE, Math.min(MAX_VALUE, value));
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }
    public void setHunger(int hunger) {
        this.hunger.set(constrain(hunger));
    }
    public void setEnergy(int energy) {
        this.energy.set(constrain(energy));
    }
    public void setHappiness(int happiness) {
        this.happiness.set(constrain(happiness));
    }

    public int getHappiness() {
        return happiness.get();
    }

    public int getHunger() {
        return hunger.get();
    }

    public int getEnergy() {
        return energy.get();
    }

    public static int getNextId() {
        return nextId++;
    }
}



