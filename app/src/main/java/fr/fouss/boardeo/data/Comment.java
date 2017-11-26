package fr.fouss.boardeo.data;

import android.content.Intent;

import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;

public class Comment {

    public final static String POST_KEY_FIELD = "commentPostKey";
    public final static String AUTHOR_UID_FIELD = "commentAuthorUid";
    public final static String CONTENT_FIELD = "commentContent";
    public final static String TIMESTAMP_FIELD = "commentTimestamp";

    private String postKey;
    private String authorUid;
    private String content;
    private Long timestamp;

    public Comment() {}

    public Comment(String postKey,
            String authorUid,
            String content,
            Long timestamp) {

        this.postKey = postKey;
        this.authorUid = authorUid;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(POST_KEY_FIELD, getPostKey());
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
        this.postKey = intent.getStringExtra(POST_KEY_FIELD);
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
        intent.putExtra(POST_KEY_FIELD, getPostKey());
        intent.putExtra(AUTHOR_UID_FIELD, getAuthorUid());
        intent.putExtra(CONTENT_FIELD, getContent());
        intent.putExtra(TIMESTAMP_FIELD, getTimestamp());

        return intent;
    }

    public String getPostKey() {
        return postKey;
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
}
