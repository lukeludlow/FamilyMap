package dev.lukel.familymap.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dev.lukel.familymap.net.message.EventsRequest;
import dev.lukel.familymap.net.message.PeopleRequest;
import dev.lukel.familymap.net.message.LoginRequest;
import dev.lukel.familymap.net.message.RegisterRequest;
import dev.lukel.familymap.net.message.EventsResponse;
import dev.lukel.familymap.net.message.PeopleResponse;
import dev.lukel.familymap.net.message.LoginResponse;
import dev.lukel.familymap.net.message.RegisterResponse;
import lombok.Data;

@Data
public class ServerProxy {

    private final String TAG = "SERVER_PROXY";

    private String host;
    private String port;
    private NetException error;

    public ServerProxy(String host, String port) {
        this.host = host;
        this.port = port;
    }

    public LoginResponse login(LoginRequest request) throws NetException {
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

    public EventsResponse getEvents(EventsRequest request) throws NetException {
        try {
            URL url = new URL("http://" + host + ":" + port + "/event");
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoOutput(false);
            httpConnection.addRequestProperty("Authorization", request.getAuthToken());
            httpConnection.connect();
            if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new NetException("get events failed: http response code not ok");
            }
            EventsResponse response;
            response = Encoder.deserialize(readResponseBody(httpConnection), EventsResponse.class);
            return response;
        } catch (IOException e) {
            throw new NetException("get events failed. " + e.getMessage());
        }
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
