package maersk.coding.assignment.filedownloadingapp.model;

public class DownLoadedItem {
    private String fileUrl;
    private String filePathOnDisk;
    private String fileName;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFilePathOnDisk() {
        return filePathOnDisk;
    }

    public void setFilePathOnDisk(String filePathOnDisk) {
        this.filePathOnDisk = filePathOnDisk;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
