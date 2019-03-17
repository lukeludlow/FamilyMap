package dev.lukel.familymap.ui;

import android.app.Activity;
import android.os.AsyncTask;
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
import dev.lukel.familymap.model.Person;
import dev.lukel.familymap.net.NetException;
import dev.lukel.familymap.net.ServerProxy;
import dev.lukel.familymap.net.request.ClientLoginRequest;
import dev.lukel.familymap.net.request.LoginRequest;
import dev.lukel.familymap.net.request.PeopleRequest;
import dev.lukel.familymap.net.request.RegisterRequest;
import dev.lukel.familymap.net.response.LoginResponse;
import dev.lukel.familymap.net.response.PeopleResponse;
import dev.lukel.familymap.net.response.RegisterResponse;

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
                ServerProxy proxy = new ServerProxy("10.0.2.2", "8080");
                LoginRequest request = getClientLoginRequest().convertToLoginRequest();
                LoginResponse response = null;
                new LoginTask(getActivity()).execute(request);
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
                RegisterResponse response = null;
                new RegisterTask(getActivity()).execute(request);
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
            LoginResponse response = null;
            try {
                ServerProxy proxy = new ServerProxy("10.0.2.2", "8080");
                response = proxy._login(request);
            } catch (NetException e) {
                response = null;
            }
            return response;
        }
        @Override
        protected void onPostExecute(LoginResponse response) {
            if (response == null) {
                loginResultText.setText("error: unable to log in.");
                return;
            }
            DataSingleton.setLoginResponse(response);
            PeopleRequest peopleRequest = new PeopleRequest(DataSingleton.getLoginResponse().getAuthToken());
            new GetPeopleTask(getActivity()).execute(peopleRequest);
            loginResultText.setText("login response: " + response.toString());
        }
    }

    private class RegisterTask extends AsyncTask<RegisterRequest, Void, RegisterResponse> {
        Activity context;
        public RegisterTask(Activity context) {
            this.context = context;
        }
        @Override
        protected RegisterResponse doInBackground(RegisterRequest... params) {
            RegisterRequest request = params[0];
            RegisterResponse response = null;
            try {
                ServerProxy proxy = new ServerProxy("10.0.2.2", "8080");
                response = proxy.register(request);
            } catch (NetException e) {
                response = null;
            }
            return response;
        }
        @Override
        protected void onPostExecute(RegisterResponse response) {
            if (response == null) {
                loginResultText.setText("error: unable to register. make sure you've filled in all required fields.");
                return;
            }
            DataSingleton.setRegisterResponse(response);
            PeopleRequest peopleRequest = new PeopleRequest(DataSingleton.getRegisterResponse().getAuthToken());
            new GetPeopleTask(getActivity()).execute(peopleRequest);
            loginResultText.setText("register response: " + response.toString());
        }
    } 
    
    private class GetPeopleTask extends AsyncTask<PeopleRequest, Void, PeopleResponse> {
        Activity context;
        public GetPeopleTask(Activity context) {
            this.context = context;
        }
        @Override
        protected PeopleResponse doInBackground(PeopleRequest... params) {
            PeopleRequest request = params[0];
            PeopleResponse response = null;
            try {
                ServerProxy proxy = new ServerProxy("10.0.2.2", "8080");
                response = proxy.getPeople(request);
            } catch (NetException e) {
                response = null;
            }
            return response;
        }
        @Override
        protected void onPostExecute(PeopleResponse response) {
            if (response == null) {
                return;
            }
            DataSingleton.setPeople(response.getData());
            Log.i(TAG, "searching for first and last name...");
            String id = "";
            if (loginRequest) {
                id = DataSingleton.getLoginResponse().getPersonID();
            } else if (registerRequest) {
                id = DataSingleton.getRegisterResponse().getPersonID();
            }
            String personFirstname = "";
            String personLastname = "";
            for (Person p : DataSingleton.getPeople()) {
                if (p.getPersonID().equals(id)) {
                    personFirstname = p.getFirstName();
                    personLastname = p.getLastName();
                }
            }
            Toast.makeText(getActivity(), "hello, " + personFirstname + " " + personLastname, Toast.LENGTH_LONG).show();
        }
    }

    // TODO find user's person object method


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
