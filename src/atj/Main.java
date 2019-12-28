package atj;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Window.fxml"));
		AnchorPane root = fxmlLoader.load();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("JavaFX Web Socket Client");
		//primaryStage.setOnHiding(e -> prmaryStage_Hiding(e, fxmlLoader));
		primaryStage.show();
		
		} catch (Exception e) {e.printStackTrace();}
		/*try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}*/
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
