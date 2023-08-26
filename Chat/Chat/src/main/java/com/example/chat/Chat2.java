package com.example.chat;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class Chat2 extends Application {

    private VBox chatContainer;
    private TextField messageField;
    private Label selectedContactLabel;

    private ListView<String> contactsListView;
    private TextField searchField;

    private Map<String, VBox> chatsByContact;
    private ObservableList<String> contacts;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1E1E1E;");

        chatsByContact = new HashMap<>();
        contacts = FXCollections.observableArrayList(
                "Contacto 1", "Contacto 2", "Contacto 3", "Contacto 4"
        );

        // Lista de contactos
        contactsListView = new ListView<>(contacts);
        contactsListView.setPrefWidth(200);
        contactsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateChatForContact(newValue);
        });

        // Buscador de contactos
        searchField = new TextField();
        searchField.setPromptText("Buscar contacto...");
        Button addButton = new Button("Agregar");
        addButton.setOnAction(e -> addContact());
        Button connectButton = new Button("Conectar al Servidor");
        connectButton.setOnAction(e -> connectToServer());
        VBox contactsPane = new VBox(10, searchField, contactsListView, connectButton, addButton);
        contactsPane.setPadding(new Insets(10));
        contactsPane.setStyle("-fx-background-color: #333333;");
        root.setLeft(contactsPane);

        // Área de chat
        chatContainer = new VBox(10);
        chatContainer.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(chatContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.vvalueProperty().bind(chatContainer.heightProperty());
        root.setCenter(scrollPane);

        // Etiqueta del contacto seleccionado
        selectedContactLabel = new Label("Selecciona un contacto");
        selectedContactLabel.setStyle("-fx-padding: 10px;");
        selectedContactLabel.setPrefWidth(200);
        selectedContactLabel.setAlignment(Pos.CENTER_LEFT);
        selectedContactLabel.setTextFill(Color.web("#FF8800"));
        selectedContactLabel.setEffect(new DropShadow(10, Color.BLACK));
        root.setTop(selectedContactLabel);

        // Campo de entrada de mensajes
        messageField = new TextField();
        messageField.setPromptText("Escribe un mensaje...");
        messageField.setStyle(
                "-fx-background-color: #1E1E1E; -fx-padding: 10px; -fx-font-size: 14px; -fx-text-fill: gold;");
        messageField.setOnAction(e -> sendMessage());
        HBox inputContainer = new HBox(messageField);
        inputContainer.setPadding(new Insets(10));
        inputContainer.setStyle("-fx-background-color: #333333;");
        root.setBottom(inputContainer);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Chat Estilizado");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateChatForContact(String contact) {
        selectedContactLabel.setText("Chat con: " + contact);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), selectedContactLabel);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.play();

        chatContainer.getChildren().clear();

        if (chatsByContact.containsKey(contact)) {
            chatContainer.getChildren().add(chatsByContact.get(contact));
        } else {
            VBox newChat = new VBox(10);
            chatsByContact.put(contact, newChat);
            chatContainer.getChildren().add(newChat);
        }
    }

    private void sendMessage() {
        String contact = selectedContactLabel.getText().replace("Chat con: ", "");
        String message = messageField.getText();
        if (!message.isEmpty()) {
            Label messageLabel = new Label(message);
            messageLabel.getStyleClass().add("message-label");
            messageLabel.setTextFill(Color.web("#000000"));
            messageLabel.setBackground(new Background(new BackgroundFill(Color.web("#A2FFAB"), CornerRadii.EMPTY, Insets.EMPTY)));
            messageLabel.setPadding(new Insets(5, 10, 5, 10));
            chatsByContact.get(contact).getChildren().add(messageLabel);
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

    private void addContact() {
        String newContact = searchField.getText();
        if (!newContact.isEmpty()) {
            contacts.add(newContact);
            searchField.clear();
        }
    }

    private void connectToServer() {
        // Aquí podrías agregar la lógica para conectarse al servidor
    }

    public static void main(String[] args) {
        launch(args);
    }
}
