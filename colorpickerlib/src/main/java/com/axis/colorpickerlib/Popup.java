package com.axis.colorpickerlib;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by DDA_Admin on 5/5/16.
 */
public class Popup {

    protected Context mContext;
    protected PopupWindow mWindow;
    protected View mRootView;
    protected Drawable mBackground = null;
    protected WindowManager mWindowManager;

    public Popup(Context context, final boolean canDissmiss) {
        mContext = context;
        mWindow = new PopupWindow(context);

        mWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    if (canDissmiss) {

                        mWindow.dismiss();
                        System.out.println("this is dismiss");
                    }
                    return true;
                }
                return false;
            }
        });

        mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
    }

    protected void onDismiss() {

    }

    protected void onShow() {

    }

    protected void preShow() {
        if (mRootView == null)
            throw new IllegalStateException(
                    "setContentView was not called with a view to display.");

        onShow();

        if (mBackground == null)
            mWindow.setBackgroundDrawable(new BitmapDrawable());
        else
            mWindow.setBackgroundDrawable(mBackground);

        mWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setTouchable(true);
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);
        mWindow.setContentView(mRootView);

    }

    public void setContentView(View root) {
        mRootView = root;
        mWindow.setContentView(root);
    }

    public void setBackgroundDrawable(Drawable background) {
        mBackground = background;
    }

    public void setContentView(int layoutResID) {
        LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setContentView(inflator.inflate(layoutResID, null));
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        mWindow.setOnDismissListener(listener);
    }

    public void dismiss() {
        mWindow.dismiss();
    }

}
