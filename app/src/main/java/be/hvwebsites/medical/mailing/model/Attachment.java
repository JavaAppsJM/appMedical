package be.hvwebsites.medical.mailing.model;

public class Attachment {
    public String path;
    public String filename;

    public Attachment(String path, String filename) {
        this.path = path;
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
