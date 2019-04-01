
package com.viomi.kettle.view;

import com.viomi.kettle.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

public class UMCustomDialog extends Dialog implements DialogInterface {
    private UMAlertController mAlert;

    protected UMCustomDialog(Context context) {
        this(context, R.style.CustomDialog);
    }

    protected UMCustomDialog(Context context, int theme) {
        super(context, theme);
        mAlert = new UMAlertController(context, this, getWindow());

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AnimationDialog);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }
    
    public void setPicker(int number){
    	 mAlert.setPicker(number);
    }
    
    public int getPicker(){
    	return mAlert.getPicker();
    }

    /**
     * Gets one of the buttons used in the dialog.
     * <p>
     * If a button does not exist in the dialog, null will be returned.
     * 
     * @param whichButton The identifier of the button that should be returned.
     *            For example, this can be
     *            {@link DialogInterface#BUTTON_POSITIVE},
     *            {@link DialogInterface#BUTTON_NEGATIVE}.
     * @return The button from the dialog, or null if a button does not exist.
     */
    public Button getButton(int whichButton) {
        return mAlert.getButton(whichButton);
    }

    @Override
    public void setTitle(CharSequence title) {
        mAlert.setTitle(title);
    }

    public void setMessage(CharSequence message) {
        mAlert.setMessage(message);
    }

    /**
     * Set the view to display in that dialog.
     */
    public void setView(View view) {
        mAlert.setView(view);
    }

    /**
     * Set the view to display in that dialog, specifying the spacing to appear
     * around that view.
     * 
     * @param view The view to show in the content area of the dialog
     * @param viewSpacingLeft Extra space to appear to the left of {@code view}
     * @param viewSpacingTop Extra space to appear above {@code view}
     * @param viewSpacingRight Extra space to appear to the right of
     *            {@code view}
     * @param viewSpacingBottom Extra space to appear below {@code view}
     */
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight,
            int viewSpacingBottom) {
        mAlert.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom);
    }

    /**
     * Set a message to be sent when a button is pressed.
     * 
     * @param whichButton Which button to set the message for, can be one of
     *            {@link DialogInterface#BUTTON_POSITIVE},
     *            {@link DialogInterface#BUTTON_NEGATIVE}, or
     *            {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text The text to display in positive button.
     * @param msg The {@link Message} to be sent when clicked.
     */
    public void setButton(int whichButton, CharSequence text, Message msg) {
        mAlert.setButton(whichButton, text, null, msg);
    }

    /**
     * Set a listener to be invoked when the positive button of the dialog is
     * pressed.
     * 
     * @param whichButton Which button to set the listener on, can be one of
     *            {@link DialogInterface#BUTTON_POSITIVE},
     *            {@link DialogInterface#BUTTON_NEGATIVE}, or
     *            {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text The text to display in positive button.
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     */
    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        mAlert.setButton(whichButton, text, listener, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlert.installContent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAlert.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mAlert.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public static class Builder {
        private final UMAlertController.AlertParams P;

        /**
         * Constructor using a context for this builder and the
         * {@link UMCustomDialog} it creates.
         */
        public Builder(Context context) {
            P = new UMAlertController.AlertParams(context);
        }

        /**
         * Set the title using the given resource id.
         * 
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setTitle(int titleId) {
            P.mTitle = P.mContext.getText(titleId);
            return this;
        }

        /**
         * Set the title displayed in the {@link Dialog}.
         * 
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setTitle(CharSequence title) {
            P.mTitle = title;
            return this;
        }

        /**
         * Set the message to display using the given resource id.
         * 
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setMessage(int messageId) {
            P.mMessage = P.mContext.getText(messageId);
            return this;
        }

        /**
         * Set the message to display.
         * 
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setMessage(CharSequence message) {
            P.mMessage = message;
            return this;
        }
        

        /**
         * Set a listener to be invoked when the positive button of the dialog
         * is pressed.
         * 
         * @param textId The resource id of the text to display in the positive
         *            button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         * 
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setPositiveButton(int textId, final OnClickListener listener) {
            P.mPositiveButtonText = P.mContext.getText(textId);
            P.mPositiveButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the positive button of the dialog
         * is pressed.
         * 
         * @param text The text to display in the positive button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         * 
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
            P.mPositiveButtonText = text;
            P.mPositiveButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog
         * is pressed.
         * 
         * @param textId The resource id of the text to display in the negative
         *            button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         * 
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setNegativeButton(int textId, final OnClickListener listener) {
            P.mNegativeButtonText = P.mContext.getText(textId);
            P.mNegativeButtonListener = listener;
            return this;
        }

        /**
         * Set a listener to be invoked when the negative button of the dialog
         * is pressed.
         * 
         * @param text The text to display in the negative button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         * 
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
            P.mNegativeButtonText = text;
            P.mNegativeButtonListener = listener;
            return this;
        }

        /**
         * Sets whether the dialog is cancelable or not default is true.
         * 
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }

        /**
         * Sets the callback that will be called if the dialog is canceled.
         * 
         * @see #setCancelable(boolean)
         * 
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }

        /**
         * Sets the callback that will be called if a key is dispatched to the
         * dialog.
         * 
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }

        /**
         * Set a custom view to be the contents of the Dialog. If the supplied
         * view is an instance of a {@link ListView} the light background will
         * be used.
         * 
         * @param view The view to use as the contents of the Dialog.
         * 
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         */
        public Builder setView(View view) {
            P.mView = view;
            P.mViewSpacingSpecified = false;
            return this;
        }

        /**
         * Set a custom view to be the contents of the Dialog, specifying the
         * spacing to appear around that view. If the supplied view is an
         * instance of a {@link ListView} the light background will be used.
         * 
         * @param view The view to use as the contents of the Dialog.
         * @param viewSpacingLeft Spacing between the left edge of the view and
         *            the dialog frame
         * @param viewSpacingTop Spacing between the top edge of the view and
         *            the dialog frame
         * @param viewSpacingRight Spacing between the right edge of the view
         *            and the dialog frame
         * @param viewSpacingBottom Spacing between the bottom edge of the view
         *            and the dialog frame
         * @return This Builder object to allow for chaining of calls to set
         *         methods
         * 
         * 
         *         This is currently hidden because it seems like people should
         *         just be able to put padding around the view.
         */
        public Builder setView(View view, int viewSpacingLeft,
                int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
            P.mView = view;
            P.mViewSpacingSpecified = true;
            P.mViewSpacingLeft = viewSpacingLeft;
            P.mViewSpacingTop = viewSpacingTop;
            P.mViewSpacingRight = viewSpacingRight;
            P.mViewSpacingBottom = viewSpacingBottom;
            return this;
        }

        /**
         * Creates a {@link UMCustomDialog} with the arguments supplied to this
         * builder. It does not {@link Dialog#show()} the dialog. This allows
         * the user to do any extra processing before displaying the dialog. Use
         * {@link #show()} if you don't have any other processing to do and want
         * this to be created and displayed.
         */
        public UMCustomDialog create() {
            final UMCustomDialog dialog = new UMCustomDialog(P.mContext);
            P.apply(dialog.mAlert);
            dialog.setCancelable(P.mCancelable);
            dialog.setOnCancelListener(P.mOnCancelListener);
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;
        }

        /**
         * Creates a {@link UMCustomDialog} with the arguments supplied to this
         * builder and {@link Dialog#show()}'s the dialog.
         */
        public UMCustomDialog show() {
            UMCustomDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

}
