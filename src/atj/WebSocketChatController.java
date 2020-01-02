package atj;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Optional;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class WebSocketChatController {

    @FXML
    TextArea chatTextArea;
    @FXML
    TextField messageTextField, filePathView, userTextField;
    @FXML
    Button btnSet, btnUpload, btnSend;

    private String user;
    private File file;
    private WebSocketClient webSocketClient;

    @FXML
    private void initialize() {
        webSocketClient = new WebSocketClient();
        user = userTextField.getText();
    }

    @FXML
    private void btnSet_Click() {
        if (userTextField.getText().isEmpty()) {
            return;
        }
        user = userTextField.getText();
    }

    @FXML
    private void btnSend_Click() {
        webSocketClient.sendMessageWithUserName(messageTextField.getText());
        webSocketClient.showMessageOnGUI(messageTextField.getText());

    }

    public void closeSession(CloseReason closeReason) {
        try {
            webSocketClient.session.close(closeReason);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) {

        webSocketClient.sendMessage("#123456789#" + file.getName());
        webSocketClient.sendFile();

    }

    public void chooseFile(ActionEvent actionEvent) {

        Optional<File> file = FilePathChooser.getFile();
        if (file.isPresent()) {
            this.filePathView.setText(file.get().getAbsolutePath());
            this.file = file.get();
//            System.out.println(file.get().getAbsolutePath());
            this.btnUpload.setDisable(false);
        } else {
            this.filePathView.clear();
            this.file = null;
            this.btnUpload.setDisable(true);
        }


    }

    @ClientEndpoint
    public class WebSocketClient {
        private Session session;

        public WebSocketClient() {
            connectToWebSocket();
        }

        @OnOpen
        public void onOpen(Session session) {
            System.out.println("Connection is opened.");
            this.session = session;
        }

        @OnClose
        public void onClose(CloseReason closeReason) {
            System.out.println("Connection is closed: " + closeReason.getReasonPhrase());
        }

        @OnError
        public void onError(Throwable throwable) {
            System.out.println("Error occured");
            throwable.printStackTrace();
        }

        @OnMessage
        public void onMessage(String message, Session session) {
            System.out.println("Message was received");
            chatTextArea.setText(chatTextArea.getText() + message + "\n");
        }

        private void connectToWebSocket() {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            try {
                URI uri = URI.create("ws://localhost:8080/chatendpoint");
                container.connectToServer(this, uri);
            } catch (DeploymentException | IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessageWithUserName(String message) {

            try {
                System.out.println("Message was sent: " + message);
                session.getBasicRemote().sendText(user + ": " + message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void sendMessage(String message) {

            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void showMessageOnGUI(String message) {
            chatTextArea.appendText(user + ": " + message + "\n");

        }


        public void sendFile() {

            try {
                System.out.println("File was sent: " + file.getAbsolutePath());
                session.getBasicRemote()
                        .sendBinary(ByteBuffer
                                .wrap(Files.readAllBytes(file.toPath())));

            } catch (IOException ex) {

                ex.printStackTrace();
            }
        }

        public void getFile(String filePath) {


        }


    }

}
