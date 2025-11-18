module com.example.a3_simulacaobancaria_com_interface {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;


    opens com.example.a3_simulacaobancaria_com_interface to javafx.fxml;
    exports com.example.a3_simulacaobancaria_com_interface;
}