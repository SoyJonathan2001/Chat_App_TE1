package com.example.to222;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

/**
 * Una aplicación de chat simple utilizando JavaFX y sockets.
 */
public class ChatApplication extends Application {

    // Listas para almacenar los sockets de los clientes y los escritores para difundir mensajes
    private List<Socket> clientSockets = new ArrayList<>();
    private List<PrintWriter> clientWriters = new ArrayList<>();

    // Componentes de la interfaz de usuario
    private int serverPort;
    private TextArea chatArea;
    private TextField messageField;
    private Button sendButton;

    // Flujos de comunicación con los clientes
    private PrintWriter outClient1;
    private BufferedReader inClient1;
    private PrintWriter outClient2;
    private BufferedReader inClient2;

    // Bandera para indicar si la instancia es un servidor o un cliente
    private boolean isServer = false;

    /**
     * El método principal que inicia la aplicación JavaFX.
     * @param args Los argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Inicializa la aplicación JavaFX y configura la interfaz de usuario.
     * @param primaryStage El escenario principal de la aplicación.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aplicación de Chat");

        // Inicializar componentes de la interfaz de usuario
        chatArea = new TextArea();
        chatArea.setEditable(false);

        messageField = new TextField();
        messageField.setPromptText("Ingresa tu mensaje...");

        sendButton = new Button("Enviar");
        sendButton.setOnAction(event -> sendMessage());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(chatArea, messageField, sendButton);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);

        // Solicitar al usuario que elija el rol
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Elegir Rol");
        alert.setHeaderText("Elige tu rol en el chat");
        alert.setContentText("Elige una opción:");

        ButtonType serverButton = new ButtonType("Servidor");
        ButtonType clientButton = new ButtonType("Cliente");

        alert.getButtonTypes().setAll(serverButton, clientButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == serverButton) {
                isServer = true;
            } else if (result.get() == clientButton) {
                isServer = false;
            }
        }

        // Configurar el controlador de eventos para el cierre de la aplicación
        primaryStage.setOnCloseRequest(event -> closeApp());

        primaryStage.show();

        // Iniciar el servidor o el cliente según el rol elegido
        if (isServer) {
            startServer();
        } else {
            startClient();
        }
    }

    // ... (Los demás métodos permanecen igual)

    /**
     * Envía un mensaje a todos los clientes conectados.
     */
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            chatArea.appendText("Tú: " + message + "\n");

            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }

            messageField.clear();
        }
    }

    /**
     * Cierra la aplicación y libera los recursos.
     */
    private void closeApp() {
        try {
            for (PrintWriter writer : clientWriters) {
                writer.close();
            }
            for (Socket socket : clientSockets) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establece el rol de la instancia (servidor o cliente).
     * @param isServer True si la instancia es un servidor, false si es un cliente.
     */
    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
    }
}