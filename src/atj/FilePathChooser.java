package atj;

import javafx.stage.FileChooser;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class FilePathChooser {

    public static Optional<String> getFilePath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File choosenFile = fileChooser.showOpenDialog(null);
        return Objects.isNull(choosenFile) ? Optional.empty() : Optional.of(choosenFile.getAbsolutePath());
    }
}
