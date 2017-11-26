package fr.fouss.boardeo.data;

import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class Board {

    public final static String NAME_FIELD = "boardName";
    public final static String SHORT_DESCRIPTION_FIELD = "boardShortDescription";
    public final static String FULL_DESCRIPTION_FIELD = "boardFullDescription";
    public final static String OWNER_UID_FIELD = "boardOwnerUid";
    public final static String LATITUDE_FIELD = "boardLatitude";
    public final static String LONGITUDE_FIELD = "boardLongitude";
    public final static String IS_EDITABLE_FIELD = "boardIsEditable";

    private String name;

    private String shortDescription;
    private String fullDescription;

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
        this.fullDescription = "";
        this.ownerUid = ownerUid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isEditable = isEditable;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(NAME_FIELD, getName());
        result.put(SHORT_DESCRIPTION_FIELD, getShortDescription());
        result.put(FULL_DESCRIPTION_FIELD, getFullDescription());
        result.put(OWNER_UID_FIELD, getOwnerUid());
        result.put(LATITUDE_FIELD, getLatitude());
        result.put(LONGITUDE_FIELD, getLongitude());
        result.put(IS_EDITABLE_FIELD, getEditable());

        return result;
    }

    /**
     * Set data from intent
     *
     * @param intent the intent containing the board's info
     */
    public void setFromIntent(Intent intent) {
        this.name = intent.getStringExtra(NAME_FIELD);
        this.shortDescription = intent.getStringExtra(SHORT_DESCRIPTION_FIELD);
        this.fullDescription = intent.getStringExtra(FULL_DESCRIPTION_FIELD);
        this.ownerUid = intent.getStringExtra(OWNER_UID_FIELD);
        this.latitude = intent.getDoubleExtra(LATITUDE_FIELD, 0.0);
        this.longitude = intent.getDoubleExtra(LONGITUDE_FIELD, 0.0);
        this.isEditable = intent.getBooleanExtra(IS_EDITABLE_FIELD, false);
    }

    /**
     * Translate to an intent
     *
     * @param intent the intent to fill
     * @return the same intent, but filled with the board's info
     */
    public Intent fillIntentExtras(Intent intent) {
        intent.putExtra(NAME_FIELD, getName());
        intent.putExtra(SHORT_DESCRIPTION_FIELD, getShortDescription());
        intent.putExtra(FULL_DESCRIPTION_FIELD, getFullDescription());
        intent.putExtra(OWNER_UID_FIELD, getOwnerUid());
        intent.putExtra(LATITUDE_FIELD, getLatitude());
        intent.putExtra(LONGITUDE_FIELD, getLongitude());
        intent.putExtra(IS_EDITABLE_FIELD, getEditable());

        return intent;
    }

    public String getName() {
        return name;
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

    public Boolean getEditable() {
        return isEditable;
    }
}
