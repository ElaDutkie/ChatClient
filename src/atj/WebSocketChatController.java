package atj;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    TextField messageTextField, filePathView, userTextField, sentFileTextField;
    @FXML
    Button btnSet, btnUpload, btnSend, btnChooseFile, btnDownload;

    //TODO: zrobić tak żeby w oknie czatu nick był pogrubiony, chyba trzeba będzie użyć html
    private String user;
    private File file;
    private WebSocketClient webSocketClient;
    private String uploadingFileName;
    private byte[] bufferedFile;

    public void setUploadingFileName(String uploadingFileName) {
        this.uploadingFileName = uploadingFileName;
    }

    public void setBufferedFile(byte[] bufferedFile) {
        this.bufferedFile = bufferedFile;
    }

    @FXML
    private void initialize() {
        webSocketClient = new WebSocketClient(this);
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
        messageTextField.clear();

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
        webSocketClient.sendMessageWithUserName("send you a file: " + file.getName() );
        sentFileTextField.setText(file.getName());


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

    //TODO: zaimplpementować okienko informujące o przesłaniu pliku z opcją pobierania go
    public void download(ActionEvent actionEvent) throws IOException {
        System.out.println(bufferedFile.length);
        System.out.println(uploadingFileName);
        Optional<File> file = FilePathChooser.getDirectory();
        if (!file.isPresent()) {
            return;
        }

        File fileName = new File(file.get().getAbsolutePath()+"/" + uploadingFileName);
        Files.write(fileName.toPath(), bufferedFile);
    }

    @ClientEndpoint
    public class WebSocketClient {

        private Session session;

        private WebSocketChatController webSocketChatController;

        public WebSocketClient(WebSocketChatController webSocketChatController) {
            this.webSocketChatController = webSocketChatController;
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
            if (message.contains("#123456789#")) {
                webSocketChatController.setUploadingFileName(message.replace("#123456789#", ""));
//                chatTextArea.setText(chatTextArea.getText() + user + ": " + "send you a file " + uploadingFileName + "\n");
            } else {
                chatTextArea.setText(chatTextArea.getText() + message + "\n");
            }
        }

        @OnMessage
        public void onMessage(byte[] buffer, Session session) {
            System.out.println("File was caught.");
            btnDownload.setDisable(false);
            sentFileTextField.setText(uploadingFileName);
            webSocketChatController.setBufferedFile(buffer);

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
//                messageTextField.setText("");
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

    }

}
