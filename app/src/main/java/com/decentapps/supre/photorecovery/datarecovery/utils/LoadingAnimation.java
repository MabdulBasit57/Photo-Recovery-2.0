package com.decentapps.supre.photorecovery.datarecovery.utils;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentActivity;

import com.decentapps.supre.photorecovery.datarecovery.R;

public class LoadingAnimation {
    private static LoadingAnimation loadingAnimationOBJ;

    public ViewGroup CurrentView = null;
    private boolean IsShown = false;

    public RelativeLayout LoadingView;

    public int Timeout = 6000;
//    private AVLoadingIndicatorView avLoadingIndicatorView;

    public Context ctx;
    ProgressDialog dialog;
    private int dp;

    public Handler handler = new Handler(Looper.getMainLooper());

    public Runnable runnable = new Runnable() {
        public void run() {
            LoadingAnimation.this.RemoveLoadingBar();
        }
    };

    LoadingAnimation() {
    }

    public static LoadingAnimation getInstance(ViewGroup viewGroup) {
        loadingAnimationOBJ = new LoadingAnimation();
        loadingAnimationOBJ.SetView(viewGroup);
        return loadingAnimationOBJ;
    }

    private void SetView(ViewGroup viewGroup) {
        this.ctx = viewGroup.getContext();
        this.dp = (int) (viewGroup.getResources().getDimension(R.dimen.ProcessBar) / viewGroup.getResources().getDisplayMetrics().density);
        Context context = this.ctx;
        if (((Activity) context) != null) {
            this.CurrentView = (ViewGroup) ((Activity) context).getWindow().findViewById(16908290);
        }
        if (this.CurrentView == null) {
            this.CurrentView = viewGroup;
        }
    }

    public LoadingAnimation LoadingShow(boolean z, boolean z2) {
        LoadingHide();
        this.IsShown = true;
        if (z) {
            ShowLoadingBarWithBlock();
        } else {
            ShowLoadingBarNoBlock(z2);
        }
        return loadingAnimationOBJ;
    }

    public void LoadingShow() {
        LoadingShow(false, false);
    }

    public void LoadingHide() {
        RemoveLoadingBar();
    }


    public void CreateAVIView(boolean z, boolean z2) {
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -1);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -2);
        final RelativeLayout relativeLayout = new RelativeLayout(this.ctx);
        layoutParams2.addRule(13, -1);
        if (z2) {
            layoutParams2.addRule(12, -1);
            layoutParams2.bottomMargin = 100;
        }
//        this.avLoadingIndicatorView = new AVLoadingIndicatorView(this.ctx, (AttributeSet) null);
//        this.avLoadingIndicatorView.setIndicator("BallPulseIndicator");
//        this.avLoadingIndicatorView.setIndicatorColor(Color.parseColor("#000000"));
        int i = this.dp;
        layoutParams2.width = i;
        layoutParams2.height = i;
//        this.avLoadingIndicatorView.setLayoutParams(layoutParams2);
//        relativeLayout.addView(this.avLoadingIndicatorView, layoutParams2);
        if (z) {
            this.handler.post(new Runnable() {
                public void run() {
                    LoadingAnimation.this.CurrentView.addView(relativeLayout, layoutParams);
                    LoadingAnimation.this.CurrentView.bringChildToFront(relativeLayout);
                }
            });
        }
        this.LoadingView = relativeLayout;
    }

    private void ShowLoadingBarNoBlock(boolean z) {
        CreateAVIView(true, z);
        this.handler.postDelayed(this.runnable, (long) this.Timeout);
        this.Timeout = 6000;
    }

    private void ShowLoadingBarWithBlock() {
        CreateAVIView(false, false);
        ProgressDialog progressDialog = this.dialog;
        if (progressDialog != null && progressDialog.isShowing() && !CheckisDestroyed()) {
            this.dialog.dismiss();
        }
        if (!CheckisDestroyed()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (!LoadingAnimation.this.CheckisDestroyed()) {
                        LoadingAnimation loadingAnimation = LoadingAnimation.this;
                        loadingAnimation.dialog = ProgressDialog.show(loadingAnimation.ctx, (CharSequence) null, (CharSequence) null);
                        LoadingAnimation.this.dialog.getWindow().setGravity(17);
                        LoadingAnimation.this.dialog.setContentView(LoadingAnimation.this.LoadingView);
                        LoadingAnimation.this.dialog.getWindow().getDecorView().setBackgroundColor(Color.parseColor("#A6FFFFFF"));
                        LoadingAnimation.this.dialog.getWindow().setLayout(-1, -1);
                    }
                    LoadingAnimation.this.handler.postDelayed(LoadingAnimation.this.runnable, (long) LoadingAnimation.this.Timeout);
                }
            }, 100);
        }
        this.Timeout = 6000;
    }


    public boolean CheckisDestroyed() {
        Context context = this.ctx;
        if (context == null) {
            return true;
        }
        if ((context instanceof Application) || Build.VERSION.SDK_INT < 17) {
            return false;
        }
        Context context2 = this.ctx;
        if (context2 instanceof FragmentActivity) {
            if (((FragmentActivity) context2).isDestroyed()) {
                return true;
            }
            return false;
        } else if (!(context2 instanceof Activity) || !((Activity) context2).isDestroyed()) {
            return false;
        } else {
            return true;
        }
    }

    public void RemoveLoadingBar() {
        this.IsShown = false;
        this.handler.removeCallbacks(this.runnable);
        this.handler.post(new Runnable() {
            public void run() {
                if (LoadingAnimation.this.dialog != null && LoadingAnimation.this.dialog.isShowing() && !LoadingAnimation.this.CheckisDestroyed()) {
                    LoadingAnimation.this.dialog.dismiss();
                }
                if (LoadingAnimation.this.CurrentView != null && LoadingAnimation.this.LoadingView != null) {
                    LoadingAnimation.this.CurrentView.removeView(LoadingAnimation.this.LoadingView);
                }
            }
        });
    }

    public void SetTimeout(int i) {
        this.Timeout = i * 1000;
    }
}
