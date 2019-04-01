
package com.viomi.kettle.view;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.R.integer;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.viomi.kettle.R;


public class UMAlertController {

    private final DialogInterface mDialogInterface;
    private final Window mWindow;

    private CharSequence mTitle;

    private CharSequence mMessage;

    private View mView;

    private int mViewSpacingLeft;
    private int mViewSpacingTop;
    private int mViewSpacingRight;
    private int mViewSpacingBottom;

    private boolean mViewSpacingSpecified = false;

    private Button mButtonPositive;
    private CharSequence mButtonPositiveText;
    private Message mButtonPositiveMessage;

    private Button mButtonNegative;
    private CharSequence mButtonNegativeText;
    private Message mButtonNegativeMessage;

    private ScrollView mScrollView;
    private TextView mMessageView;
    private TextView mTitleView;
    
	UMPickerView minute_pv;
	UMPickerView second_pv;

    private Handler mHandler;

    View.OnClickListener mButtonHandler = new View.OnClickListener() {

        public void onClick(View v) {
            Message m = null;
            if (v == mButtonPositive && mButtonPositiveMessage != null) {
                m = Message.obtain(mButtonPositiveMessage);
            } else if (v == mButtonNegative && mButtonNegativeMessage != null) {
                m = Message.obtain(mButtonNegativeMessage);
            }
            if (m != null) {
                m.sendToTarget();
            }

            // Post a message so we dismiss after the above handlers are
            // executed
            mHandler.obtainMessage(ButtonHandler.MSG_DISMISS_DIALOG, mDialogInterface)
                    .sendToTarget();
        }

    };

    private static final class ButtonHandler extends Handler {
        // Button clicks have Message.what as the BUTTON{1,2,3} constant
        private static final int MSG_DISMISS_DIALOG = 1;

        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialog) {
            mDialog = new WeakReference<DialogInterface>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case DialogInterface.BUTTON_POSITIVE:
                case DialogInterface.BUTTON_NEGATIVE:
                case DialogInterface.BUTTON_NEUTRAL:
                    ((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(), msg.what);
                    break;

                case MSG_DISMISS_DIALOG:
                    ((DialogInterface) msg.obj).dismiss();
            }
        }
    }

    public UMAlertController(Context context, DialogInterface di, Window window) {
        mDialogInterface = di;
        mWindow = window;
        mHandler = new ButtonHandler(di);
    }

    static boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }

        if (!(v instanceof ViewGroup)) {
            return false;
        }

        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            v = vg.getChildAt(i);
            if (canTextInput(v)) {
                return true;
            }
        }

        return false;
    }

    public void installContent() {
        /** We use a custom title so never request a window title */
        mWindow.requestFeature(Window.FEATURE_NO_TITLE);

        if (mView == null || !canTextInput(mView)) {
            mWindow.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        // mWindow.setContentView(R.layout.alert_dialog);
        mWindow.setContentView(R.layout.um_custom_dialog);
        setupView();
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
    }

    public void setMessage(CharSequence message) {
        mMessage = message;
        if (mMessageView != null) {
            mMessageView.setText(message);
        }
    }
    


    /***
     * Set the view to display in the dialog.
     */
    public void setView(View view) {
        mView = view;
        mViewSpacingSpecified = false;
    }

    /***
     * Set the view to display in the dialog along with the spacing around that
     * view
     */
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight,
            int viewSpacingBottom) {
        mView = view;
        mViewSpacingSpecified = true;
        mViewSpacingLeft = viewSpacingLeft;
        mViewSpacingTop = viewSpacingTop;
        mViewSpacingRight = viewSpacingRight;
        mViewSpacingBottom = viewSpacingBottom;
    }

    /***
     * Sets a click listener or a message to be sent when the button is clicked.
     * You only need to pass one of {@code listener} or {@code msg}.
     * 
     * @param whichButton Which button, can be one of
     *            {@link DialogInterface#BUTTON_POSITIVE},
     *            {@link DialogInterface#BUTTON_NEGATIVE}, or
     *            {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text The text to display in positive button.
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @param msg The {@link Message} to be sent when clicked.
     */
    public void setButton(int whichButton, CharSequence text,
            DialogInterface.OnClickListener listener, Message msg) {

        if (msg == null && listener != null) {
            msg = mHandler.obtainMessage(whichButton, listener);
        }

        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                mButtonPositiveText = text;
                mButtonPositiveMessage = msg;
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                mButtonNegativeText = text;
                mButtonNegativeMessage = msg;
                break;

            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }

    public Button getButton(int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                return mButtonPositive;
            case DialogInterface.BUTTON_NEGATIVE:
                return mButtonNegative;
            default:
                return null;
        }
    }

    // @SuppressWarnings({ "UnusedDeclaration" })
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mScrollView != null && mScrollView.executeKeyEvent(event);
    }

    // @SuppressWarnings({ "UnusedDeclaration" })
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mScrollView != null && mScrollView.executeKeyEvent(event);
    }
    private void setupView() {
        LinearLayout topPanel = (LinearLayout) mWindow.findViewById(R.id.topPanel);
        setupTitle(topPanel);

        LinearLayout contentPanel = (LinearLayout) mWindow.findViewById(R.id.contentPanel);
        contentPanel.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 0, 1.0f));
        setupContent(contentPanel);

        LinearLayout buttonPanel = (LinearLayout) mWindow.findViewById(R.id.buttonPanel);
        setupButtons(buttonPanel);
        
        RelativeLayout pickerPanel = (RelativeLayout) mWindow.findViewById(R.id.picker);
        setupPicker(pickerPanel);
      
        if (mView != null) {
            FrameLayout custom = (FrameLayout) mWindow.findViewById(R.id.custom);
            custom.addView(mView, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
            if (mViewSpacingSpecified) {
                custom.setPadding(mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight,
                        mViewSpacingBottom);
            }
        } else {
            mWindow.findViewById(R.id.customPanel).setVisibility(View.GONE);
        }
        

    }

    private boolean setupTitle(LinearLayout topPanel) {
        boolean hasTitle = true;
        final boolean hasTextTitle = !TextUtils.isEmpty(mTitle);

        if (hasTextTitle) {
            mTitleView = (TextView) topPanel.findViewById(R.id.title);
            mTitleView.setText(mTitle);
        } else {
            topPanel.setVisibility(View.GONE);
            hasTitle = false;
        }
        return hasTitle;
    }
    private void setupContent(LinearLayout contentPanel) {
        mScrollView = (ScrollView) contentPanel.findViewById(R.id.scrollView);
        mScrollView.setFocusable(false);

        mMessageView = (TextView) contentPanel.findViewById(R.id.message);
        if (mMessageView == null) {
            return;
        }
        if (mMessage != null) {
            mMessageView.setText(mMessage);
        } else {
            mMessageView.setVisibility(View.GONE);
            mScrollView.removeView(mMessageView);
        }
    }
    
    private void setupPicker(RelativeLayout pickerPanel) {

		minute_pv = (UMPickerView) pickerPanel.findViewById(R.id.minute_pv);
		second_pv = (UMPickerView) pickerPanel.findViewById(R.id.second_pv);
		List<String> data = new ArrayList<String>();
		List<String> seconds = new ArrayList<String>();
		for (int i = 0; i < 10; i++)
		{
			//data.add("0" + i);
			data.add(""+i);
		}
		for (int i = 0; i < 10; i++)
		{
			//seconds.add(i < 10 ? "0" + i : "" + i);
			seconds.add(""+i);
		}
		minute_pv.setData(data);
		second_pv.setData(seconds);
		
    }
    
    public void setPicker(int number) {
    	int ten,uint;
    	if(number<0){
    		number=0;
    	}
    	ten=number/10;
    	uint=number%10;
		minute_pv.setSelected(ten);
		second_pv.setSelected(uint);
	}
    
    public int getPicker(){
    	int ten=0,uint=0;
    	try {
       	 ten=Integer.parseInt(minute_pv.getSelected());
       	 uint=Integer.parseInt(second_pv.getSelected());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return ten*10+uint;

    }

    private void setupButtons(LinearLayout buttonPanel) {
        final int BIT_BUTTON_POSITIVE = 1;
        final int BIT_BUTTON_NEGATIVE = 2;
        int whichButtons = 0;

        mButtonPositive = (Button) buttonPanel.findViewById(R.id.button1);
        mButtonPositive.setOnClickListener(mButtonHandler);
        if (TextUtils.isEmpty(mButtonPositiveText)) {
            mButtonPositive.setVisibility(View.GONE);
        } else {
            mButtonPositive.setText(mButtonPositiveText);
            mButtonPositive.setVisibility(View.VISIBLE);
            whichButtons = whichButtons | BIT_BUTTON_POSITIVE;
        }

        mButtonNegative = (Button) mWindow.findViewById(R.id.button2);
        mButtonNegative.setOnClickListener(mButtonHandler);
        if (TextUtils.isEmpty(mButtonNegativeText)) {
            mButtonNegative.setVisibility(View.GONE);
        } else {
            mButtonNegative.setText(mButtonNegativeText);
            mButtonNegative.setVisibility(View.VISIBLE);
            whichButtons = whichButtons | BIT_BUTTON_NEGATIVE;
        }

        if (whichButtons == 0) {
            buttonPanel.setVisibility(View.GONE);
            return;
        }

        switch (whichButtons) {
            case BIT_BUTTON_POSITIVE:
                if (mButtonPositive.getVisibility() == View.VISIBLE) {
                    mButtonPositive.setBackgroundResource(R.drawable.selector_popup_btn_single);
                }
                break;
            case BIT_BUTTON_NEGATIVE:
                if (mButtonNegative.getVisibility() == View.VISIBLE) {
                    mButtonNegative.setBackgroundResource(R.drawable.selector_popup_btn_single);
                }
                break;
            case BIT_BUTTON_POSITIVE + BIT_BUTTON_NEGATIVE:
                if (mButtonPositive.getVisibility() == View.VISIBLE) {
                    mButtonPositive.setBackgroundResource(R.drawable.selector_popup_btn_last);
                }
                if (mButtonNegative.getVisibility() == View.VISIBLE) {
                    mButtonNegative.setBackgroundResource(R.drawable.selector_popup_btn_frist);
                }
                break;
        }
    }
    public static class AlertParams {
        public final Context mContext;

        public CharSequence mTitle;
        public CharSequence mMessage;
        public CharSequence mPositiveButtonText;
        public DialogInterface.OnClickListener mPositiveButtonListener;
        public CharSequence mNegativeButtonText;
        public DialogInterface.OnClickListener mNegativeButtonListener;
        public boolean mCancelable;
        public DialogInterface.OnCancelListener mOnCancelListener;
        public DialogInterface.OnKeyListener mOnKeyListener;
        public DialogInterface.OnClickListener mOnClickListener;
        public View mView;
        public int mViewSpacingLeft;
        public int mViewSpacingTop;
        public int mViewSpacingRight;
        public int mViewSpacingBottom;
        public boolean mViewSpacingSpecified = false;

        public AlertParams(Context context) {
            mContext = context;
            mCancelable = true;
        }

        public void apply(UMAlertController dialog) {
            if (mTitle != null) {
                dialog.setTitle(mTitle);
            }
            if (mMessage != null) {
                dialog.setMessage(mMessage);
            }
            if (mPositiveButtonText != null) {
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, mPositiveButtonText,
                        mPositiveButtonListener, null);
            }
            if (mNegativeButtonText != null) {
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mNegativeButtonText,
                        mNegativeButtonListener, null);
            }
            // addView的实现  
            if (mView != null) {  
                if (mViewSpacingSpecified) {  
                    dialog.setView(mView, mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight,  
                            mViewSpacingBottom);  
                } else {  
                    dialog.setView(mView);  
                }  
            }  
            /**
             * dialog.setCancelable(mCancelable);
             * dialog.setOnCancelListener(mOnCancelListener); if (mOnKeyListener
             * != null) { dialog.setOnKeyListener(mOnKeyListener); }
             */
        }

    }
 

}
