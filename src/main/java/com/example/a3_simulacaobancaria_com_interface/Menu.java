package com.example.a3_simulacaobancaria_com_interface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.List;

public class Menu extends Application {

    public static FilaAtendimento filaGlobal = new FilaAtendimento();
    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws Exception {

        mainStage = stage; // ⬅️ AQUI salvamos o stage principal

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Atendimento Bancário");
        stage.setScene(scene);
        stage.show();
    }

    public static void mudarTela(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(Menu.class.getResource(fxml));
            Scene novaCena = new Scene(loader.load());

            mainStage.setScene(novaCena); // ⬅️ agora funciona!
            mainStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // Carrega os clientes salvos no .dat
        List<Cliente> carregados = ArquivoClientes.carregarClientes();
        for (Cliente c : carregados) {
            filaGlobal.adicionarCliente(c);
        }

        launch();
    }
}
