package dev.lukel.familymap.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import dev.lukel.familymap.R;
import dev.lukel.familymap.net.NetException;
import dev.lukel.familymap.net.ServerProxy;
import dev.lukel.familymap.net.request.ClientLoginRequest;
import dev.lukel.familymap.net.request.LoginRequest;
import dev.lukel.familymap.net.response.LoginResponse;

public class LoginFragment extends Fragment {

    private final String TAG = "LOGIN_FRAGMENT";
    private EditText serverHost;
    private EditText serverPort;
    private EditText username;
    private EditText password;
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private RadioGroup genderSelection;
    private String gender;
    private Button loginButton;
    private Button registerButton;
    private boolean loginRequest;
    private boolean registerRequest;
    TextView loginResultText;

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
        genderSelection = (RadioGroup) v.findViewById(R.id.button_gender);
        loginButton = (Button) v.findViewById(R.id.button_login);
        registerButton = (Button) v.findViewById(R.id.button_register);
        loginResultText = (TextView) v.findViewById(R.id.login_result_text);

        serverHost.addTextChangedListener(textWatcher);
        serverPort.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        firstname.addTextChangedListener(textWatcher);
        lastname.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        String s = "login result: ";
        loginResultText.setText(s);

        genderSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                switch (checkedId) {
                    case R.id.button_male:
                        if (isChecked) {
                            gender = "m";
                            Log.i(TAG, "male");
                        }
                        break;
                    case R.id.button_female:
                            if (isChecked) {
                                gender = "m";
                                Log.i(TAG, "female");
                            }
                        break;
                    default:
                        Log.i(TAG, "gender huh?");
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRequest = true;
                registerRequest = false;
                Log.i(TAG, "loginRequest=true");
                Log.i(TAG, "sending login request...");
                Toast.makeText(getActivity(), "sending login request...", Toast.LENGTH_SHORT).show();
                // 10.0.2.2 is how to access localhost within the android vm
                ServerProxy proxy = new ServerProxy("10.0.2.2", "8080");
                LoginRequest request = getClientLoginRequest().convertToLoginRequest();
                LoginResponse response = null;

                new LoginTask(getActivity()).execute(request);
//                new LoginTask(getActivity(), getActivity().findViewById(R.id.login_result_text)).execute(request);



//                try {
//                    response = proxy.login(request);
//                } catch (Exception e) {
//                    Log.e(TAG, ("error: " + e.getMessage()));
//                    return;
//                }
//                Log.i(TAG, "response:\n" + response.toString());
//                Toast.makeText(getActivity(), "response: " + response.toString(), Toast.LENGTH_LONG).show();

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerRequest = true;
                loginRequest = false;
                Log.i(TAG, "registerRequest=true");
                Log.i(TAG, "sending register request...");
                Toast.makeText(getActivity(), "sending register request...", Toast.LENGTH_SHORT).show();
                onSubmitRequest();
            }
        });

        return v;
    }


    private class LoginTask extends AsyncTask<LoginRequest, Void, LoginResponse> {
        Activity context;
        public LoginTask(Activity context) {
            this.context = context;
        }
        @Override
        protected LoginResponse doInBackground(LoginRequest... params) {
            LoginRequest request = params[0];
            try {
                ServerProxy proxy = new ServerProxy("10.0.2.2", "8080");
                return proxy._login(request);
            } catch (NetException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(LoginResponse response) {
            Log.i(TAG, "on post execute." + "\n" + response.toString());
            loginResultText.setText("login response: " + response.toString());
        }
    }


    private void onSubmitRequest() {
        ClientLoginRequest request = getClientLoginRequest();
        Log.i(TAG, request.toString());
        if (loginRequest) {
            if (!checkValidLogin(request)) {
                Log.i(TAG, "invalid login request!");
                Toast.makeText(getActivity(), "invalid login request!", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "valid login request!");
            }
        }
        // register request
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //
        }
        @Override
        public void afterTextChanged(Editable s) {
            //
        }
    };

    private ClientLoginRequest getClientLoginRequest() {
        ClientLoginRequest request = new ClientLoginRequest();
        request.setServerHost(serverHost.getText().toString());
        request.setServerPort(serverPort.getText().toString());
        request.setUsername(username.getText().toString());
        request.setPassword(password.getText().toString());
        request.setFirstname(firstname.getText().toString());
        request.setLastname(lastname.getText().toString());
        request.setEmail(email.getText().toString());
        request.setGender(gender);
        if (registerRequest) {
            // TODO
        }
        return request;
    }

    private boolean checkValidLogin(ClientLoginRequest request) {
        return (loginRequest && !"".equals(request.getUsername()) && !"".equals(request.getPassword())
                && !"".equals(request.getServerHost()) && !"".equals(request.getServerPort()));
    }

    private boolean checkValidRegister() {
        return  (registerRequest && serverHost != null && serverPort != null && username != null
                && password != null && firstname != null && lastname != null && email != null
                && (gender.contains("m") || gender.contains("f")));
    }




}
