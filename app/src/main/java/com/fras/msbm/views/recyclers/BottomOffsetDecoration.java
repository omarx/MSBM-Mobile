package com.fras.msbm.views.recyclers;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Shane on 7/22/2016.
 */
public class BottomOffsetDecoration extends RecyclerView.ItemDecoration {
    public static final String TAG = BottomOffsetDecoration.class.getSimpleName();

    private final int mBottomOffset;

    public BottomOffsetDecoration(int bottomOffset) {
        this.mBottomOffset = bottomOffset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final int dataSize = state.getItemCount();
        final int position = parent.getChildAdapterPosition(view);

        if (dataSize > 0 && position == dataSize - 1)
            outRect.set(0, 0, 0, mBottomOffset);
        else
            outRect.set(0, 0, 0, 0);
    }
}
