package dev.lukel.familymap.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
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
import dev.lukel.familymap.model.DataSingleton;
import dev.lukel.familymap.net.LoginTask;
import dev.lukel.familymap.net.RegisterTask;
import dev.lukel.familymap.net.ServerProxy;
import dev.lukel.familymap.net.SyncDataTask;
import dev.lukel.familymap.net.request.ClientLoginRequest;
import dev.lukel.familymap.net.request.LoginRequest;
import dev.lukel.familymap.net.request.RegisterRequest;
import dev.lukel.familymap.net.response.LoginResponse;
import dev.lukel.familymap.net.response.RegisterResponse;

public class LoginFragment extends Fragment implements SyncDataTask.SyncDataAsyncListener, LoginTask.LoginAsyncListener, RegisterTask.RegisterAsyncListener {

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
    private TextView loginResultText;

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
        serverHost = v.findViewById(R.id.server_host_edit_text);
        serverPort = v.findViewById(R.id.server_port_edit_text);
        username = v.findViewById(R.id.username_edit_text);
        password = v.findViewById(R.id.password_edit_text);
        firstname = v.findViewById(R.id.firstname_edit_text);
        lastname = v.findViewById(R.id.lastname_edit_text);
        email = v.findViewById(R.id.email_edit_text);
        genderSelection = v.findViewById(R.id.button_gender);
        loginButton = v.findViewById(R.id.button_login);
        registerButton = v.findViewById(R.id.button_register);
        loginResultText = v.findViewById(R.id.login_result_text);
        checkEnableLoginButton();
        checkEnableRegisterButton();

        serverHost.addTextChangedListener(textWatcher);
        serverPort.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        firstname.addTextChangedListener(textWatcher);
        lastname.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        loginResultText.setText("login result: ");

        genderSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                switch (checkedId) {
                    case R.id.button_male:
                        if (isChecked) {
                            gender = "m";
                            Log.i(TAG, "male");
                        }
                        checkEnableRegisterButton();
                        break;
                    case R.id.button_female:
                            if (isChecked) {
                                gender = "m";
                                Log.i(TAG, "female");
                            }
                        checkEnableRegisterButton();
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
                Toast.makeText(getActivity(), "sending login request...", Toast.LENGTH_SHORT).show();
                // 10.0.2.2 accesses localhost within the android vm
                LoginRequest request = getClientLoginRequest().convertToLoginRequest();
                startLoginTask(request);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerRequest = true;
                loginRequest = false;
                Toast.makeText(getActivity(), "sending register request...", Toast.LENGTH_SHORT).show();
                ServerProxy proxy = new ServerProxy("10.0.2.2", "8080");
                RegisterRequest request = getClientLoginRequest().convertToRegisterRequest();
                startRegisterTask(request);
            }
        });

        return v;
    }

    private void startSyncDataTask(String authtoken) {
        new SyncDataTask(this).execute(authtoken);
    }

    private void startLoginTask(LoginRequest request) {
        new LoginTask(this).execute(request);
    }

    private void startRegisterTask(RegisterRequest request) {
        new RegisterTask(this).execute(request);
    }

    @Override
    public void syncDataComplete(String result) {
        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginComplete(LoginResponse response) {
        if (response == null) {
            Toast.makeText(getActivity(), "login failed", Toast.LENGTH_SHORT).show();
            return;
        }
        DataSingleton.setUsername(response.getUserName());
        DataSingleton.setUserPersonID(response.getPersonID());
        DataSingleton.setAuthtoken(response.getAuthToken());
        Toast.makeText(getActivity(), "login success", Toast.LENGTH_SHORT).show();
        startSyncDataTask(response.getAuthToken());
    }

    @Override
    public void registerComplete(RegisterResponse r) {
        Toast.makeText(getActivity(), "register complete!", Toast.LENGTH_SHORT).show();
        loginComplete(new LoginResponse(r.getAuthToken(), r.getUserName(), r.getPersonID()));
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
            checkEnableLoginButton();
            checkEnableRegisterButton();
        }
    };

    private void checkEnableLoginButton() {
        if (TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
            loginButton.setEnabled(false);
            loginButton.setAlpha(0.5f);
        } else {
            loginButton.setEnabled(true);
            loginButton.setAlpha(1.0f);
        }
    }

    private void checkEnableRegisterButton() {
        if (TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString())
            || TextUtils.isEmpty(firstname.getText().toString()) || TextUtils.isEmpty(lastname.getText().toString())
            || TextUtils.isEmpty(email.getText().toString()) || (genderSelection.getCheckedRadioButtonId() == -1)) {
            registerButton.setEnabled(false);
            registerButton.setAlpha(0.5f);
        } else {
            registerButton.setEnabled(true);
            registerButton.setAlpha(1.0f);
        }
    }

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
        return request;
    }

}
