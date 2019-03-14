package dev.lukel.familymap.net;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import dev.lukel.familymap.BuildConfig;
import dev.lukel.familymap.net.request.EventsRequest;
import dev.lukel.familymap.net.request.PeopleRequest;
import dev.lukel.familymap.net.request.LoginRequest;
import dev.lukel.familymap.net.request.RegisterRequest;
import dev.lukel.familymap.net.response.AsyncLoginResponse;
import dev.lukel.familymap.net.response.EventsResponse;
import dev.lukel.familymap.net.response.PeopleResponse;
import dev.lukel.familymap.net.response.LoginResponse;
import dev.lukel.familymap.net.response.RegisterResponse;
import lombok.Data;

@Data
public class ServerProxy {

    private final String TAG = "SERVER_PROXY";

    private String host;
    private String port;
    private LoginResponse loginResponse; // used by async task on post execute
    private NetException error;

    public ServerProxy(String host, String port) {
        this.host = host;
        this.port = port;
    }

//    public LoginResponse login(LoginRequest request) throws NetException {
//        try {
//            new LoginTask().execute(request);
//        } catch (NullPointerException e) {
//            throw new NetException("login failed. " + e.getMessage() + ". " + e.getClass());
//        }
//        return null;
//    }


    // TODO handle exceptions properly
    public LoginResponse _login(LoginRequest request) throws NetException {
        try {
            URL url = new URL("http://" + host + ":" + port + "/user/login");
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true); // there is a request body
            httpConnection.connect();
            String loginInfo = Encoder.serialize(request);
            OutputStream requestBody = httpConnection.getOutputStream();
            requestBody.write(loginInfo.getBytes());
            requestBody.close();
            LoginResponse response;
            if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new NetException("login failed: http response code not ok");
            }
            response = Encoder.deserialize(readResponseBody(httpConnection), LoginResponse.class);
            System.out.println("response:");
            System.out.println(response.toString());
            return response;
        } catch (IOException e) {
            throw new NetException("login failed. " + e.getMessage());
        }
    }

    public RegisterResponse register(RegisterRequest request) throws NetException {
        try {
            URL url = new URL("http://" + host + ":" + port + "/user/register");
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true); // there is a request body
            httpConnection.connect();
            String loginInfo = Encoder.serialize(request);
            OutputStream requestBody = httpConnection.getOutputStream();
            requestBody.write(loginInfo.getBytes());
            requestBody.close();
            if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new NetException("login failed: http response code not ok");
            }
            RegisterResponse response;
            response = Encoder.deserialize(readResponseBody(httpConnection), RegisterResponse.class);
            System.out.println("response:");
            System.out.println(response.toString());
            return response;
        } catch (IOException e) {
            throw new NetException("register failed. " + e.getMessage());
        }
    }

    public PeopleResponse getPeople(PeopleRequest request) throws NetException {
        try {
            URL url = new URL("http://" + host + ":" + port + "/person");
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoOutput(false);
            httpConnection.addRequestProperty("Authorization", request.getAuthToken());
            httpConnection.connect();
            if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new NetException("get people failed: http response code not ok");
            }
            PeopleResponse response;
            response = Encoder.deserialize(readResponseBody(httpConnection), PeopleResponse.class);
            return response;
        } catch (IOException e) {
            throw new NetException("get people failed. " + e.getMessage());
        }
    }

    public EventsResponse getEvents(EventsRequest request) throws Exception {

        URL url = new URL("http://" + host + ":" + port + "/event");
        HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.setDoOutput(false);
        httpConnection.addRequestProperty("Authorization", request.getAuthToken());
        httpConnection.connect();

        EventsResponse response;
        if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            response = Encoder.deserialize(readResponseBody(httpConnection), EventsResponse.class);
            return response;
        }

        return null;
    }

    private static String readResponseBody(HttpURLConnection httpConnection) throws IOException {
        InputStream inputStream = httpConnection.getInputStream();
        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bf.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

}
