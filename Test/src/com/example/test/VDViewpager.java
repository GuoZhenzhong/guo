package com.example.test;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Scroller;

public class VDViewpager extends ViewGroup {

	/* 滚动实例 */
	private Scroller mScroller;

	/* 是否可滑�?*/
	private boolean canScroller=false;

	public boolean isCanScroller() {
		return canScroller;
	}

	public void setCanScroller(boolean canScroller) {
		this.canScroller = canScroller;
	}

	/* 判断是否滑动的最小距�?*/
	private int mTouchSlop;

	/* 手指触摸到屏幕时y�?*/
	private float mYDown;

	/* 手指在竖直方向上移动的距�?*/
	private float mYMove;

	/* 标记�?��的位�?*/
	private float mLastMove;

	/* 标记上边�?*/
	private int mTopBorder;

	/* 标记下边�?*/
	private int mBottomBorder;

	public VDViewpager(Context context) {
		this(context, null);
	}

	public VDViewpager(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VDViewpager(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		/* 初始化滚动实�?*/
		mScroller = new Scroller(context);

		/* 获取�?��滑动值，该�?用于判断是否可滑 */
		ViewConfiguration vc = ViewConfiguration.get(context);
		mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(vc);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int cCount = this.getChildCount();
		for (int i = 0; i < cCount; i++) {
			View childView = this.getChildAt(i);
			/* 测量每一个子View */
			measureChild(childView, widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			int cCount = this.getChildCount();
			for (int i = 0; i < cCount; i++) {
				View childView = this.getChildAt(i);
				/* 从上�?��布局 */
				childView.layout(0, i * childView.getMeasuredHeight(),
						childView.getMeasuredWidth(),
						(i + 1) * childView.getMeasuredHeight());
			}
			mTopBorder = this.getChildAt(0).getTop();
			mBottomBorder = this.getChildAt(this.getChildCount() - 1)
					.getBottom();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (canScroller) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mYDown = ev.getRawY();
				mLastMove = mYDown;
				break;
			case MotionEvent.ACTION_MOVE:
				mYMove = ev.getRawY();
				int diff = (int) Math.abs(mYMove - mYDown);
				if (diff > mTouchSlop) { // 判断是否可滑
					return true;
				}
				mLastMove = mYMove;
				break;
			default:
				break;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (canScroller) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				mYMove = event.getRawY();
				int scrolledY = (int) (mLastMove - mYMove);
				if (this.getScrollY() + scrolledY < mTopBorder) {
					/* 到第�?��子View顶端时不给下�?*/
					this.scrollTo(mTopBorder, 0);
					return true;
				} else if (this.getScrollY() + scrolledY + this.getHeight() > mBottomBorder) {
					/* 到最后一个子View底端时不给上�?*/
					this.scrollTo(0, mBottomBorder - this.getHeight());
					return true;
				}
				this.scrollBy(0, scrolledY);
				mLastMove = mYMove;
				break;
			case MotionEvent.ACTION_UP:
				int targetIndex = (this.getScrollY() + this.getHeight() / 2)
						/ this.getHeight();
				/* 得到应该滑动的距�?*/
				int dy = targetIndex * this.getHeight() - this.getScrollY();
				/* �?��滑动 */
				mScroller.startScroll(0, this.getScrollY(), 0, dy);
				invalidate();
				break;
			default:
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		/* 平滑滚动 */
		if (mScroller.computeScrollOffset()) {
			this.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			invalidate();
		}
	}
}



