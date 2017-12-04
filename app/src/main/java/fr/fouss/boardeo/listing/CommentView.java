package fr.fouss.boardeo.listing;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.widget.AdapterView;
import android.widget.LinearLayout;

/**
 * Created by esia on 02/12/17.
 */

public class CommentView extends LinearLayout {

    private int position = -1;

    public CommentView(Context context) {
        super(context);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        ContextMenu.ContextMenuInfo info =
                new AdapterView.AdapterContextMenuInfo(this, position, getId());
        return info;
    }
}
