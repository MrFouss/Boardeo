package fr.fouss.boardeo.listing;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import fr.fouss.boardeo.R;
import fr.fouss.boardeo.data.Comment;
import fr.fouss.boardeo.utils.UserUtils;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    ///// FIELDS /////

    private Activity parentActivity;

    /**
     * Firebase database instance
     */
    private DatabaseReference mDatabase;

    /**
     * User utility class instance
     */
    private UserUtils userUtils;

    /**
     * Map of comments
     * A comparator can be specified to order them
     */
    private final TreeMap<String, Comment> comments = new TreeMap<>();

    private ChildEventListener commentListListener = null;
    private Map<String, ValueEventListener> commentListenerMap = new HashMap<>();

    ///// CONSTRUCTOR /////

    public CommentAdapter(Activity activity) {
        super();
        this.parentActivity = activity;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userUtils = new UserUtils(activity);
    }

    ///// NECESSARY IMPLEMENTATIONS /////

    /**
     * Called when the instantiation of a new view holder is necessary
     * @param parent parent view (the recycler view)
     * @param viewType the type of the view to be created (not used, default to 0)
     * @return the view holder encapsulating the newly created view
     */
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate (parse and create) view from layout file
//        View newView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.comment_list_item, parent, false);

        View newView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_list_item, parent, false);
        parentActivity.registerForContextMenu(newView.findViewById(R.id.comment_view));
        // TODO create comment_list_item
        return new CommentViewHolder(newView);
    }

    /**
     * Called when the data displayed by holder needs to be changed
     * This is typically called when a view just has been created or is recycled
     * @param holder view holder encapsulation the view
     * @param position the position of the data to be displayed in the data list
     */
    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Map.Entry<String, Comment> entry = null;
        Iterator<Map.Entry<String, Comment>> it = comments.entrySet().iterator();
        for (int i = 0; i <= position; ++i) {
            entry = it.next();
        }
        if (entry != null) {
            holder.set(position, entry.getKey(), entry.getValue());
        }
    }

    public Comment getComment(int position) {
        Map.Entry<String, Comment> entry = null;
        Iterator<Map.Entry<String, Comment>> it = comments.entrySet().iterator();
        for (int i = 0; i <= position; ++i) {
            entry = it.next();
        }
        if (entry != null) {
            return entry.getValue();
        }
        return null;
    }

    public String getKey(int position) {
        Map.Entry<String, Comment> entry = null;
        Iterator<Map.Entry<String, Comment>> it = comments.entrySet().iterator();
        for (int i = 0; i <= position; ++i) {
            entry = it.next();
        }
        if (entry != null) {
            return entry.getKey();
        }
        return null;
    }

    ///// DATA MANAGEMENT /////

    ///// DATA MANAGEMENT /////

    public void initCommentListListener(String postKey) {
        if (commentListListener == null) {
            commentListListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String key = dataSnapshot.getKey();
                    setComment(key);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getKey();
                    removeComment(key);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            };

            mDatabase.child("posts").child(postKey).child("comments")
                    .addChildEventListener(commentListListener);
        }
    }

    private void removeCommentListListener(String postKey) {
        if (commentListListener != null) {
            mDatabase.child("posts").child(postKey).child("comments")
                    .removeEventListener(commentListListener);
            commentListListener = null;
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setComment(String commentKey) {
        if (!commentListenerMap.containsKey(commentKey)) {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String commentKey = dataSnapshot.getKey();
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    comments.put(commentKey, comment);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            };

            commentListenerMap.put(commentKey, listener);
            mDatabase.child("comments").child(commentKey).addValueEventListener(listener);
        }
    }

    private void removeCommentListener(String commentKey) {
        if (commentListenerMap.containsKey(commentKey)) {
            mDatabase.child("comments").child(commentKey)
                    .removeEventListener(commentListenerMap.get(commentKey));
            commentListenerMap.remove(commentKey);
        }
    }

    public void removeComment(String commentKey) {
        removeCommentListener(commentKey);
        comments.remove(commentKey);
        notifyDataSetChanged();
    }

    ///// VIEW HOLDER /////

    /**
     * Implement a basic view holder that requires a view with at least a checkbox to manage selection
     */
    public class CommentViewHolder extends RecyclerView.ViewHolder {

        private CommentView commentView;
        private TextView titleLabel;
        private TextView contentLabel;
        private TextView dateLabel;
        private TextView authorLabel;
        private String key;

        public CommentViewHolder(View itemView) {
            super(itemView);

            this.contentLabel = itemView.findViewById(R.id.comment_content_label);
            this.dateLabel = itemView.findViewById(R.id.comment_date_label);
            this.authorLabel = itemView.findViewById(R.id.comment_author_label);
            this.commentView = itemView.findViewById(R.id.comment_view);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onCommentClicked(this.key);
                }
            });
        }

        public void set(int position, String key, Comment comment) {
            this.commentView.setPosition(position);
            this.key = key;
            this.contentLabel.setText(comment.getContent());
            this.dateLabel.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(comment.getTimestamp())));
            this.authorLabel.setText(comment.getAuthorUid()); // TODO replace with the right author's name
        }

        public String getKey() {
            return key;
        }
    }

    ///// LISTENERS /////

    private CommentClickListener clickListener = null;

    public void setCommentClickListener(CommentClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface CommentClickListener {
        void onCommentClicked(String key);
    }
}
