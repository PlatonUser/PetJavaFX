package assigment.prog.pet123;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;

public class Pet implements Comparable<Pet> {
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
    public Pet(int id, String name, int happiness, int hunger, int energy) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.happiness = new SimpleIntegerProperty(happiness);
        this.hunger = new SimpleIntegerProperty(hunger);
        this.energy = new SimpleIntegerProperty(energy);
    }

    @Override
    public int compareTo(Pet other) {
        return Integer.compare(this.getId(), other.getId());
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
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidPetException("Name cannot be empty.");
        }
        this.name = new SimpleStringProperty(name);
    }

    public void setHunger(int hunger) {
        if (hunger < MIN_VALUE || hunger > MAX_VALUE) {
            throw new InvalidPetException("Hunger must be between " + MIN_VALUE + " and " + MAX_VALUE);
        }
        this.hunger.set(hunger);
    }

    public void setEnergy(int energy) {
        if (energy < MIN_VALUE || energy > MAX_VALUE) {
            throw new InvalidPetException("Energy must be between " + MIN_VALUE + " and " + MAX_VALUE);
        }
        this.energy.set(energy);
    }

    public void setHappiness(int happiness) {
        if (happiness < MIN_VALUE || happiness > MAX_VALUE) {
            throw new InvalidPetException("Happiness must be between " + MIN_VALUE + " and " + MAX_VALUE);
        }
        this.happiness.set(happiness);
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

    public static class InvalidPetException extends RuntimeException {
        public InvalidPetException(String message) {
            super(message);
        }
    }
}



