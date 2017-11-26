package fr.fouss.boardeo.listing;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import fr.fouss.boardeo.R;
import fr.fouss.boardeo.data.Board;
import fr.fouss.boardeo.utils.UserUtils;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

    ///// FIELDS /////

    /**
     * Firebase database instance
     */
    private DatabaseReference mDatabase;

    /**
     * User utility class instance
     */
    private UserUtils userUtils;

    /**
     * Map of boards
     * A comparator can be specified to order them
     */
    private final TreeMap<String, Board> boards = new TreeMap<>();

    private ChildEventListener subscriptionListener = null;
    private Map<String, ValueEventListener> boardListenerMap = new HashMap<>();

    ///// CONSTRUCTOR /////

    public BoardAdapter(Activity activity) {
        super();
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
    public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate (parse and create) view from layout file
        View newView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_list_item, parent, false);
        return new BoardViewHolder(newView);
    }

    /**
     * Called when the data displayed by holder needs to be changed
     * This is typically called when a view just has been created or is recycled
     * @param holder view holder encapsulation the view
     * @param position the position of the data to be displayed in the data list
     */
    @Override
    public void onBindViewHolder(BoardViewHolder holder, int position) {
        Map.Entry<String, Board> entry = null;
        Iterator<Map.Entry<String, Board>> it = boards.entrySet().iterator();
        for (int i = 0; i < position; ++i) {
            entry = it.next();
        }
        if (entry != null) {
            holder.set(entry.getKey(), entry.getValue());
        }
    }

    ///// DATA MANAGEMENT /////

    private void initSubscriptionsListener() {
        if (subscriptionListener == null) {
            subscriptionListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String key = dataSnapshot.getKey();
                    setBoard(key);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getKey();
                    removeBoard(key);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            };

            mDatabase.child("users").child(userUtils.getUserUid()).child("subscriptions")
                    .addChildEventListener(subscriptionListener);
        }
    }

    private void removeSubscriptionsListener() {
        if (subscriptionListener != null) {
            mDatabase.child("users").child(userUtils.getUserUid()).child("subscriptions")
                    .removeEventListener(subscriptionListener);
            subscriptionListener = null;
        }
    }

    @Override
    public int getItemCount() {
        return boards.size();
    }

    public void setBoard(String key) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Board board = (Board) dataSnapshot.getValue();
                boards.put(key, board);
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };

        removeBoardListener(key);
        boardListenerMap.put(key, listener);
        mDatabase.child("boards").child(key).addValueEventListener(listener);
    }

    private void removeBoardListener(String key) {
        if (boardListenerMap.containsKey(key)) {
            mDatabase.child("boards").child(key)
                    .removeEventListener(boardListenerMap.get(key));
            boardListenerMap.remove(key);
        }
    }

    public void removeBoard(String key) {
        removeBoardListener(key);
        boards.remove(key);
        notifyDataSetChanged();
    }

    ///// VIEW HOLDER /////

    /**
     * Implement a basic view holder that requires a view with at least a checkbox to manage selection
     */
    public class BoardViewHolder extends RecyclerView.ViewHolder {

        private TextView nameLabel;
        private TextView shortDescriptionLabel;
        private String key;

        public BoardViewHolder(View itemView) {
            super(itemView);

            this.nameLabel = itemView.findViewById(R.id.board_name_label);
            this.shortDescriptionLabel = itemView.findViewById(R.id.board_short_description_label);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onBoardClicked(this.key);
                }
            });
        }

        public void set(String key, Board board) {
            this.key = key;
            this.nameLabel.setText(board.getName());
            this.shortDescriptionLabel.setText(board.getShortDescription());
        }
    }

    ///// LISTENERS /////

    private BoardClickListener clickListener = null;

    public void setBoardClickListener(BoardClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface BoardClickListener {
        void onBoardClicked(String key);
    }
}
