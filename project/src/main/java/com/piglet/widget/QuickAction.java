package com.piglet.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.piglet.R;

/**
 * Popup window, shows action list as icon and text like the one in Gallery3D
 * app.
 * 
 */
public class QuickAction extends QuickWindow {
	private final View root;
	// private final ImageView mArrowUp;
	// private final ImageView mArrowDown;
	private final LayoutInflater inflater;
	private final Context context;

	protected static final int ANIM_GROW_FROM_LEFT = 1;
	protected static final int ANIM_GROW_FROM_RIGHT = 2;
	protected static final int ANIM_GROW_FROM_CENTER = 3;
	protected static final int ANIM_REFLECT = 4;
	protected static final int ANIM_AUTO = 5;

	private int animStyle;
	private LinearLayout mTrack;
	private ScrollView scroller;
	
	private final TextView title;

	/**
	 * Constructor
	 * 
	 * @param anchor
	 *            {@link View} on where the popup window should be displayed
	 */
	public QuickAction(View anchor) {
		super(anchor);

		context = anchor.getContext();
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		root = inflater.inflate(R.layout.popup_title, null);

		// mArrowDown = (ImageView) root.findViewById(R.id.arrow_down);
		// mArrowUp = (ImageView) root.findViewById(R.id.arrow_up);
		title = (TextView)root.findViewById(R.id.title);
		setContentView(root);

		mTrack = (LinearLayout) root.findViewById(R.id.tracks);
		scroller = (ScrollView) root.findViewById(R.id.scroller);
		animStyle = ANIM_GROW_FROM_CENTER;
	}

	public void setTitle(String title){
		this.title.setText(title);
	}
	
	public void setTitle(int titleId){
		this.title.setText(titleId);
	}
	
	/**
	 * Set animation style
	 * 
	 * @param animStyle
	 *            animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int animStyle) {
		this.animStyle = animStyle;
	}

	/**
	 * Show popup window. Popup is automatically positioned, on top or bottom of
	 * anchor view.
	 * 
	 */
	public void show() {
		preShow();

		int xPos, yPos;

		int[] location = new int[2];

		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ anchor.getWidth(), location[1] + anchor.getHeight());

		root.setLayoutParams(new ViewGroup.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight = root.getMeasuredHeight();
		int rootWidth = root.getMeasuredWidth();

		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();

		// automatically get X coord of popup (top left)
		int anchorXCenter = anchorRect.centerX();
		int rootXCenter = rootWidth / 2;
		if (anchorXCenter - rootXCenter > 0
				&& anchorXCenter + rootXCenter < screenWidth) {
			xPos = anchorXCenter - rootXCenter;
		} else if ((anchorRect.left + rootWidth) > screenWidth) {
			xPos = anchorRect.left - (rootWidth - anchor.getWidth()) - 15;
		} else {
			if (anchor.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth / 2);
			} else {
				xPos = anchorRect.left + 15;
			}
		}

		int dyTop = anchorRect.top;
		int dyBottom = screenHeight - anchorRect.bottom;

		boolean onTop = (dyTop > dyBottom) ? true : false;

		if (onTop) {
			if (rootHeight > dyTop) {
				yPos = 15;
				LayoutParams l = scroller.getLayoutParams();
				l.height = dyTop - anchor.getHeight();
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;

			if (rootHeight > dyBottom) {
				LayoutParams l = scroller.getLayoutParams();
				l.height = dyBottom;
			}
		}

		// showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up),
		// anchorRect.centerX()-xPos);

		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

		window.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos); // 这里设置了
	}

	/**
	 * Set animation style
	 * 
	 * @param screenWidth
	 *            screen width
	 * @param requestedX
	 *            distance from left edge
	 * @param onTop
	 *            flag to indicate where the popup should be displayed. Set TRUE
	 *            if displayed on top of anchor view and vice versa
	 */
	protected void setAnimationStyle(int screenWidth, int requestedX,
			boolean onTop) {
		// int arrowPos = requestedX - mArrowUp.getMeasuredWidth()/2;

		switch (animStyle) {
		case ANIM_GROW_FROM_LEFT:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
					: R.style.Animations_PopDownMenu_Left);
			break;

		case ANIM_GROW_FROM_RIGHT:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right
					: R.style.Animations_PopDownMenu_Right);
			break;

		case ANIM_GROW_FROM_CENTER:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
					: R.style.Animations_PopDownMenu_Center);
			break;

		case ANIM_REFLECT:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect
					: R.style.Animations_PopDownMenu_Reflect);
			break;

		case ANIM_AUTO:
			// if (arrowPos <= screenWidth/4) {
			// window.setAnimationStyle((onTop) ?
			// R.style.Animations_PopUpMenu_Left :
			// R.style.Animations_PopDownMenu_Left);
			// } else if (arrowPos > screenWidth/4 && arrowPos < 3 *
			// (screenWidth/4)) {
			// window.setAnimationStyle((onTop) ?
			// R.style.Animations_PopUpMenu_Center :
			// R.style.Animations_PopDownMenu_Center);
			// } else {
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right
					: R.style.Animations_PopDownMenu_Right);
			// }

			break;
		}
	}

	/**
	 * Show arrow
	 * 
	 * @param whichArrow
	 *            arrow type resource id
	 * @param requestedX
	 *            distance from left screen
	 */
	private void showArrow(int whichArrow, int requestedX) {
		// final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp :
		// mArrowDown;
		// final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown :
		// mArrowUp;

		// final int arrowWidth = mArrowUp.getMeasuredWidth();

		// showArrow.setVisibility(View.VISIBLE);
		//
		// ViewGroup.MarginLayoutParams param =
		// (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
		//
		// param.leftMargin = requestedX - arrowWidth / 2;
		//
		// hideArrow.setVisibility(View.INVISIBLE);
	}

	public void addItem(View item, LinearLayout.LayoutParams params) {
		mTrack.addView(item, params);
	}

	public void addItem(View item) {
		mTrack.addView(item);
	}

	public void onItemSelected(View selected) {
		if (mTrack != null) {
			for (int i = 0; i < mTrack.getChildCount(); i++) {
				View view = mTrack.getChildAt(i);
				if (view != null) {
					View item = view.findViewById(R.id.item);
					if (item != null && item instanceof TextView) {
						if (selected == item) {
							((TextView) item).setTextColor(item
									.getContext()
									.getResources()
									.getColorStateList(
											R.color.popup_item_selector));
						} else {
							((TextView) item).setTextColor(item
									.getContext()
									.getResources()
									.getColorStateList(
											R.color.black_to_white_return));
						}
					}
				}
			}
		}
	}
	public void onSnsItemSelected(TextView selected) {
		if (mTrack != null) {
			for (int i = 0; i < mTrack.getChildCount(); i++) {
				View view = mTrack.getChildAt(i);
				if (view != null) {
					View item = view.findViewById(R.id.item);
					if (item != null && item instanceof TextView) {
						if (selected == item) {
							((TextView) item).setTextColor(item
									.getContext()
									.getResources()
									.getColorStateList(
											R.color.pt_deep_green));
						} else {
							((TextView) item).setTextColor(item
									.getContext()
									.getResources()
									.getColorStateList(
											R.color.gray_green_text_return));
						}
					}
				}
			}
		}
	}
}