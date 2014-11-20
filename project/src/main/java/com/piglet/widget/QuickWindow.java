package com.piglet.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.piglet.R;

public class QuickWindow {
	protected final View anchor;
	protected final PopupWindow window;
	private View root;
	private Drawable background = null;
	protected final WindowManager windowManager;

	private int animStyle = ANIM_AUTO;
	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_REFLECT = 4;
	public static final int ANIM_AUTO = 5;

	/**
	 * Create a QuickAction
	 * 
	 * @param anchor
	 *            the view that the QuickAction will be displaying 'from'
	 */
	public QuickWindow(View anchor) {
		this.anchor = anchor;
		this.window = new PopupWindow(anchor.getContext());

		// when a touch even happens outside of the window
		// make the window go away
		window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					QuickWindow.this.window.dismiss();

					return true;
				}

				return false;
			}
		});

		windowManager = (WindowManager) anchor.getContext().getSystemService(
				Context.WINDOW_SERVICE);

		onCreate();
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
	 * Anything you want to have happen when created. Probably should create a
	 * view and setup the event listeners on child views.
	 */
	protected void onCreate() {
	}

	/**
	 * In case there is stuff to do right before displaying.
	 */
	protected void onShow() {
	}

	protected void preShow() {
		if (root == null) {
			throw new IllegalStateException(
					"setContentView was not called with a view to display.");
		}

		onShow();

		if (background == null) {
			window.setBackgroundDrawable(new BitmapDrawable());
		} else {
			window.setBackgroundDrawable(background);
		}

		// if using PopupWindow#setBackgroundDrawable this is the only values of
		// the width and hight that make it work
		// otherwise you need to set the background of the root viewgroup
		// and set the popupwindow background to an empty BitmapDrawable

		window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		window.setTouchable(true);
		window.setFocusable(true);
		window.setOutsideTouchable(true);

		window.setContentView(root);
	}

	public void setBackgroundDrawable(Drawable background) {
		this.background = background;
	}

	/**
	 * Sets the content view. Probably should be called from {@link onCreate}
	 * 
	 * @param root
	 *            the view the popup will display
	 */
	public void setContentView(View root) {
		this.root = root;

		window.setContentView(root);
	}

	/**
	 * Will inflate and set the view from a resource id
	 * 
	 * @param layoutResID
	 */
	public void setContentView(int layoutResID) {
		LayoutInflater inflator = (LayoutInflater) anchor.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		setContentView(inflator.inflate(layoutResID, null));
	}

	/**
	 * If you want to do anything when {@link dismiss} is called
	 * 
	 * @param listener
	 */
	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		window.setOnDismissListener(listener);
	}

	/**
	 * Displays like a popdown menu from the anchor view
	 */
	public void showDropDown() {
		showDropDown(0, 0);
	}

	/**
	 * Displays like a popdown menu from the anchor view.
	 * 
	 * @param xOffset
	 *            offset in X direction
	 * @param yOffset
	 *            offset in Y direction
	 */
	public void showDropDown(int xOffset, int yOffset) {
		preShow();

		window.setAnimationStyle(R.style.Animations_PopDownMenu);

		window.showAsDropDown(anchor, xOffset, yOffset);
	}

	/**
	 * Displays like a QuickAction from the anchor view.
	 */
	public void showLikeQuickAction() {
		showLikeQuickAction(0, 0);
	}

	/**
	 * Displays like a QuickAction from the anchor view.
	 * 
	 * @param xOffset
	 *            offset in the X direction
	 * @param yOffset
	 *            offset in the Y direction
	 */
	public void showLikeQuickAction(int xOffset, int yOffset) {
		preShow();

		window.setAnimationStyle(R.style.Animations_PopUpMenu_Center);

		int[] location = new int[2];
		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ anchor.getWidth(), location[1] + anchor.getHeight());

		root.setLayoutParams(new ViewGroup.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootWidth = root.getMeasuredWidth();
		int rootHeight = root.getMeasuredHeight();

		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		// int screenHeight = windowManager.getDefaultDisplay().getHeight();

		int xPos = ((screenWidth - rootWidth) / 2) + xOffset;
		int yPos = anchorRect.top - rootHeight + yOffset;

		// display on bottom
		if (rootHeight > anchorRect.top) {
			yPos = anchorRect.bottom + yOffset;

			window.setAnimationStyle(R.style.Animations_PopDownMenu_Center);
		}

		window.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	public void dismiss() {
		window.dismiss();
	}

	public void showCovered() {
		preShow();
		int[] location = new int[2];

		anchor.getLocationInWindow(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ anchor.getWidth(), location[1] + anchor.getHeight());

		root.setLayoutParams(new ViewGroup.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();

		int dyTop = anchorRect.top;
		int dyBottom = screenHeight - anchorRect.bottom;

		boolean onTop = (dyTop > dyBottom) ? true : false;

		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

		window.showAtLocation(anchor, Gravity.NO_GRAVITY, anchorRect.left,
				anchorRect.top);
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
		int arrowPos = requestedX - 5;// mArrowUp.getMeasuredWidth()/2;

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
			if (arrowPos <= screenWidth / 4) {
				window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
						: R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth / 4
					&& arrowPos < 3 * (screenWidth / 4)) {
				window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
						: R.style.Animations_PopDownMenu_Center);
			} else {
				window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right
						: R.style.Animations_PopDownMenu_Right);
			}

			break;
		}
	}

	public boolean isShowing() {
		return window == null ? false : window.isShowing();
	}

}