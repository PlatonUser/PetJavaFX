package assigment.prog.pet123;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class PetController {

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
    private TextField tfFindPet;

    @FXML
    private TableView<Pet> tablePets;

    @FXML
    private Label lblMessage;

    Map<String, Pet> petMap = new HashMap<>();



    @FXML
    void savePet(ActionEvent event) {
        String petName = tfName.getText().trim();
        int happiness = (int) slHappiness.getValue();
        int hunger = (int) slHunger.getValue();
        int energy = (int) slEnergy.getValue();

        if (!hasDigit(petName)) {
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

        String keyName = petName.toLowerCase();
        int id = generateNextId();
        Pet pet = new Pet(id, petName, happiness, hunger, energy);
        petMap.put(keyName, pet);
        lblMessage.setText("Success, Pet named:  " + petName +  "  saved! Click 'Show Pets' to see it in the table.");
        clearInput();
    }

    @FXML
    void showPets(ActionEvent event) {
        ObservableList<Pet> displayedPets = FXCollections.observableArrayList(petMap.values());
        tablePets.setItems(displayedPets);
    }


    @FXML
    void clearInput() {
        tfName.clear();
        slHappiness.setValue(0);
        slEnergy.setValue(0);
        slHunger.setValue(0);
    }

    @FXML
    void quitGame(ActionEvent event) {
        Platform.exit();
    }


    private void searchPet() {
        String inputName = tfFindPet.getText().trim();

        if (inputName.isEmpty()) {
            showAlert("Search Error", "Please enter a name to search.");
            return;
        }

        Pet foundPet = petMap.get(inputName.toLowerCase());
        if (foundPet != null) {
            String petInfo = "ID: " + foundPet.getId() + "\n" +
                    "Name: " + foundPet.getName() + "\n" +
                    "Happiness: " + foundPet.getHappiness() + "\n" +
                    "Hunger: " + foundPet.getHunger() + "\n" + "Energy: " + foundPet.getEnergy();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pet Found");
            alert.setHeaderText("Details of the Pet:");
            alert.setContentText(petInfo);
            alert.showAndWait();
        }else {
            showAlert("Not Found", "Pet not found.");
        }
        tfFindPet.clear();

    }


    @FXML
    void importPet() {
        String filePath = "pets_import.xlsx";
        // Use try-with-resources to ensure that the FileInputStream and Workbook are properly closed
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            // Get the first sheet in the Excel workbook
            Sheet sheet = workbook.getSheetAt(0);

            if (sheet.getLastRowNum() < 1) {
                showAlert("Import Error", "File is empty. Nothing to import.");
                return;
            }
            // Get an iterator over the rows in the sheet
            Iterator<Row> rowIterator = sheet.iterator();
            // Skip the header row if it exists
            if (rowIterator.hasNext()) rowIterator.next(); // пропустити заголовок

            int validCount = 0;
            int invalidCount = 0;
            // Iterate through the remaining rows in the sheet
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (row == null || row.getCell(0) == null) continue;

                try {
                    int id = (int) row.getCell(0).getNumericCellValue();
                    String name = row.getCell(1).getStringCellValue();
                    int happiness = (int) row.getCell(2).getNumericCellValue();
                    int hunger = (int) row.getCell(3).getNumericCellValue();
                    int energy = (int) row.getCell(4).getNumericCellValue();


                    if (validateName(name) &&
                            validateHappiness(happiness) &&
                            validateHunger(hunger) &&
                            validateEnergy(energy)) {

                        Pet pet = new Pet(id, name, happiness, hunger, energy);
                        petMap.put(name.toLowerCase(), pet);
                        validCount++;
                    } else {
                        invalidCount++;
                    }

                } catch (Exception e) {
                    invalidCount++;
                }
            }

            // Оновлення таблиці — сортуємо за ID
            List<Pet> petList = new ArrayList<>(petMap.values());
            Collections.sort(petList, new Comparator<Pet>() {
                @Override
                public int compare(Pet p1, Pet p2) {
                    return Integer.compare(p1.getId(), p2.getId());
                }
            });
            tablePets.setItems(FXCollections.observableArrayList(petList));


            // Підсумкове повідомлення
            if (validCount == 0) {
                showAlert("Import Result", "No pets were imported. All of them are invalid");
            } else {
                String message = "Succesfully imported: " + validCount + " Pet(s).";
                if (invalidCount > 0) {
                    message += "\nInvalid Pets count: " + invalidCount;
                }
                showInfo("Import Result", message);
            }

        } catch (IOException e) {
            showAlert("Import Error", "Could not open the file:\n" + e.getMessage());
        }
    }


    @FXML
    void exportPet() {
        String filePath = "pets_export.xlsx"; // Шлях до файлу
        // Use try-with-resources to ensure the Workbook is properly closed after use,
        // even if exceptions occur. XSSFWorkbook is used for .xlsx files.
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create a new sheet in the Excel workbook named "Pets"
            Sheet sheet = workbook.createSheet("Pets");
            // Initialize the row index to keep track of the current row being written
            int rowIndex = 0;

            // Create the header row for the columns
            Row header = sheet.createRow(rowIndex++);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Happiness");
            header.createCell(3).setCellValue("Hunger");
            header.createCell(4).setCellValue("Energy");

            // Iterate through each entry (key-value pair) in the input Map
            for (Pet pet : petMap.values()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(pet.getId());
                row.createCell(1).setCellValue(pet.getName());
                row.createCell(2).setCellValue(pet.getHappiness());
                row.createCell(3).setCellValue(pet.getHunger());
                row.createCell(4).setCellValue(pet.getEnergy());
            }
            // Use another try-with-resources to ensure the FileOutputStream is properly closed
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                // Write the contents of the Workbook to the specified file path
                workbook.write(fos);
            }
            showInfo("Export", "Pets exported to:\n" + filePath);
        } catch (IOException e) {
            showAlert("Export Error", "An error occurred: " + e.getMessage());
        }
    }


    @FXML
    void deletePet() {
        Pet selected = tablePets.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("No Selection", "Please select a pet to delete.");
            return;
        }

        String key = selected.getName().toLowerCase();

        if (petMap.containsKey(key)) {
            petMap.remove(key);
            tablePets.setItems(FXCollections.observableArrayList(petMap.values())); // оновлюємо
            lblMessage.setText("Pet \"" + selected.getName() + "\" has been deleted.");
        } else {
            showAlert("Error", "Pet not found in internal data.");
        }
    }

    @FXML
    public void initialize() {
        setupTable();
        setupTableEditing();

        tfFindPet.setOnAction(event -> searchPet());
    }

    public void setupTable() {

        clID.setCellValueFactory(new PropertyValueFactory<>("id"));
        clName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clHappiness.setCellValueFactory(new PropertyValueFactory<>("happiness"));
        clHunger.setCellValueFactory(new PropertyValueFactory<>("hunger"));
        clEnergy.setCellValueFactory(new PropertyValueFactory<>("energy"));

        tablePets.setEditable(true);

        ObservableList<Pet> displayedPets = FXCollections.observableArrayList(petMap.values());
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
        for (Pet pet : petMap.values()) {
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
        return petMap.containsKey(name.toLowerCase());
    }
    private boolean hasDigit(String name) {
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
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private int generateNextId() {
        int maxId = 0;
        for (Pet pet : petMap.values()) {
            if (pet.getId() > maxId) {
                maxId = pet.getId();
            }
        }
        return maxId + 1;
    }
}
