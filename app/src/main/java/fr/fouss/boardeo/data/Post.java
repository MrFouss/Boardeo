package fr.fouss.boardeo.data;

import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class Post {

    public final static String BOARD_KEY_FIELD = "postBoardKey";
    public final static String AUTHOR_UID_FIELD = "postAuthorUid";
    public final static String CONTENT_FIELD = "postContent";
    public final static String TIMESTAMP_FIELD = "postTimestamp";

    private String boardKey;
    private String authorUid;
    private String content;
    private Long timestamp;

    public Post() {}

    public Post(String boardKey,
            String authorUid,
            String content,
            Long timestamp) {

        this.boardKey = boardKey;
        this.authorUid = authorUid;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(BOARD_KEY_FIELD, getBoardKey());
        result.put(AUTHOR_UID_FIELD, getAuthorUid());
        result.put(CONTENT_FIELD, getContent());
        result.put(TIMESTAMP_FIELD, getTimestamp());

        return result;
    }

    /**
     * Set data from intent
     *
     * @param intent the intent containing the comment's info
     */
    public void setFromIntent(Intent intent) {
        this.boardKey = intent.getStringExtra(BOARD_KEY_FIELD);
        this.authorUid = intent.getStringExtra(AUTHOR_UID_FIELD);
        this.content = intent.getStringExtra(CONTENT_FIELD);
        this.timestamp = intent.getLongExtra(TIMESTAMP_FIELD, 0);
    }

    /**
     * Translate to an intent
     *
     * @param intent the intent to fill
     * @return the same intent, but filled with the board's info
     */
    public Intent fillIntentExtras(Intent intent) {
        intent.putExtra(BOARD_KEY_FIELD, getBoardKey());
        intent.putExtra(AUTHOR_UID_FIELD, getAuthorUid());
        intent.putExtra(CONTENT_FIELD, getContent());
        intent.putExtra(TIMESTAMP_FIELD, getTimestamp());

        return intent;
    }

    public String getBoardKey() {
        return boardKey;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public String getContent() {
        return content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setBoardKey(String boardKey) {
        this.boardKey = boardKey;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
