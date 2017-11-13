package fr.fouss.boardeo.listing;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import fr.fouss.boardeo.NewBoardActivity;
import fr.fouss.boardeo.R;

/**
 * Created by esia on 12/11/17.
 */

public class BoardRecyclerViewAdapter extends SelectableRecyclerViewAdapter<BoardData> {

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    public static interface ItemClickListener {
        public void onItemClickListener(int position);
    }

    public BoardRecyclerViewAdapter() {
        super(R.layout.board_item);

        // TODO remove
        for (int i = 0; i < 10; ++i) {
            addItem(
                    new BoardData("name"+i, "author"+i, "description"+i, "full description"+i, false)
            );
        }
    }

    @Override
    public BoardItemViewHolder createViewHolder(View view) {
        TextView textName = view.findViewById(R.id.boardNameText);
        TextView textAuthor = view.findViewById(R.id.boardAuthorText);
        TextView textShortDescription = view.findViewById(R.id.boardShortDescText);
        CheckBox checkBoxSelection = view.findViewById(R.id.checkBoxSelect);
        CheckBox checkBoxSubscription = view.findViewById(R.id.checkBoxSubscription);
        return new BoardItemViewHolder(
                view,
                checkBoxSelection,
                textName,
                textAuthor,
                textShortDescription,
                checkBoxSubscription);
    }

    /**
     * Set all selected data as subscription status
     * @param setSubscribed true if setting data as subscribed
     */
    public void setSelectionSubscription(boolean setSubscribed) {
        for (BoardData d : selectedData) {
            d.setSubscribed(setSubscribed);
        }
    }

    public void setSubscription(int position, boolean setSubscribed) {
        data.get(position).setSubscribed(setSubscribed);
    }

    public BoardData getItemById(int id) {
        for (BoardData d : data) {
            if (d.getBoardId() == id) {
                return d;
            }
        }
        return null;
    }

    public class BoardItemViewHolder extends SelectableItemViewHolder {

        private TextView nameText;
        private TextView authorText;
        private TextView shortDescriptionText;
        private CheckBox subscriptionCheckbox;

        public BoardItemViewHolder(View itemView,
                                   CheckBox selectionCheckbox,
                                   TextView nameText,
                                   TextView authorText,
                                   TextView shortDescriptionText,
                                   CheckBox subscriptionCheckbox) {
            super(itemView, selectionCheckbox);

            this.nameText = nameText;
            this.authorText = authorText;
            this.shortDescriptionText = shortDescriptionText;
            this.subscriptionCheckbox = subscriptionCheckbox;

            subscriptionCheckbox.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> onSubscriptionCheckedChange(buttonView, isChecked)
            );
            itemView.setOnClickListener(v -> onItemClick(v));
        }

        public void onSubscriptionCheckedChange(CompoundButton buttonView, boolean isChecked) {
            // update data subscription status
            setSubscription(getAdapterPosition(), isChecked);
        }

        @Override
        public void setData(BoardData d) {
            this.nameText.setText(d.getBoardName());
            this.authorText.setText(d.getBoardAuthor());
            this.shortDescriptionText.setText(d.getBoardShortDescription());

            // subscription checkbox
            subscriptionCheckbox.setChecked(d.isSubscribed());
        }

        public void onItemClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClickListener(getAdapterPosition());
            }
        }
    }
}
