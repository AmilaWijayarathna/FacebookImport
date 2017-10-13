package com.axis.colorpickerlib;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by DDA_Admin on 5/5/16.
 */
public class ColorPickerPopup extends Popup implements PopupWindow.OnDismissListener {

    private ImageView mArrowUp;
    private ImageView mArrowDown;
    private ImageView mArrowLeft;
    private ImageView mArrowRight;
    protected LinearLayout rootLayout;

    private LayoutInflater inflater;
    private OnDismissListener mDismissListener;

    private boolean mDidAction;
    private boolean mAnimateTrack;

    private int mChildPos;
    private int mAnimStyle;

    public static final int ANIM_GROW_FROM_LEFT = 1;
    public static final int ANIM_GROW_FROM_RIGHT = 2;
    public static final int ANIM_GROW_FROM_CENTER = 3;
    public static final int ANIM_AUTO = 4;
    private Animation mTrackAnim;

    public ColorPickerPopup(Context context, boolean canDismiss) {
        super(context, canDismiss);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mTrackAnim = AnimationUtils.loadAnimation(context, R.anim.rail);

        mTrackAnim.setInterpolator(new Interpolator() {
            public float getInterpolation(float t) {
                // Pushes past the target area, then snaps back into place.
                // Equation for graphing: 1.2-((x*1.6)-1.1)^2
                final float inner = (t * 1.55f) - 1.1f;

                return 1.2f - inner * inner;
            }
        });


        mAnimStyle		= ANIM_AUTO;
        mAnimateTrack	= true;
        mChildPos		= 0;

        setRootViewId(R.layout.color_popup);
    }

    public void setRootViewId(int colorPopup) {
        mRootView = (ViewGroup) inflater.inflate(colorPopup, null);



        // mtrack
        mArrowDown = (ImageView) mRootView.findViewById(R.id.arrow_down);
        mArrowUp = (ImageView) mRootView.findViewById(R.id.arrow_up);
        mArrowLeft = (ImageView) mRootView.findViewById(R.id.arrow_left);
        mArrowRight = (ImageView) mRootView.findViewById(R.id.arrow_right);
        rootLayout = (LinearLayout) mRootView.findViewById(R.id.content);

        mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(mRootView);
    }

    private int getNavBarHeight() {
        Resources resources = mContext.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }


    public void show(View anchor, AlignMode alignMode) {
        preShow();

        int[] location = new int[2];

        // getting clicked view params
        anchor.getLocationOnScreen(location);
        Rect anchorRect = new Rect(location[0], location[1], location[0]
                + anchor.getWidth(), location[1] + anchor.getHeight());

        mRootView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int rootWidth = mRootView.getMeasuredWidth();
        int rootHeight = mRootView.getMeasuredHeight();
        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

        int xPos = 0;
        int yPos = 0;
        int arrowMargin = 0;

        int anchorMidX = location[0] + anchor.getMeasuredWidth() / 2;
        int anchorMidY = location[1] + anchor.getMeasuredHeight() / 2;

        boolean onTop		= true;

        // display on bottom

        AlignMode mode = alignMode;
        if (mode == AlignMode.TOP) {
            xPos = anchorMidX - rootWidth / 2;
            yPos = anchor.getBottom();
            if (rootWidth / 2 > anchorMidX) {
                arrowMargin = anchorMidX - mArrowUp.getMeasuredWidth() / 2
                        - mArrowLeft.getMeasuredWidth();
            } else if (rootWidth / 2 > (screenWidth - anchorMidX)) {
                arrowMargin = (rootWidth - (screenWidth - anchorMidX))
                        - mArrowUp.getMeasuredWidth() / 2
                        - mArrowLeft.getMeasuredWidth();
            } else {
                arrowMargin = rootWidth / 2 - mArrowUp.getMeasuredWidth() / 2
                        - mArrowLeft.getMeasuredWidth();
            }
            showArrow(R.id.arrow_up, arrowMargin);
            setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
        } else if (mode == AlignMode.BOTTOM) {

            xPos = anchorMidX - rootWidth / 2;
            yPos = location[1] - rootHeight;
            if (rootWidth / 2 > anchorMidX) {
                arrowMargin = anchorMidX - mArrowDown.getMeasuredWidth() / 2
                        - mArrowLeft.getMeasuredWidth();
            } else if (rootWidth / 2 > (screenWidth - anchorMidX)) {
                arrowMargin = (rootWidth - (screenWidth - anchorMidX))
                        - mArrowDown.getMeasuredWidth() / 2
                        - mArrowLeft.getMeasuredWidth();
            } else {
                arrowMargin = rootWidth / 2 - mArrowDown.getMeasuredWidth() / 2
                        - mArrowLeft.getMeasuredWidth();
            }
            showArrow(R.id.arrow_down, arrowMargin);
            mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
                    : R.style.Animations_PopDownMenu_Center);

        } else if (mode == AlignMode.LEFT) {
            // ok finish
            xPos = anchor.getRight();
            yPos = anchorMidY - rootHeight / 2;
            if (rootHeight / 2 > anchorMidY) {
                arrowMargin = anchorMidY - mArrowLeft.getMeasuredHeight() / 2;
            } else if (rootHeight / 2 > (screenHeight - anchorMidY)) {
                arrowMargin = (rootHeight - (screenHeight - anchorMidY))
                        - mArrowLeft.getMeasuredHeight() / 2;
            } else {
                arrowMargin = rootHeight / 2 - mArrowRight.getMeasuredHeight()
                        / 2;
            }
            showArrow(R.id.arrow_left, arrowMargin);
            mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
                    : R.style.Animations_PopDownMenu_Left);
        } else if (mode == AlignMode.RIGHT) {
            xPos = screenWidth - rootWidth - anchor.getWidth() - anchor.getLeft();
            yPos = anchorMidY - rootHeight / 2;
            if (rootHeight / 2 > anchorMidY) {
                arrowMargin = anchorMidY - mArrowRight.getMeasuredHeight() / 2;
            } else if (rootHeight / 2 > (screenHeight - anchorMidY)) {
                arrowMargin = (rootHeight - (screenHeight - anchorMidY))
                        - mArrowRight.getMeasuredHeight() / 2;
            } else {
                arrowMargin = rootHeight / 2 - mArrowRight.getMeasuredHeight()
                        / 2;
            }
            showArrow(R.id.arrow_right, arrowMargin);
            setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
        } else {

        }

        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);

    }

    private void showArrow(int whichArrow, int requestedX) {
        ViewGroup.MarginLayoutParams param = null;
        if (whichArrow == R.id.arrow_up) {
            param = (ViewGroup.MarginLayoutParams) mArrowUp.getLayoutParams();
            param.leftMargin = requestedX;
            mArrowLeft.setVisibility(View.INVISIBLE);
            mArrowUp.setVisibility(View.VISIBLE);
            mArrowDown.setVisibility(View.INVISIBLE);
            mArrowRight.setVisibility(View.INVISIBLE);

        } else if (whichArrow == R.id.arrow_down) {
            param = (ViewGroup.MarginLayoutParams) mArrowDown.getLayoutParams();
            param.leftMargin = requestedX;
            mArrowLeft.setVisibility(View.INVISIBLE);
            mArrowUp.setVisibility(View.INVISIBLE);
            mArrowDown.setVisibility(View.VISIBLE);
            mArrowRight.setVisibility(View.INVISIBLE);

        } else if (whichArrow == R.id.arrow_left) {
            param = (ViewGroup.MarginLayoutParams) mArrowLeft.getLayoutParams();
            param.topMargin = requestedX;
            mArrowLeft.setVisibility(View.VISIBLE);
            mArrowUp.setVisibility(View.INVISIBLE);
            mArrowDown.setVisibility(View.INVISIBLE);
            mArrowRight.setVisibility(View.INVISIBLE);

        } else if (whichArrow == R.id.arrow_right) {
            param = (ViewGroup.MarginLayoutParams) mArrowRight.getLayoutParams();
            param.topMargin = requestedX;
            mArrowLeft.setVisibility(View.INVISIBLE);
            mArrowUp.setVisibility(View.INVISIBLE);
            mArrowDown.setVisibility(View.INVISIBLE);
            mArrowRight.setVisibility(View.VISIBLE);

        } else {
        }
    }

    private void setAnimationStyle(int screenWidth, int requestedX,
                                   boolean onTop) {
        int arrowPos = requestedX - mArrowUp.getMeasuredWidth() / 2;

        switch (mAnimStyle) {
            case ANIM_GROW_FROM_LEFT:
                mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
                        : R.style.Animations_PopDownMenu_Left);
                break;

            case ANIM_GROW_FROM_RIGHT:
                mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right
                        : R.style.Animations_PopDownMenu_Right);
                break;

            case ANIM_GROW_FROM_CENTER:
                mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
                        : R.style.Animations_PopDownMenu_Center);
                break;

            case ANIM_AUTO:
                if (arrowPos <= screenWidth / 4) {
                    mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
                            : R.style.Animations_PopDownMenu_Left);
                } else if (arrowPos > screenWidth / 4
                        && arrowPos < 3 * (screenWidth / 4)) {
                    mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
                            : R.style.Animations_PopDownMenu_Center);
                } else {
                    mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopDownMenu_Right
                            : R.style.Animations_PopDownMenu_Right);
                }

                break;
        }
    }

    public void setAnimStyle(int mAnimStyle) {
        this.mAnimStyle = mAnimStyle;
    }

    public void mAnimateTrack(boolean mAnimateTrack) {
        this.mAnimateTrack = mAnimateTrack;
    }

    public void setOnDismissListener(ColorPickerPopup.OnDismissListener listener) {
        setOnDismissListener(this);

        mDismissListener = listener;
    }

    @Override
    public void onDismiss() {
    }

    public interface OnDismissListener {
        public abstract void onDismiss();
    }

    public enum AlignMode {
        TOP, BOTTOM, LEFT, RIGHT;
    }

    protected void addView(View view) {
        rootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

}
