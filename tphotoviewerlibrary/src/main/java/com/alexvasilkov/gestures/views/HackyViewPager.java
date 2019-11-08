package com.alexvasilkov.gestures.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author sunwei
 * email：tianmu19@gmail.com
 * date：2019/11/8 22:03
 * version：1.0
 * <p>description：防止崩溃的viewpager   </p>
 */

public class HackyViewPager extends ViewPager {
    private boolean c;
    private boolean canScroll;

    public HackyViewPager(Context context) {
        this(context, null);
    }

    public HackyViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.canScroll = true;
    }

    public void setCanScroll(boolean z) {
        this.canScroll = z;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (!this.canScroll) {
            return z;
        }
        try {
            return super.onTouchEvent(motionEvent);
        } catch (IllegalArgumentException e) {
            return z;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        try {
            return super.onInterceptTouchEvent(motionEvent);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}