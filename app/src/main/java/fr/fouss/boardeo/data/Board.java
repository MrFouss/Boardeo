package fr.fouss.boardeo.data;

public class Board {

    private String name;

    private String shortDescription;
    private String longDescription;

    private String ownerUid;

    private Double latitude;
    private Double longitude;

    private Boolean isEditable;

    public Board() {}

    public Board(String name,
                 String ownerUid,
                 Double latitude,
                 Double longitude,
                 Boolean isEditable) {

        this.name = name;
        this.shortDescription = "";
        this.longDescription = "";
        this.ownerUid = ownerUid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isEditable = isEditable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getEditable() {
        return isEditable;
    }

    public void setEditable(Boolean editable) {
        isEditable = editable;
    }
}
