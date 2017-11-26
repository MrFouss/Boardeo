package fr.fouss.boardeo.listing;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import fr.fouss.boardeo.R;
import fr.fouss.boardeo.data.Board;



public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

    ///// FIELDS /////

    /**
     * Map of boards
     * A comparator can be specified to order them
     */
    private final TreeMap<Long, Board> boards = new TreeMap<>();

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
        Map.Entry<Long, Board> entry = null;
        Iterator<Map.Entry<Long, Board>> it = boards.entrySet().iterator();
        for (int i = 0; i < position; ++i) {
            entry = it.next();
        }
        if (entry != null) {
            holder.set(entry.getKey(), entry.getValue());
        }
    }

    ///// DATA MANAGEMENT /////

    @Override
    public int getItemCount() {
        return boards.size();
    }

    public void setBoard(long id) {
        Board board = null;

        // TODO retrieve board from DB

        boards.put(id, board);
        notifyDataSetChanged();
    }

    public void removeBoard(long id) {
        boards.remove(id);
        notifyDataSetChanged();
    }

    ///// VIEW HOLDER /////

    /**
     * Implement a basic view holder that requires a view with at least a checkbox to manage selection
     */
    public class BoardViewHolder extends RecyclerView.ViewHolder {

        private TextView nameLabel;
        private TextView shortDescriptionLabel;
        private long id;

        public BoardViewHolder(View itemView) {
            super(itemView);

            this.nameLabel = itemView.findViewById(R.id.board_name_label);
            this.shortDescriptionLabel = itemView.findViewById(R.id.board_short_description_label);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onBoardClicked(this.id);
                }
            });
        }

        public void set(long id, Board board) {
            this.id = id;
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
        void onBoardClicked(long id);
    }
}
