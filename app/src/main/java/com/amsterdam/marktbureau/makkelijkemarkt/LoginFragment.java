package com.amsterdam.marktbureau.makkelijkemarkt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment {

    // use classname when logging
    private static final String LOG_TAG = LoginFragment.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.account) TextView mAcount;
    @Bind(R.id.password) TextView mPassword;
    @Bind(R.id.login_button) Button mLoginButton;

    /**
     * Constructor
     */
    public LoginFragment() {
    }

    /**
     * Set the login fragment layout and initialize the login logic
     * @param inflater inflater object to inflate the layout
     * @param container the parent view container
     * @param savedInstanceState fragment state bundle
     * @return the inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the login fragment layout
        View mainView =  inflater.inflate(R.layout.login_fragment, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, mainView);

        // disable allcaps for the button title
        mLoginButton.setTransformationMethod(null);

        return mainView;
    }

    /**
     * Authenticate the user
     */
    @OnClick(R.id.login_button)
    public void authenticateAccount() {

        Utility.log(getContext(), LOG_TAG, "authenticateAccount");




    }
}
