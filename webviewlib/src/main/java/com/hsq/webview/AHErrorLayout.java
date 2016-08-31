package com.hsq.webview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 显示加载中，加载失败，无数据等布局，说明
 */
public class AHErrorLayout extends LinearLayout {
	public static final int TYPE_NETWORK_ERROR = 1;
	public static final int TYPE_LOADING = 2;
	public static final int TYPE_NO_DATA = 3;
	public static final int TYPE_HIDE = 4;
	public static final int TYPE_NO_DATA_ENABLE_CLICK = 5;
	public static final int TYPE_NO_DATA_GUIDE = 6;

	private Context context;
	private View mRootView;
	private OnClickListener listener;
	private LinearLayout mLoadFailLayout;
	private RelativeLayout mNoDatalayout;
	private TextView mNoDataHint;
	private TextView mMessageTv;
	private ProgressBar mProgress;
	private int mLoadState;

	public AHErrorLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public AHErrorLayout(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private void init() {
		View view = View.inflate(context, R.layout.error_layout, null);
		mRootView = view;
		mLoadFailLayout = (LinearLayout) view.findViewById(R.id.load_fail_layout);
		mNoDatalayout=(RelativeLayout)view.findViewById(R.id.load_no_data_layout);
		mNoDataHint=(TextView)view.findViewById(R.id.no_data_tv);
		mMessageTv = (TextView) view.findViewById(R.id.empty_message_tv);
		mProgress = (ProgressBar) view.findViewById(R.id.progressbar);


		Button reloadBtn = (Button)mLoadFailLayout.findViewById(R.id.reload_btn);
		reloadBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null){
					setErrorType(TYPE_LOADING);
					listener.onClick(v);
				}
			}
		});

		addView(view, new ViewGroup.LayoutParams(-1, -1));
	}

	private void setMessage(String message) {
		mMessageTv.setText(message);
	}

	public void setNotDataHint(String msg){
		mNoDataHint.setText(msg);
	}

	public synchronized void setErrorType(int type) {

        if (mLoadState == type){
            return;
        }
		setVisibility(View.VISIBLE);
		mLoadFailLayout.setVisibility(GONE);
		mNoDatalayout.setVisibility(View.GONE);
		mMessageTv.setVisibility(GONE);
		mProgress.setVisibility(GONE);
		switch (type) {
			case TYPE_NETWORK_ERROR:
				mLoadState = TYPE_NETWORK_ERROR;
				mLoadFailLayout.setVisibility(View.VISIBLE);
				break;
			case TYPE_LOADING:
				mLoadState = TYPE_LOADING;
				mProgress.setVisibility(VISIBLE);
				break;
			case TYPE_NO_DATA:
				mLoadState = TYPE_NO_DATA;
				mMessageTv.setVisibility(VISIBLE);
				break;
			case TYPE_NO_DATA_GUIDE:
				mLoadState = TYPE_NO_DATA_GUIDE;
				mNoDatalayout.setVisibility(VISIBLE);
				break;
			case TYPE_NO_DATA_ENABLE_CLICK:
				mLoadState = TYPE_NO_DATA_ENABLE_CLICK;
				mLoadFailLayout.setVisibility(View.VISIBLE);
				break;
			case TYPE_HIDE:
                mLoadState = TYPE_HIDE;
				setVisibility(View.GONE);
                //translateLoadingView(0, ScreenUtils.getCurrentScreenWidth() / 2 + ScreenUtils.dp2Px(60) / 2);
				break;
			default:
				break;
		}

	}

	@Override
	public void setVisibility(int visibility) {
		if (visibility == View.GONE) {
			mLoadState = TYPE_HIDE;
		}
		super.setVisibility(visibility);
	}

	public boolean isLoadError() {
		if (mLoadState == TYPE_NETWORK_ERROR)
			return true;
		else
			return false;
	}

	public boolean isLoading() {
		if (mLoadState == TYPE_LOADING)
			return true;
		else
			return false;
	}

	public void setNoDataContent(String message) {
		setErrorType(TYPE_NO_DATA);
		setMessage(TextUtils.isEmpty(message)?"暂时没有内容":message);
	}

	public int getErrorState() {
		return mLoadState;
	}

	public void setOnLayoutClickListener(OnClickListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}


    public void translateLoadingView(final float p1, final float p2) {
        TranslateAnimation animation = new TranslateAnimation(p1, p2, 0, 0);
        //animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(300);
        animation.setStartOffset(0);//delay
        animation.setFillEnabled(true);
        animation.setFillBefore(true);
        animation.setFillAfter(false);
        animation.setFillEnabled(false);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mProgress.clearAnimation();
                setVisibility(View.GONE);
            }
        });
        mProgress.startAnimation(animation);

    }
}
