package assigment.prog.pet123;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

public class HelloController {

    @FXML
    private TableColumn<Pet, Integer> clEnergy;

    @FXML
    private TableColumn<Pet, Integer> clHappiness;

    @FXML
    private TableColumn<Pet, Integer> clHunger;

    @FXML
    private TableColumn<Pet, Integer> clID;

    @FXML
    private TableColumn<Pet, String> clName;

    @FXML
    private Slider slEnergy;

    @FXML
    private Slider slHappiness;

    @FXML
    private Slider slHunger;

    @FXML
    private TextField tfName;

    @FXML
    private TableView<Pet> tablePets;

    @FXML
    private Label lblMessage;

    private final ObservableList<Pet> petList = FXCollections.observableArrayList();
    private final ObservableList<Pet> displayedPets = FXCollections.observableArrayList();


    @FXML
    void showPets(ActionEvent event) {
        displayedPets.setAll(petList);
    }

    @FXML
    void savePet(ActionEvent event) {
        String petName = tfName.getText().trim();
        int happiness = (int) slHappiness.getValue();
        int hunger = (int) slHunger.getValue();
        int energy = (int) slEnergy.getValue();

        if (!nameHasNoDigits(petName)) {
            showAlert("Validation Error", "Pet name must not contain any digit");
            return;
        }

        // prevent exact duplicate
        if (isExactDuplicate(petName, happiness, hunger, energy)) {
            showAlert("Validation Error", "This exact pet already exists.");
            return;
        }

        // prevent exact name only
        if (isNameDuplicate(petName)) {
            showAlert("Validation Error", "A pet with this name already exists.");
            return;
        }


        Pet pet = new Pet(petName, happiness, hunger, energy);
        petList.add(pet);
        lblMessage.setText("Success, Pet named:  " + petName +  "  saved! Click 'Show Pets' to see it in the table.");
        cancelInput();
    }


    @FXML
    void cancelInput() {
        tfName.clear();
        slHappiness.setValue(0);
        slEnergy.setValue(0);
        slHunger.setValue(0);
    }

    @FXML
    void quitGame(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void initialize() {
        setupTable();
        setupTableEditing();
    }

    public void setupTable() {

        clID.setCellValueFactory(new PropertyValueFactory<>("id"));
        clName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clHappiness.setCellValueFactory(new PropertyValueFactory<>("happiness"));
        clHunger.setCellValueFactory(new PropertyValueFactory<>("hunger"));
        clEnergy.setCellValueFactory(new PropertyValueFactory<>("energy"));

        tablePets.setEditable(true);
        tablePets.setItems(displayedPets);
        clID.setEditable(false);

    }
    public void setupTableEditing() {
        // Name column editing
        clName.setCellFactory(TextFieldTableCell.forTableColumn());
        clName.setOnEditCommit(event -> {
            String newName = event.getNewValue();
            if (validateName(newName)) {
                event.getRowValue().setName(newName);
            } else {
                tablePets.refresh(); // Prevent invalid value from being saved
            }
        });

        clHappiness.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        clHappiness.setOnEditCommit(event -> {
            Pet pet = event.getRowValue();
            Integer newHappiness = event.getNewValue();
            int oldValue = pet.getHappiness();

            if (validateHappiness(newHappiness)) {
                pet.setHappiness(newHappiness);
            } else {
                pet.setHappiness(oldValue);
                tablePets.refresh();
            }
        });

        clHunger.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        clHunger.setOnEditCommit(event -> {
            Pet pet = event.getRowValue();
            Integer newHunger = event.getNewValue();
            int oldValue = pet.getHunger();

            if (validateHunger(newHunger)) {
                pet.setHunger(newHunger);
            } else {
                pet.setHunger(oldValue); // restore
                tablePets.refresh();
            }
        });

        clEnergy.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        clEnergy.setOnEditCommit(event -> {
            Pet pet = event.getRowValue();
            Integer newEnergy = event.getNewValue();
            int oldValue = pet.getEnergy();

            if (validateEnergy(newEnergy)) {
                pet.setEnergy(newEnergy);
            } else {
                pet.setEnergy(oldValue); // restore
                tablePets.refresh();
            }
        });
    }

    private boolean validateName(String name) {
        if (name.trim().isEmpty() || isNameDuplicate(name)) {
            showAlert("Validation Error", "Name cannot be empty or duplicate.");
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            if (Character.isDigit(name.charAt(i))) {
                showAlert("Validation Error", "Name cannot contain any number!");
                return false;
            }
        }
        return true;
    }

    private boolean validateHappiness(Integer happiness) {
        if (happiness == null) {
            showAlert("Validation Error", "Happiness cannot be empty and must be an Integer!");
            return false;
        }
        if (happiness < 0 || happiness > 100 ) {
            showAlert("Validation Error", "Happiness must be between 0 and 100!");
            return false;
        }
        return true;
    }
    private boolean validateHunger(Integer hunger) {
        if (hunger == null) {
            showAlert("Validation Error", "Hunger cannot be empty and must be an Integer!");
            return false;
        }
        if (hunger < 0 || hunger > 100) {
            showAlert("Validation Error", "Hunger must be between 0 and 100!");
            return false;
        }
        return true;
    }
    private boolean validateEnergy(Integer energy) {
        if (energy == null) {
            showAlert("Validation Error", "Energy cannot be empty and must be an Integer!");
            return false;
        }
        if (energy < 0 || energy > 100) {
            showAlert("Validation Error", "Energy must be between 0 and 100!");
            return false;
        }
        return true;
    }

    private boolean isExactDuplicate(String name, int happiness, int hunger, int energy) {
        for (Pet pet : petList) {
            if (pet.getName().equals(name)
                && pet.getHappiness() == happiness
                && pet.getHunger() == hunger
                && pet.getEnergy() == energy) {
                return true;
            }
        }
        return false;
    }
    private boolean isNameDuplicate(String name) {
        for (Pet pet : petList) {
            if (pet.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    private boolean nameHasNoDigits(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (Character.isDigit(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public class SafeIntegerStringConverter extends IntegerStringConverter {
        @Override
        public Integer fromString(String value) {
            try {
                return super.fromString(value);
            } catch (NumberFormatException e) {
                return null; // This validate() method to handle the error
            }
        }
    }
}
