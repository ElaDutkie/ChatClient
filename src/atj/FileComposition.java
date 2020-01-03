package atj;

public class FileComposition {
    private byte[] files;
    private String fileName;

    public byte[] getFiles() {
        return files;
    }

    public String getFileName() {
        return fileName;
    }

    public FileComposition(byte[] files, String fileName) {
        this.files = files;
        this.fileName = fileName;
    }
}
