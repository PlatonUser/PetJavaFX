module assigment.prog.pet123 {
    requires javafx.controls;
    requires javafx.fxml;


    opens assigment.prog.pet123 to javafx.fxml;
    exports assigment.prog.pet123;
}