package com.fras.msbm.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fras.msbm.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Shane on 6/18/2016.
 */
public class LoginDialog extends DialogFragment {
    public static final String TAG = LoginDialog.class.getSimpleName();

    @BindView(R.id.edit_text_username) TextInputEditText mEditTextUsername;
    @BindView(R.id.edit_text_password) TextInputEditText mEditTextPassword;
    @BindView(R.id.button_moodle_login) Button mButtonMoodle;
    @BindView(R.id.button_other) Button mButtonOther;

    private OnLoginDialogInteractionListener mListener;

    public interface OnLoginDialogInteractionListener {
        void onLoginAction(String username, String password);
        void onOtherAction();
    }

    public LoginDialog() {}

    public static LoginDialog newInstance() {
        return new LoginDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_login, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.button_moodle_login)
    public void onMoodleLoginButtonClick(Button button){
        final String username = mEditTextUsername.getText().toString();
        final String password = mEditTextPassword.getText().toString();

        if (username.isEmpty()) {
            mEditTextUsername.setError("Please provide a username");
            return;
        } else if (password.isEmpty()) {
            mEditTextPassword.setError("Please provide a password");
            return;
        }

        mListener.onLoginAction(username, password);
    }

    @OnClick(R.id.button_other)
    public void onOtherButtonClick(Button button){
        mListener.onOtherAction();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginDialogInteractionListener)
            mListener = (OnLoginDialogInteractionListener) context;
        else
            throw new RuntimeException(context.toString()
                    + " must implement " + TAG);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
