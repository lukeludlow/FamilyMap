package dev.lukel.familymap.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dev.lukel.familymap.net.request.LoginRequest;
import dev.lukel.familymap.net.response.LoginResponse;

public class ServerProxy {

    private String host;
    private String port;

    public ServerProxy(String host, String port) {
        this.host = host;
        this.port = port;
    }

    // TODO handle exceptions properly
    public LoginResponse login(LoginRequest request) throws Exception {

        URL url = new URL("http://" + host + ":" + port + "/user/login");
        HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
        httpConnection.setRequestMethod("POST");
        httpConnection.setDoOutput(true); // there is a request body
        httpConnection.connect();
        String loginInfo = Encoder.serialize(request);
        OutputStream requestBody = httpConnection.getOutputStream();
        requestBody.write(loginInfo.getBytes());
        requestBody.close();

        LoginResponse response;
        if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            response = Encoder.deserialize(readResponseBody(httpConnection), LoginResponse.class);
            System.out.println("response:");
            System.out.println(response.toString());
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
