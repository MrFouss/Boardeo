package fr.fouss.boardeo.listing;

import android.content.Intent;

public class BoardData {
    public final static String BOARD_ID_FIELD = "boardId";
    public final static String BOARD_NAME_FIELD = "boardName";
    public final static String BOARD_AUTHOR_FIELD = "boardAuthor";
    public final static String BOARD_SHORT_DESCRIPTION_FIELD = "boardShortDescription";
    public final static String BOARD_FULL_DESCRIPTION_FIELD = "boardFullDescription";
    public final static String BOARD_SUBSCRIPTION_FIELD = "boardSubscription";

    // TODO remove
    public static int idIncrement = 0;

    private int boardId;
    private String boardName;
    private String boardAuthor;
    private String boardShortDescription;
    private String boardFullDescription;
    private boolean subscribed;

    public BoardData(String boardName, String boardAuthor, String boardShortDescription, String boardFullDescription, boolean subscribed) {
        this.boardId = idIncrement++;
        this.boardName = boardName;
        this.boardAuthor = boardAuthor;
        this.boardShortDescription = boardShortDescription;
        this.boardFullDescription = boardFullDescription;
        this.subscribed = subscribed;
    }

    /**
     * Create a new BoardData from an intent
     * @param intent
     */
    public BoardData(Intent intent) {
        this.boardId = idIncrement++;
        this.boardName = intent.getStringExtra(BOARD_NAME_FIELD);
        this.boardAuthor = intent.getStringExtra(BOARD_AUTHOR_FIELD);
        this.boardShortDescription = intent.getStringExtra(BOARD_SHORT_DESCRIPTION_FIELD);
        this.boardFullDescription = intent.getStringExtra(BOARD_FULL_DESCRIPTION_FIELD);
        this.subscribed = intent.getBooleanExtra(BOARD_SUBSCRIPTION_FIELD, false);
    }

    /**
     * Set data from intent
     * @param intent
     */
    public void setFromIntent(Intent intent) {
        this.boardName = intent.getStringExtra(BOARD_NAME_FIELD);
        this.boardAuthor = intent.getStringExtra(BOARD_AUTHOR_FIELD);
        this.boardShortDescription = intent.getStringExtra(BOARD_SHORT_DESCRIPTION_FIELD);
        this.boardFullDescription = intent.getStringExtra(BOARD_FULL_DESCRIPTION_FIELD);
        this.subscribed = intent.getBooleanExtra(BOARD_SUBSCRIPTION_FIELD, isSubscribed());
    }

    /**
     * Translate to an intent
     * @param intent
     * @return
     */
    public Intent fillIntentExtras(Intent intent) {
        intent.putExtra(BoardData.BOARD_ID_FIELD, getBoardId());
        intent.putExtra(BoardData.BOARD_NAME_FIELD, getBoardName());
        intent.putExtra(BoardData.BOARD_AUTHOR_FIELD, getBoardAuthor());
        intent.putExtra(BoardData.BOARD_SHORT_DESCRIPTION_FIELD, getBoardShortDescription());
        intent.putExtra(BoardData.BOARD_FULL_DESCRIPTION_FIELD, getBoardFullDescription());
        intent.putExtra(BoardData.BOARD_SUBSCRIPTION_FIELD, isSubscribed());

        return intent;
    }

    public String getBoardName() {
        return boardName;
    }

    public String getBoardAuthor() {
        return boardAuthor;
    }

    public String getBoardShortDescription() {
        return boardShortDescription;
    }

    public String getBoardFullDescription() {
        return boardFullDescription;
    }

    public int getBoardId() {
        return boardId;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
            this.subscribed = subscribed;
        }
}
