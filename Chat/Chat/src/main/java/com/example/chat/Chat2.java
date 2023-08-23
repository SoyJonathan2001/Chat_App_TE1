package com.example.chat;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Chat2 extends Application {

    private VBox chatContainer;
    private TextField messageField;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f0f0;");

        // Área de chat
        chatContainer = new VBox(10);
        chatContainer.setPadding(new Insets(10));
        chatContainer.setAlignment(Pos.TOP_LEFT);
        ScrollPane scrollPane = new ScrollPane(chatContainer);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        // Campo de entrada de mensajes
        messageField = new TextField();
        messageField.setPromptText("Escribe un mensaje...");
        messageField.setStyle("-fx-background-color: white;");
        messageField.setOnAction(e -> sendMessage());
        HBox inputContainer = new HBox(messageField);
        inputContainer.setPadding(new Insets(10));
        root.setBottom(inputContainer);

        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle("Chat de Mensajes Dinámico");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            Label messageLabel = new Label(message);
            messageLabel.getStyleClass().add("message-label");
            chatContainer.getChildren().add(messageLabel);
            animateMessage(messageLabel);
            messageField.clear();
        }
    }

    private void animateMessage(Label messageLabel) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), messageLabel);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
