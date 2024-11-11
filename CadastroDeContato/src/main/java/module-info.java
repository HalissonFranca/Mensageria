module com.mensageria.cadastrodecontato {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires static lombok;

    opens com.mensageria.cadastrodecontato to javafx.fxml;
    exports com.mensageria.cadastrodecontato;
}