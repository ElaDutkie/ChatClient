package atj;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class FilePathChooser {

    public static Optional<File> getFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File chosenFile = fileChooser.showOpenDialog(null);
        return Objects.isNull(chosenFile) ? Optional.empty() : Optional.of(chosenFile);

    }
    public static Optional<File> getDirectory(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Please select the path to save the file.");
        File directoryChosen = directoryChooser.showDialog(null);
        return Objects.isNull(directoryChosen) ? Optional.empty():Optional.of(directoryChosen);
    }
}
