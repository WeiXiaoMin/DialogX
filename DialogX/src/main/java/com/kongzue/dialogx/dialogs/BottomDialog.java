package com.kongzue.dialogx.dialogs;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;

import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.R;
import com.kongzue.dialogx.interfaces.BaseDialog;
import com.kongzue.dialogx.interfaces.DialogConvertViewInterface;
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback;
import com.kongzue.dialogx.interfaces.DialogXStyle;
import com.kongzue.dialogx.interfaces.OnBackPressedListener;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.ScrollController;
import com.kongzue.dialogx.util.BottomDialogTouchEventInterceptor;
import com.kongzue.dialogx.util.TextInfo;
import com.kongzue.dialogx.util.views.BlurView;
import com.kongzue.dialogx.util.views.BottomDialogScrollView;
import com.kongzue.dialogx.util.views.DialogXBaseRelativeLayout;
import com.kongzue.dialogx.util.views.MaxRelativeLayout;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2020/10/6 15:17
 */
public class BottomDialog extends BaseDialog {
    
    public static int overrideEnterDuration = -1;
    public static int overrideExitDuration = -1;
    public static BOOLEAN overrideCancelable;
    protected OnBindView<BottomDialog> onBindView;
    protected CharSequence title;
    protected CharSequence message;
    protected CharSequence cancelText;
    protected CharSequence okText;
    protected CharSequence otherText;
    protected boolean allowInterceptTouch = true;
    protected int maskColor = -1;
    protected OnDialogButtonClickListener<BottomDialog> cancelButtonClickListener;
    protected OnDialogButtonClickListener<BottomDialog> okButtonClickListener;
    protected OnDialogButtonClickListener<BottomDialog> otherButtonClickListener;
    protected BOOLEAN privateCancelable;
    
    protected TextInfo titleTextInfo;
    protected TextInfo messageTextInfo;
    protected TextInfo menuTextInfo;
    protected TextInfo cancelTextInfo = new TextInfo().setBold(true);
    protected TextInfo okTextInfo = new TextInfo().setBold(true);
    protected TextInfo otherTextInfo = new TextInfo().setBold(true);
    
    /**
     * 此值用于，当禁用滑动时（style.overrideBottomDialogRes.touchSlide = false时）的最大显示高度。
     * 0：不限制，最大显示到屏幕可用高度。
     */
    protected float bottomDialogMaxHeight = 0.6f;
    
    protected DialogLifecycleCallback<BottomDialog> dialogLifecycleCallback;
    
    protected BottomDialog me = this;
    
    protected BottomDialog() {
        super();
    }
    
    @Override
    public String dialogKey() {
        return getClass().getSimpleName() + "(" + Integer.toHexString(hashCode()) + ")";
    }
    
    private View dialogView;
    
    public static BottomDialog build() {
        return new BottomDialog();
    }
    
    public static BottomDialog build(OnBindView<BottomDialog> onBindView) {
        return new BottomDialog().setCustomView(onBindView);
    }
    
    public BottomDialog(CharSequence title, CharSequence message) {
        this.title = title;
        this.message = message;
    }
    
    public BottomDialog(int titleResId, int messageResId) {
        this.title = getString(titleResId);
        this.message = getString(messageResId);
    }
    
    public static BottomDialog show(CharSequence title, CharSequence message) {
        BottomDialog bottomDialog = new BottomDialog(title, message);
        bottomDialog.show();
        return bottomDialog;
    }
    
    public static BottomDialog show(int titleResId, int messageResId) {
        BottomDialog bottomDialog = new BottomDialog(titleResId, messageResId);
        bottomDialog.show();
        return bottomDialog;
    }
    
    public BottomDialog(CharSequence title, CharSequence message, OnBindView<BottomDialog> onBindView) {
        this.title = title;
        this.message = message;
        this.onBindView = onBindView;
    }
    
    public BottomDialog(int titleResId, int messageResId, OnBindView<BottomDialog> onBindView) {
        this.title = getString(titleResId);
        this.message = getString(messageResId);
        this.onBindView = onBindView;
    }
    
    public static BottomDialog show(CharSequence title, CharSequence message, OnBindView<BottomDialog> onBindView) {
        BottomDialog bottomDialog = new BottomDialog(title, message, onBindView);
        bottomDialog.show();
        return bottomDialog;
    }
    
    public static BottomDialog show(int titleResId, int messageResId, OnBindView<BottomDialog> onBindView) {
        BottomDialog bottomDialog = new BottomDialog(titleResId, messageResId, onBindView);
        bottomDialog.show();
        return bottomDialog;
    }
    
    public BottomDialog(CharSequence title, OnBindView<BottomDialog> onBindView) {
        this.title = title;
        this.onBindView = onBindView;
    }
    
    public BottomDialog(int titleResId, OnBindView<BottomDialog> onBindView) {
        this.title = getString(titleResId);
        this.onBindView = onBindView;
    }
    
    public static BottomDialog show(CharSequence title, OnBindView<BottomDialog> onBindView) {
        BottomDialog bottomDialog = new BottomDialog(title, onBindView);
        bottomDialog.show();
        return bottomDialog;
    }
    
    public static BottomDialog show(int titleResId, OnBindView<BottomDialog> onBindView) {
        BottomDialog bottomDialog = new BottomDialog(titleResId, onBindView);
        bottomDialog.show();
        return bottomDialog;
    }
    
    public BottomDialog(OnBindView<BottomDialog> onBindView) {
        this.onBindView = onBindView;
    }
    
    public static BottomDialog show(OnBindView<BottomDialog> onBindView) {
        BottomDialog bottomDialog = new BottomDialog(onBindView);
        bottomDialog.show();
        return bottomDialog;
    }
    
    public void show() {
        super.beforeShow();
        if (getDialogView() == null) {
            int layoutId = isLightTheme() ? R.layout.layout_dialogx_bottom_material : R.layout.layout_dialogx_bottom_material_dark;
            if (style.overrideBottomDialogRes() != null) {
                layoutId = style.overrideBottomDialogRes().overrideDialogLayout(isLightTheme());
            }
            
            dialogView = createView(layoutId);
            dialogImpl = new DialogImpl(dialogView);
            if (dialogView != null) dialogView.setTag(me);
        }
        show(dialogView);
    }
    
    public void show(Activity activity) {
        super.beforeShow();
        if (getDialogView() == null) {
            int layoutId = isLightTheme() ? R.layout.layout_dialogx_bottom_material : R.layout.layout_dialogx_bottom_material_dark;
            if (style.overrideBottomDialogRes() != null) {
                layoutId = style.overrideBottomDialogRes().overrideDialogLayout(isLightTheme());
            }
            
            dialogView = createView(layoutId);
            dialogImpl = new DialogImpl(dialogView);
            if (dialogView != null) dialogView.setTag(me);
        }
        show(activity, dialogView);
    }
    
    protected DialogImpl dialogImpl;
    
    public class DialogImpl implements DialogConvertViewInterface {
        
        private BottomDialogTouchEventInterceptor bottomDialogTouchEventInterceptor;
        
        public DialogXBaseRelativeLayout boxRoot;
        public RelativeLayout boxBkg;
        public MaxRelativeLayout bkg;
        public ViewGroup boxBody;
        public ImageView imgTab;
        public TextView txtDialogTitle;
        public ScrollController scrollView;
        public LinearLayout boxContent;
        public TextView txtDialogTip;
        public View imgSplit;
        public RelativeLayout boxList;
        public RelativeLayout boxCustom;
        public BlurView blurView;
        public ViewGroup boxCancel;
        public TextView btnCancel;
        public BlurView cancelBlurView;
        
        public TextView btnSelectOther;
        public TextView btnSelectPositive;
        
        public DialogImpl(View convertView) {
            if (convertView == null) return;
            boxRoot = convertView.findViewById(R.id.box_root);
            boxBkg = convertView.findViewById(R.id.box_bkg);
            bkg = convertView.findViewById(R.id.bkg);
            boxBody = convertView.findViewWithTag("body");
            imgTab = convertView.findViewById(R.id.img_tab);
            txtDialogTitle = convertView.findViewById(R.id.txt_dialog_title);
            scrollView = convertView.findViewById(R.id.scrollView);
            boxContent = convertView.findViewById(R.id.box_content);
            txtDialogTip = convertView.findViewById(R.id.txt_dialog_tip);
            imgSplit = convertView.findViewWithTag("split");
            boxList = convertView.findViewById(R.id.box_list);
            boxCustom = convertView.findViewById(R.id.box_custom);
            blurView = convertView.findViewById(R.id.blurView);
            boxCancel = convertView.findViewWithTag("cancelBox");
            btnCancel = convertView.findViewWithTag("cancel");
            
            btnSelectOther = convertView.findViewById(R.id.btn_selectOther);
            btnSelectPositive = convertView.findViewById(R.id.btn_selectPositive);
            
            init();
            dialogImpl = this;
            refreshView();
        }
        
        public void reBuild() {
            init();
            dialogImpl = this;
            refreshView();
        }
        
        /**
         * 此值记录了BottomDialog启动后的位置
         * ·当内容高度大于屏幕安全区高度时，BottomDialog会以全屏方式启动，但一开始只会展开到 0.8×屏幕高度，
         * 此时可以再次上划查看全部内容。
         * ·当内容高度小于屏幕安全区高度时，BottomDialog会以内容高度启动。
         * <p>
         * 记录这个值的目的是，当用户向下滑动时，判断情况该回到这个位置还是关闭对话框，
         * 并阻止当内容高度已经完全显示时的继续向上滑动操作。
         */
        public float bkgEnterAimY = -1;
        
        @Override
        public void init() {
            if (titleTextInfo == null) titleTextInfo = DialogX.menuTitleInfo;
            if (titleTextInfo == null) titleTextInfo = DialogX.titleTextInfo;
            if (messageTextInfo == null) messageTextInfo = DialogX.messageTextInfo;
            if (okTextInfo == null) okTextInfo = DialogX.okButtonTextInfo;
            if (okTextInfo == null) okTextInfo = DialogX.buttonTextInfo;
            if (cancelTextInfo == null) cancelTextInfo = DialogX.buttonTextInfo;
            if (otherTextInfo == null) otherTextInfo = DialogX.buttonTextInfo;
            if (backgroundColor == -1) backgroundColor = DialogX.backgroundColor;
            if (cancelText == null) cancelText = DialogX.cancelButtonText;
            
            txtDialogTitle.getPaint().setFakeBoldText(true);
            if (btnCancel != null) btnCancel.getPaint().setFakeBoldText(true);
            if (btnSelectPositive != null) btnSelectPositive.getPaint().setFakeBoldText(true);
            if (btnSelectOther != null) btnSelectOther.getPaint().setFakeBoldText(true);
    
            boxBkg.setY(getRootFrameLayout().getMeasuredHeight());
            bkg.setMaxWidth(getMaxWidth());
            boxRoot.setParentDialog(me);
            boxRoot.setOnLifecycleCallBack(new DialogXBaseRelativeLayout.OnLifecycleCallBack() {
                @Override
                public void onShow() {
                    
                    isShow = true;
                    
                    getDialogLifecycleCallback().onShow(me);
                    
                    onDialogInit(dialogImpl);
                    
                    boxRoot.post(new Runnable() {
                        @Override
                        public void run() {
                            if (style.messageDialogBlurSettings() != null && style.messageDialogBlurSettings().blurBackground() && boxBody != null && boxCancel != null) {
                                int blurFrontColor = getResources().getColor(style.messageDialogBlurSettings().blurForwardColorRes(isLightTheme()));
                                blurView = new BlurView(bkg.getContext(), null);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bkg.getWidth(), bkg.getHeight());
                                blurView.setOverlayColor(backgroundColor == -1 ? blurFrontColor : backgroundColor);
                                blurView.setTag("blurView");
                                blurView.setRadiusPx(style.messageDialogBlurSettings().blurBackgroundRoundRadiusPx());
                                boxBody.addView(blurView, 0, params);
                                
                                cancelBlurView = new BlurView(boxCancel.getContext(), null);
                                RelativeLayout.LayoutParams cancelButtonLp = new RelativeLayout.LayoutParams(boxCancel.getWidth(), boxCancel.getHeight());
                                cancelBlurView.setOverlayColor(backgroundColor == -1 ? blurFrontColor : backgroundColor);
                                cancelBlurView.setTag("blurView");
                                cancelBlurView.setRadiusPx(style.messageDialogBlurSettings().blurBackgroundRoundRadiusPx());
                                boxCancel.addView(cancelBlurView, 0, cancelButtonLp);
                            }
                        }
                    });
                    
                    refreshUI();
                }
                
                @Override
                public void onDismiss() {
                    isShow = false;
                    getDialogLifecycleCallback().onDismiss(me);
                    dialogImpl = null;
                    bottomDialogTouchEventInterceptor = null;
                    dialogLifecycleCallback = null;
                    System.gc();
                }
            });
            
            if (btnCancel != null) {
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cancelButtonClickListener != null) {
                            if (!cancelButtonClickListener.onClick(me, v)) {
                                dismiss();
                            }
                        } else {
                            dismiss();
                        }
                    }
                });
            }
            if (btnSelectOther != null) {
                btnSelectOther.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (otherButtonClickListener != null) {
                            if (!otherButtonClickListener.onClick(me, v)) {
                                dismiss();
                            }
                        } else {
                            dismiss();
                        }
                    }
                });
            }
            if (btnSelectPositive != null) {
                btnSelectPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (okButtonClickListener != null) {
                            if (!okButtonClickListener.onClick(me, v)) {
                                dismiss();
                            }
                        } else {
                            dismiss();
                        }
                    }
                });
            }
            
            if (imgSplit != null) {
                int dividerRes = style.overrideBottomDialogRes().overrideMenuDividerDrawableRes(isLightTheme());
                int dividerHeight = style.overrideBottomDialogRes().overrideMenuDividerHeight(isLightTheme());
                if (dividerRes != 0) imgSplit.setBackgroundResource(dividerRes);
                if (dividerHeight != 0) {
                    ViewGroup.LayoutParams lp = imgSplit.getLayoutParams();
                    lp.height = dividerHeight;
                    imgSplit.setLayoutParams(lp);
                }
            }
            
            boxRoot.setOnBackPressedListener(new OnBackPressedListener() {
                @Override
                public boolean onBackPressed() {
                    if (onBackPressedListener != null) {
                        if (onBackPressedListener.onBackPressed()) {
                            dismiss();
                        }
                        return false;
                    }
                    if (isCancelable()) {
                        dismiss();
                    }
                    return false;
                }
            });
    
            boxBkg.post(new Runnable() {
                @Override
                public void run() {
                    long enterAnimDurationTemp = 300;
                    
                    //上移动画
                    ObjectAnimator enterAnim = ObjectAnimator.ofFloat(boxBkg, "y", boxBkg.getY(), boxRoot.getUnsafePlace().top);
                    if (overrideEnterDuration >= 0) {
                        enterAnimDurationTemp = overrideEnterDuration;
                    }
                    if (enterAnimDuration >= 0) {
                        enterAnimDurationTemp = enterAnimDuration;
                    }
                    enterAnim.setDuration(enterAnimDurationTemp);
                    enterAnim.setAutoCancel(true);
                    enterAnim.setInterpolator(new DecelerateInterpolator(2f));
                    enterAnim.start();
    
                    //遮罩层动画
                    ValueAnimator bkgAlpha = ValueAnimator.ofFloat(0f, 1f);
                    bkgAlpha.setDuration(enterAnimDurationTemp);
                    bkgAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (float) animation.getAnimatedValue();
                            boxRoot.setBkgAlpha(value);
                            if (value == 1f) {
                                bottomDialogTouchEventInterceptor = new BottomDialogTouchEventInterceptor(me, dialogImpl);
                            }
                        }
                    });
                    bkgAlpha.start();
                }
            });
        }
        
        @Override
        public void refreshView() {
            if (boxRoot == null || getContext() == null) {
                return;
            }
            if (backgroundColor != -1) {
                tintColor(bkg, backgroundColor);
                if (blurView != null && cancelBlurView != null) {
                    blurView.setOverlayColor(backgroundColor);
                    cancelBlurView.setOverlayColor(backgroundColor);
                }
                
                tintColor(btnSelectOther, backgroundColor);
                tintColor(btnCancel, backgroundColor);
                tintColor(btnSelectPositive, backgroundColor);
            }
            
            showText(txtDialogTitle, title);
            showText(txtDialogTip, message);
            
            useTextInfo(txtDialogTitle, titleTextInfo);
            useTextInfo(txtDialogTip, messageTextInfo);
            useTextInfo(btnCancel, cancelTextInfo);
            useTextInfo(btnSelectOther, otherTextInfo);
            useTextInfo(btnSelectPositive, okTextInfo);
            
            if (isCancelable()) {
                boxRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doDismiss(v);
                    }
                });
            } else {
                boxRoot.setOnClickListener(null);
            }
            boxBkg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boxRoot.callOnClick();
                }
            });
            
            if (maskColor != -1) boxRoot.setBackgroundColor(maskColor);
            
            if (onBindView != null && onBindView.getCustomView() != null) {
                onBindView.bindParent(boxCustom, me);
                if (onBindView.getCustomView() instanceof ScrollController) {
                    if (scrollView instanceof BottomDialogScrollView) {
                        ((BottomDialogScrollView) scrollView).setVerticalScrollBarEnabled(false);
                    }
                    scrollView = (ScrollController) onBindView.getCustomView();
                } else {
                    View scrollController = onBindView.getCustomView().findViewWithTag("ScrollController");
                    if (scrollController instanceof ScrollController) {
                        if (scrollView instanceof BottomDialogScrollView) {
                            ((BottomDialogScrollView) scrollView).setVerticalScrollBarEnabled(false);
                        }
                        scrollView = (ScrollController) scrollController;
                    }
                }
            }
            
            if (isAllowInterceptTouch() && isCancelable()) {
                if (imgTab != null) imgTab.setVisibility(View.VISIBLE);
            } else {
                if (imgTab != null) imgTab.setVisibility(View.GONE);
            }
            
            if (bottomDialogTouchEventInterceptor != null) {
                bottomDialogTouchEventInterceptor.refresh(me, this);
            }
            
            if (imgSplit != null) {
                if (txtDialogTitle.getVisibility() == View.VISIBLE || txtDialogTip.getVisibility() == View.VISIBLE) {
                    imgSplit.setVisibility(View.VISIBLE);
                } else {
                    imgSplit.setVisibility(View.GONE);
                }
            }
            
            if (boxCancel != null) {
                if (isNull(cancelText)) {
                    boxCancel.setVisibility(View.GONE);
                } else {
                    boxCancel.setVisibility(View.VISIBLE);
                }
            }
            
            showText(btnSelectPositive, okText);
            showText(btnCancel, cancelText);
            showText(btnSelectOther, otherText);
        }
        
        @Override
        public void doDismiss(View v) {
            if (v != null) v.setEnabled(false);
            if (getContext() == null) return;
            
            if (!dismissAnimFlag) {
                dismissAnimFlag = true;
                
                long exitAnimDurationTemp = 300;
                if (overrideExitDuration >= 0) {
                    exitAnimDurationTemp = overrideExitDuration;
                }
                if (exitAnimDuration >= 0) {
                    exitAnimDurationTemp = exitAnimDuration;
                }
                
                ObjectAnimator exitAnim = ObjectAnimator.ofFloat(bkg, "y", bkg.getY(), boxBkg.getHeight());
                exitAnim.setDuration(exitAnimDurationTemp);
                exitAnim.start();
                
                ValueAnimator bkgAlpha = ValueAnimator.ofFloat(1f, 0f);
                bkgAlpha.setDuration(exitAnimDurationTemp);
                bkgAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (boxRoot != null) {
                            float value = (float) animation.getAnimatedValue();
                            boxRoot.setBkgAlpha(value);
                            if (value == 0) boxRoot.setVisibility(View.GONE);
                        }
                    }
                });
                bkgAlpha.start();
                
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss(dialogView);
                    }
                }, exitAnimDurationTemp);
            }
        }
        
        public void preDismiss() {
            if (isCancelable()) {
                doDismiss(boxRoot);
            } else {
                long exitAnimDurationTemp = 300;
                if (overrideExitDuration >= 0) {
                    exitAnimDurationTemp = overrideExitDuration;
                }
                if (exitAnimDuration >= 0) {
                    exitAnimDurationTemp = exitAnimDuration;
                }
                ObjectAnimator exitAnim = ObjectAnimator.ofFloat(bkg, "y", bkg.getY(), bkgEnterAimY);
                exitAnim.setDuration(exitAnimDurationTemp);
                exitAnim.start();
            }
        }
    }
    
    protected void onDialogInit(DialogImpl dialog) {
    }
    
    public void refreshUI() {
        if (getDialogImpl() == null) return;
        runOnMain(new Runnable() {
            @Override
            public void run() {
                if (dialogImpl != null) dialogImpl.refreshView();
            }
        });
    }
    
    public void dismiss() {
        runOnMain(new Runnable() {
            @Override
            public void run() {
                if (dialogImpl == null) return;
                dialogImpl.doDismiss(null);
            }
        });
    }
    
    public DialogLifecycleCallback<BottomDialog> getDialogLifecycleCallback() {
        return dialogLifecycleCallback == null ? new DialogLifecycleCallback<BottomDialog>() {
        } : dialogLifecycleCallback;
    }
    
    public BottomDialog setDialogLifecycleCallback(DialogLifecycleCallback<BottomDialog> dialogLifecycleCallback) {
        this.dialogLifecycleCallback = dialogLifecycleCallback;
        if (isShow) dialogLifecycleCallback.onShow(me);
        return this;
    }
    
    public OnBackPressedListener getOnBackPressedListener() {
        return onBackPressedListener;
    }
    
    public BottomDialog setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
        refreshUI();
        return this;
    }
    
    public BottomDialog setStyle(DialogXStyle style) {
        this.style = style;
        return this;
    }
    
    public BottomDialog setTheme(DialogX.THEME theme) {
        this.theme = theme;
        return this;
    }
    
    public boolean isCancelable() {
        if (privateCancelable != null) {
            return privateCancelable == BOOLEAN.TRUE;
        }
        if (overrideCancelable != null) {
            return overrideCancelable == BOOLEAN.TRUE;
        }
        return cancelable;
    }
    
    public BottomDialog setCancelable(boolean cancelable) {
        this.privateCancelable = cancelable ? BOOLEAN.TRUE : BOOLEAN.FALSE;
        refreshUI();
        return this;
    }
    
    public DialogImpl getDialogImpl() {
        return dialogImpl;
    }
    
    public CharSequence getTitle() {
        return title;
    }
    
    public BottomDialog setTitle(CharSequence title) {
        this.title = title;
        refreshUI();
        return this;
    }
    
    public BottomDialog setTitle(int titleResId) {
        this.title = getString(titleResId);
        refreshUI();
        return this;
    }
    
    public CharSequence getMessage() {
        return message;
    }
    
    public BottomDialog setMessage(CharSequence message) {
        this.message = message;
        refreshUI();
        return this;
    }
    
    public BottomDialog setMessage(int messageResId) {
        this.message = getString(messageResId);
        refreshUI();
        return this;
    }
    
    public CharSequence getCancelButton() {
        return cancelText;
    }
    
    public BottomDialog setCancelButton(CharSequence cancelText) {
        this.cancelText = cancelText;
        refreshUI();
        return this;
    }
    
    public BottomDialog setCancelButton(int cancelTextResId) {
        this.cancelText = getString(cancelTextResId);
        refreshUI();
        return this;
    }
    
    public BottomDialog setCancelButton(OnDialogButtonClickListener<BottomDialog> cancelButtonClickListener) {
        this.cancelButtonClickListener = cancelButtonClickListener;
        return this;
    }
    
    public BottomDialog setCancelButton(CharSequence cancelText, OnDialogButtonClickListener<BottomDialog> cancelButtonClickListener) {
        this.cancelText = cancelText;
        this.cancelButtonClickListener = cancelButtonClickListener;
        refreshUI();
        return this;
    }
    
    public BottomDialog setCancelButton(int cancelTextResId, OnDialogButtonClickListener<BottomDialog> cancelButtonClickListener) {
        this.cancelText = getString(cancelTextResId);
        this.cancelButtonClickListener = cancelButtonClickListener;
        refreshUI();
        return this;
    }
    
    public BottomDialog setCustomView(OnBindView<BottomDialog> onBindView) {
        this.onBindView = onBindView;
        refreshUI();
        return this;
    }
    
    public View getCustomView() {
        if (onBindView == null) return null;
        return onBindView.getCustomView();
    }
    
    public BottomDialog removeCustomView() {
        this.onBindView.clean();
        refreshUI();
        return this;
    }
    
    public boolean isAllowInterceptTouch() {
        if (style.overrideBottomDialogRes() == null) {
            return false;
        } else {
            return allowInterceptTouch && style.overrideBottomDialogRes().touchSlide();
        }
    }
    
    public BottomDialog setAllowInterceptTouch(boolean allowInterceptTouch) {
        this.allowInterceptTouch = allowInterceptTouch;
        refreshUI();
        return this;
    }
    
    public OnDialogButtonClickListener<BottomDialog> getCancelButtonClickListener() {
        return cancelButtonClickListener;
    }
    
    public BottomDialog setCancelButtonClickListener(OnDialogButtonClickListener<BottomDialog> cancelButtonClickListener) {
        this.cancelButtonClickListener = cancelButtonClickListener;
        return this;
    }
    
    public TextInfo getTitleTextInfo() {
        return titleTextInfo;
    }
    
    public BottomDialog setTitleTextInfo(TextInfo titleTextInfo) {
        this.titleTextInfo = titleTextInfo;
        refreshUI();
        return this;
    }
    
    public TextInfo getMessageTextInfo() {
        return messageTextInfo;
    }
    
    public BottomDialog setMessageTextInfo(TextInfo messageTextInfo) {
        this.messageTextInfo = messageTextInfo;
        refreshUI();
        return this;
    }
    
    public TextInfo getCancelTextInfo() {
        return cancelTextInfo;
    }
    
    public BottomDialog setCancelTextInfo(TextInfo cancelTextInfo) {
        this.cancelTextInfo = cancelTextInfo;
        refreshUI();
        return this;
    }
    
    public int getBackgroundColor() {
        return backgroundColor;
    }
    
    public BottomDialog setBackgroundColor(@ColorInt int backgroundColor) {
        this.backgroundColor = backgroundColor;
        refreshUI();
        return this;
    }
    
    public BottomDialog setBackgroundColorRes(@ColorRes int backgroundRes) {
        this.backgroundColor = getColor(backgroundRes);
        refreshUI();
        return this;
    }
    
    public CharSequence getOkButton() {
        return okText;
    }
    
    public BottomDialog setOkButton(CharSequence okText) {
        this.okText = okText;
        refreshUI();
        return this;
    }
    
    public BottomDialog setOkButton(int OkTextResId) {
        this.okText = getString(OkTextResId);
        refreshUI();
        return this;
    }
    
    public BottomDialog setOkButton(OnDialogButtonClickListener<BottomDialog> OkButtonClickListener) {
        this.okButtonClickListener = OkButtonClickListener;
        return this;
    }
    
    public BottomDialog setOkButton(CharSequence OkText, OnDialogButtonClickListener<BottomDialog> OkButtonClickListener) {
        this.okText = OkText;
        this.okButtonClickListener = OkButtonClickListener;
        refreshUI();
        return this;
    }
    
    public BottomDialog setOkButton(int OkTextResId, OnDialogButtonClickListener<BottomDialog> OkButtonClickListener) {
        this.okText = getString(OkTextResId);
        this.okButtonClickListener = OkButtonClickListener;
        refreshUI();
        return this;
    }
    
    public CharSequence getOtherButton() {
        return otherText;
    }
    
    public BottomDialog setOtherButton(CharSequence otherText) {
        this.otherText = otherText;
        refreshUI();
        return this;
    }
    
    public BottomDialog setOtherButton(int OtherTextResId) {
        this.otherText = getString(OtherTextResId);
        refreshUI();
        return this;
    }
    
    public BottomDialog setOtherButton(OnDialogButtonClickListener<BottomDialog> OtherButtonClickListener) {
        this.otherButtonClickListener = OtherButtonClickListener;
        return this;
    }
    
    public BottomDialog setOtherButton(CharSequence OtherText, OnDialogButtonClickListener<BottomDialog> OtherButtonClickListener) {
        this.otherText = OtherText;
        this.otherButtonClickListener = OtherButtonClickListener;
        refreshUI();
        return this;
    }
    
    public BottomDialog setOtherButton(int OtherTextResId, OnDialogButtonClickListener<BottomDialog> OtherButtonClickListener) {
        this.otherText = getString(OtherTextResId);
        this.otherButtonClickListener = OtherButtonClickListener;
        refreshUI();
        return this;
    }
    
    public BottomDialog setMaskColor(@ColorInt int maskColor) {
        this.maskColor = maskColor;
        refreshUI();
        return this;
    }
    
    public long getEnterAnimDuration() {
        return enterAnimDuration;
    }
    
    public BottomDialog setEnterAnimDuration(long enterAnimDuration) {
        this.enterAnimDuration = enterAnimDuration;
        return this;
    }
    
    public long getExitAnimDuration() {
        return exitAnimDuration;
    }
    
    public BottomDialog setExitAnimDuration(long exitAnimDuration) {
        this.exitAnimDuration = exitAnimDuration;
        return this;
    }
    
    @Override
    public void restartDialog() {
        if (dialogView != null) {
            dismiss(dialogView);
            isShow = false;
        }
        if (getDialogImpl().boxCustom != null) {
            getDialogImpl().boxCustom.removeAllViews();
        }
        if (getDialogImpl().boxList != null) {
            getDialogImpl().boxList.removeAllViews();
        }
        int layoutId = isLightTheme() ? R.layout.layout_dialogx_bottom_material : R.layout.layout_dialogx_bottom_material_dark;
        if (style.overrideBottomDialogRes() != null) {
            layoutId = style.overrideBottomDialogRes().overrideDialogLayout(isLightTheme());
        }
        
        enterAnimDuration = 0;
        dialogView = createView(layoutId);
        dialogImpl = new DialogImpl(dialogView);
        if (dialogView != null) dialogView.setTag(me);
        show(dialogView);
    }
    
    public void hide() {
        if (getDialogView() != null) {
            getDialogView().setVisibility(View.GONE);
        }
    }
    
    @Override
    protected void shutdown() {
        dismiss();
    }
    
    public float getBottomDialogMaxHeight() {
        return bottomDialogMaxHeight;
    }
    
    public BottomDialog setBottomDialogMaxHeight(float bottomDialogMaxHeight) {
        this.bottomDialogMaxHeight = bottomDialogMaxHeight;
        return this;
    }
    
    public BottomDialog setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        refreshUI();
        return this;
    }
    
    public BottomDialog setDialogImplMode(DialogX.IMPL_MODE dialogImplMode) {
        this.dialogImplMode = dialogImplMode;
        return this;
    }
}
