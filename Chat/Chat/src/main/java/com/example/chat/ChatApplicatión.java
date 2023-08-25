package com.example.to222;
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


public class ChatApplication extends Application {

    private List<Socket> clientSockets = new ArrayList<>();
    private List<PrintWriter> clientWriters = new ArrayList<>();


    private int serverPort;
    private TextArea chatArea;
    private TextField messageField;
    private Button sendButton;

    private PrintWriter outClient1;
    private BufferedReader inClient1;
    private PrintWriter outClient2;
    private BufferedReader inClient2;

    private boolean isServer = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat Application");

        chatArea = new TextArea();
        chatArea.setEditable(false);

        messageField = new TextField();
        messageField.setPromptText("Enter your message...");

        sendButton = new Button("Send");
        sendButton.setOnAction(event -> sendMessage());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(chatArea, messageField, sendButton);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choose Role");
        alert.setHeaderText("Choose your role in the chat");
        alert.setContentText("Choose your option:");

        ButtonType serverButton = new ButtonType("Server");
        ButtonType clientButton = new ButtonType("Client");

        alert.getButtonTypes().setAll(serverButton, clientButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == serverButton) {
                isServer = true;
            } else if (result.get() == clientButton) {
                isServer = false;
            }
        }

        primaryStage.setOnCloseRequest(event -> closeApp());

        primaryStage.show();

        if (isServer) {
            startServer();
        } else {
            startClient();
        }
    }

    private void startServer() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(0);
                serverPort = serverSocket.getLocalPort();
                Platform.runLater(() -> {
                    chatArea.appendText("Waiting for clients on port " + serverSocket.getLocalPort() + "\n");
                });

                Socket clientSocket1 = serverSocket.accept();
                Platform.runLater(() -> {
                    chatArea.appendText("Client 1 connected\n");
                });

                Socket clientSocket2 = serverSocket.accept();
                Platform.runLater(() -> {
                    chatArea.appendText("Client 2 connected\n");
                });

                outClient1 = new PrintWriter(clientSocket1.getOutputStream(), true);
                inClient1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));

                outClient2 = new PrintWriter(clientSocket2.getOutputStream(), true);
                inClient2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    Platform.runLater(() -> {
                        chatArea.appendText("Client connected\n");
                    });

                    clientSockets.add(clientSocket);
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    clientWriters.add(out);

                    String message = inClient1.readLine();
                    if (message == null) {
                        break;
                    }
                    Platform.runLater(() -> {
                        chatArea.appendText("Client 1: " + message + "\n");
                    });
                    outClient2.println(message); // Forward the message to the other client
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void startClient() {
        new Thread(() -> {
            try {
                Socket clientSocket = new Socket("127.0.0.1", serverPort);

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while (true) {
                    String message = in.readLine();
                    if (message == null) {
                        break;
                    }
                    Platform.runLater(() -> {
                        chatArea.appendText("Server: " + message + "\n");
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


    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            chatArea.appendText("You: " + message + "\n");

            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }

            messageField.clear();
        }
    }



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


    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
    }
}