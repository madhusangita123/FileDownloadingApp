package maersk.coding.assignment.filedownloadingapp.model;

public class DownloadProgress {

    private int progress;
    private boolean isDone;
    private String filePath;

    public DownloadProgress(int progress,boolean done){
        this.progress = progress;
        this.isDone = done;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
