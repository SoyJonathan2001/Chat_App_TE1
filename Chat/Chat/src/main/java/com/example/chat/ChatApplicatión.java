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

public class ChatApplication extends Application {

    // Componentes de la interfaz gráfica
    private TextArea chatArea;
    private TextField messageField;
    private Button sendButton;

    // Streams de entrada y salida para la comunicación
    private PrintWriter out;
    private BufferedReader in;

    // Variable para determinar si el usuario actúa como servidor o cliente
    private boolean isServer = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aplicación de Chat");

        // Inicialización de los componentes de la interfaz gráfica
        chatArea = new TextArea();
        chatArea.setEditable(false);

        messageField = new TextField();
        messageField.setPromptText("Ingresa tu mensaje...");

        sendButton = new Button("Enviar");
        sendButton.setOnAction(event -> sendMessage());

        // Diseño de la interfaz gráfica
        VBox layout = new VBox(10);
        layout.getChildren().addAll(chatArea, messageField, sendButton);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);

        // Diálogo para elegir el rol de servidor o cliente
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

        // Acción al cerrar la aplicación
        primaryStage.setOnCloseRequest(event -> closeApp());

        primaryStage.show();

        // Iniciar el servidor o el cliente según la elección del usuario
        if (isServer) {
            startServer();
        } else {
            startClient();
        }
    }

    // Método para iniciar el servidor
    private void startServer() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(0); // 0 significa que el sistema asignará un puerto disponible
                Platform.runLater(() -> {
                    chatArea.appendText("Esperando conexión en el puerto " + serverSocket.getLocalPort() + "\n");
                });

                Socket clientSocket = serverSocket.accept();
                chatArea.appendText("Conectado al cliente\n");

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while (true) {
                    String message = in.readLine();
                    if (message == null) {
                        break;
                    }
                    Platform.runLater(() -> {
                        chatArea.appendText("Cliente: " + message + "\n");
                    });
                }

                out.close();
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Método para iniciar el cliente
    private void startClient() {
        new Thread(() -> {
            try {
                Socket clientSocket = new Socket("127.0.0.1", 0); // Conexión a localhost y asignación de un puerto disponible
                chatArea.appendText("Conectado al servidor\n");

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while (true) {
                    String message = in.readLine();
                    if (message == null) {
                        break;
                    }
                    Platform.runLater(() -> {
                        chatArea.appendText("Servidor: " + message + "\n");
                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Método para enviar un mensaje
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            chatArea.appendText("Tú: " + message + "\n");
            out.println(message);
        }
        messageField.clear();
    }

    // Método para cerrar la aplicación y liberar recursos
    private void closeApp() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Setter para actualizar si la instancia actúa como servidor o cliente
    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
    }
}
