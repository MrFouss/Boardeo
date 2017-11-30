package fr.fouss.boardeo.data;

import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class Post {
    public final static String KEY_FIELD = "POST_KEY";
    public final static String TITLE_FIELD = "postTitle";
    public final static String CONTENT_FIELD = "postContent";
    public final static String TIMESTAMP_FIELD = "postTimestamp";
    public final static String AUTHOR_UID_FIELD = "postAuthorUid";
    public final static String BOARD_KEY_FIELD = "postBoardKey";

    private String title;
    private String content;
    private Long timestamp;
    private String authorUid;
    private String boardKey;

    public Post() {}

    public Post(String title,
                String content,
                Long timestamp,
                String authorUid,
                String boardKey) {

        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.authorUid = authorUid;
        this.boardKey = boardKey;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(TITLE_FIELD, getTitle());
        result.put(CONTENT_FIELD, getContent());
        result.put(AUTHOR_UID_FIELD, getAuthorUid());
        result.put(TIMESTAMP_FIELD, getTimestamp());
        result.put(BOARD_KEY_FIELD, getBoardKey());

        return result;
    }

    /**
     * Set data from intent
     *
     * @param intent the intent containing the comment's info
     */
    public void setFromIntent(Intent intent) {
        this.title = intent.getStringExtra(TITLE_FIELD);
        this.content = intent.getStringExtra(CONTENT_FIELD);
        this.timestamp = intent.getLongExtra(TIMESTAMP_FIELD, 0);
        this.authorUid = intent.getStringExtra(AUTHOR_UID_FIELD);
        this.boardKey = intent.getStringExtra(BOARD_KEY_FIELD);
    }

    /**
     * Translate to an intent
     *
     * @param intent the intent to fill
     * @return the same intent, but filled with the board's info
     */
    public Intent fillIntentExtras(Intent intent) {
        intent.putExtra(TITLE_FIELD, getTitle());
        intent.putExtra(CONTENT_FIELD, getContent());
        intent.putExtra(TIMESTAMP_FIELD, getTimestamp());
        intent.putExtra(AUTHOR_UID_FIELD, getAuthorUid());
        intent.putExtra(BOARD_KEY_FIELD, getBoardKey());

        return intent;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public String getBoardKey() {
        return boardKey;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public void setBoardKey(String boardKey) {
        this.boardKey = boardKey;
    }
}
