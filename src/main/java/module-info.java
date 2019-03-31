module dbdesktopapp {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.commons.lang3;

    exports pw.po to javafx.graphics;
    opens pw.po to javafx.fxml, javafx.base;
}