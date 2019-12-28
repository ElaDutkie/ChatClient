package atj;

import java.awt.Container;
import java.io.IOException;
import java.net.URI;
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
    TextField userTextField;
    @FXML
    TextArea chatTextArea;
    @FXML
    TextField messageTextField, filePathView;
    @FXML
    Button btnSet, btnUpload;
    @FXML
    Button btnSend;
    private String user;
    private String filePath;
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

    public void Upload(ActionEvent actionEvent) {

        //TODO: sprawdzić czy jest nullem, jak jest to dać użytkonikowi informację
        // metody, które ustawiają niewidoczność komórki
        // .diseable, . eneable - np kiedy nie wybrano pliku, a jak wybrano to wyslij

        //TODO: zrobić refaktoring projektu - nazwy metody z małej, choosefile upload,
        //TODO: pogrupować na górze zmienne, po przcinku Óak dla user,
        // TODO: zastanowić się nad koncepcją, zapytać prowadzącego jak by chciał, czy na serwer czy do konkretnej osoby

//        if(){

//        }
//        else{}
    }

    public void ChooseFile(ActionEvent actionEvent) {

        Optional<String> filePath = FilePathChooser.getFilePath();
        if (filePath.isPresent()) {
            filePathView.setText(filePath.get());
            this.filePath = filePath.get();
        } else {
            filePathView.clear();
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
//        private void connectToWebSocketFileServer() {
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            try {
//                URI uri = URI.create("ws://localhost:8080/receive/fileserver");
//                container.connectToServer(this, uri);
//            } catch (DeploymentException | IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void sendFile(String message) {
//            chatTextArea.appendText(user + ": " + message + "\n");
//            try {
//                System.out.println("Message was sent: " + message);
//                session.getBasicRemote().sendText(user + ": " + message);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }


        public void sendFile() {

        }

    }

}
