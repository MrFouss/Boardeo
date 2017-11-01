package fr.fouss.boardeo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BoardItemAdapter extends RecyclerView.Adapter<BoardItemAdapter.BoardItemViewHolder> {

    // TODO fetch firebase data
    private BoardItemData[] itemData = {
            new BoardItemData("name1", "author1", "description1")
    };

    /**
     * Created by esia on 31/10/17.
     */

    public class BoardItemData {
        private String boardName;
        private String boardAuthor;
        private String boardShortDesc;

        public BoardItemData(String boardName, String boardAuthor, String boardShortDesc) {
            this.boardName = boardName;
            this.boardAuthor = boardAuthor;
            this.boardShortDesc = boardShortDesc;
        }

        public String getBoardName() {
            return boardName;
        }

        public String getBoardAuthor() {
            return boardAuthor;
        }

        public String getBoardShortDesc() {
            return boardShortDesc;
        }
    }

    public class BoardItemViewHolder extends RecyclerView.ViewHolder {

        private TextView boardNameText;
        private TextView boardAuthorText;
        private TextView boardShortDescText;

        public BoardItemViewHolder(View itemView) {
            super(itemView);
            boardNameText = itemView.findViewById(R.id.boardNameText);
            boardAuthorText = itemView.findViewById(R.id.boardAuthorText);
            boardShortDescText = itemView.findViewById(R.id.boardShortDescText);
        }

        public void set(BoardItemData data) {
            this.boardNameText.setText(data.getBoardName());
            this.boardAuthorText.setText(data.getBoardAuthor());
            this.boardShortDescText.setText(data.getBoardShortDesc());
        }
    }

    @Override
    public BoardItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View newView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_item, parent, false);
        BoardItemViewHolder newViewHolder = new BoardItemViewHolder(newView);
        return newViewHolder;
    }

    @Override
    public void onBindViewHolder(BoardItemViewHolder holder, int position) {
        BoardItemData data = itemData[position];
        holder.set(data);
    }

    @Override
    public int getItemCount() {
        return itemData.length;
    }
}
