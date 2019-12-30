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

import java.nio.file.Path;

public class WebSocketChatController {

    @FXML
    TextArea chatTextArea;
    @FXML
    TextField messageTextField, filePathView, userTextField;
    @FXML
    Button btnSet, btnUpload, btnSend;

    private String user, filePath;
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
        webSocketClient.sendMessage(messageTextField.getText());

    }

    public void closeSession(CloseReason closeReason) {
        try {
            webSocketClient.session.close(closeReason);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) {


//            webSocketClient.sendFile(filePathView.getText(), filePathView.getText());

//        TODO: sprawdzić dlaczego kod poniżej nie działa
        if ((filePath.equals(null))) {
            btnUpload.disableProperty();
        } else {
            webSocketClient.sendFile(filePath);
        }
//        filePath.equals(null) ? btnUpload.disableProperty() : webSocketClient.sendFile(pathName, filePath); // nie działa :-(

    }

    public void chooseFile(ActionEvent actionEvent) {

        Optional<String> filePathView = FilePathChooser.getFilePath();
        if (filePathView.isPresent()) {
            this.filePathView.setText(filePathView.get());
            this.filePath = filePathView.get();
        } else {
            this.filePathView.clear();
            this.filePath = null;
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

        public void sendMessage(String message) {
            chatTextArea.appendText(user + ": " + message + "\n");
            try {
                System.out.println("Message was sent: " + message);
                session.getBasicRemote().sendText(user + ": " + message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        public void sendFile(String filePath) {
            Path chosenFileName=new File(filePathView.getText()).toPath();
            Path fileName = chosenFileName.getFileName();

            chatTextArea.appendText(user + " send you a file: " + chosenFileName
                    + "\n" + "if you want download this file please click on link "
                    + fileName + "\n");
            try {
                System.out.println("File was sent: " + filePath);

                ;
                session.getBasicRemote()
                        .sendBinary(ByteBuffer
                                .wrap(Files.readAllBytes(chosenFileName)));
                //TODO: jak w powyższej metodzie przesłac nazwę pliku do serwera razem z bajtami pliku

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        public void getFile(String filePath){




        }




    }

}
