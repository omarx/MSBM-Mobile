package com.fras.msbm.gallery;

/**
 * Created by Shane on 8/25/2016.
 */
public class ImageEntity {

    protected String imageTitle;
    protected String imageURL;
    protected String folderTitle;
    protected Integer isFolder;
    protected Integer folderID;

    public ImageEntity() {
    }

    public ImageEntity(String imageTitle, String imageURL, String folder, Integer isFolder) {
        this.imageTitle = imageTitle;
        this.imageURL = imageURL;
        this.folderTitle = folder;
        this.isFolder = isFolder;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getFolderTitle() {
        return folderTitle;
    }

    public Integer getIsFolder() {
        return isFolder;
    }

    public Integer getFolderID() {
        return folderID;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setIsFolder(Integer isFolder) {
        this.isFolder = isFolder;
    }

    public void setFolderTitle(String folderTitle) {
        this.folderTitle = folderTitle;
    }

    public void setFolderID(Integer folderID) {

        this.folderID = folderID;
    }
}
