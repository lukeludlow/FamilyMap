package dev.lukel.familymap.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import dev.lukel.familymap.R;
import dev.lukel.familymap.model.LoginRequest;

public class LoginFragment extends Fragment {

    private final String TAG = "LOGIN_FRAGMENT";
    private EditText serverHost;
    private EditText serverPort;
    private EditText username;
    private EditText password;
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private RadioButton male;
    private RadioButton female;
    private boolean loginRequest;
    private boolean registerRequest;

    public LoginFragment() {
        //
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        serverHost = (EditText) v.findViewById(R.id.server_host_edit_text);
        serverPort = (EditText) v.findViewById(R.id.server_port_edit_text);
        username = (EditText) v.findViewById(R.id.username_edit_text);
        password = (EditText) v.findViewById(R.id.password_edit_text);
        firstname = (EditText) v.findViewById(R.id.firstname_edit_text);
        lastname = (EditText) v.findViewById(R.id.lastname_edit_text);
        email = (EditText) v.findViewById(R.id.email_edit_text);
        male = (RadioButton) v.findViewById(R.id.button_male);
        female = (RadioButton) v.findViewById(R.id.button_female);

        serverHost.addTextChangedListener(textWatcher);


        return v;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.i(TAG, "onTextChanged");
            Log.i(TAG, s.toString());
        }
        @Override
        public void afterTextChanged(Editable s) {
            //
        }
    };

    public LoginRequest getLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setServerHost(serverHost.getText().toString());
        request.setServerPort(serverPort.getText().toString());
        request.setUsername(username.getText().toString());
        request.setPassword(password.getText().toString());
        request.setFirstname(firstname.getText().toString());
        request.setEmail(email.getText().toString());
        // male or female?
        // login or register?
        return request;
    }


}
