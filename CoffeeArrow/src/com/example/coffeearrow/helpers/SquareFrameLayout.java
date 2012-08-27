/**
 * 
 */
package com.example.coffeearrow.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @author sunshi
 *
 * I hope the name of the class explains what it is.
 * It is a square FrameLayout (FrameLayout can only hold one sub view).
 * It will shrink based on the smaller one of it's width/height.
 */
public class SquareFrameLayout extends FrameLayout {
	public SquareFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
	/**
	 * This is needed to paint it's only child in itself.
	 * Frankly I am puzzled by why we need this. I would expect that the parent
	 * onLayout in FrameLayout does this.
	 */
	@Override
    protected void onLayout(boolean changed, int l, int u, int r, int d) {
        getChildAt(0).layout(0, 0, r-l, d-u); // Layout with max size
    }
    
	/**
	 * Here is the magic and it's not that magically.
	 */
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int square = Math.min(getMeasuredWidth(), getMeasuredHeight());
		setMeasuredDimension(square, square);
	}
	
}
