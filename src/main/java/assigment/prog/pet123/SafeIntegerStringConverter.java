package assigment.prog.pet123;

import javafx.util.converter.IntegerStringConverter;

public class SafeIntegerStringConverter extends IntegerStringConverter {
    @Override
    public Integer fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null; // Return null if input is empty
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null; // Handle invalid number format
        }
    }

    @Override
    public String toString(Integer value) {
        return value == null ? "" : value.toString();
    }
}
