module assigment.prog.pet123 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;


    opens assigment.prog.pet123 to javafx.fxml;
    exports assigment.prog.pet123;
}