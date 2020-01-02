package atj;

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
}
