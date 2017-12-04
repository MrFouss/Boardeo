package fr.fouss.boardeo.data;

public class Board {

    public final static String KEY_FIELD = "BOARD_KEY";

    private String name;
    private Long color;

    private String shortDescription;
    private String fullDescription;

    private String ownerUid;

    private Double latitude;
    private Double longitude;

    private Boolean isPublic;
    private Long lastUpdate;

    public Board() {}

    public Board(String name,
                 Long color,
                 String ownerUid,
                 Double latitude,
                 Double longitude,
                 Boolean isPublic,
                 Long lastUpdate) {

        this.name = name;
        this.color = color;
        this.shortDescription = "";
        this.fullDescription = "";
        this.ownerUid = ownerUid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isPublic = isPublic;
        this.lastUpdate = lastUpdate;
    }

    public String getName() {
        return name;
    }

    public Long getColor() {
        return color;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(Long color) {
        this.color = color;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
