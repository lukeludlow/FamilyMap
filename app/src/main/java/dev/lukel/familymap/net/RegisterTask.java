package dev.lukel.familymap.net;

import android.os.AsyncTask;

import dev.lukel.familymap.net.request.RegisterRequest;
import dev.lukel.familymap.net.response.RegisterResponse;

public class RegisterTask extends AsyncTask<RegisterRequest, Void, RegisterResponse> {

    public interface RegisterAsyncListener {
        void registerComplete(RegisterResponse response);
    }

    private RegisterAsyncListener listener;

    public RegisterTask(RegisterAsyncListener listener) {
        this.listener = listener;
    }

    @Override
    protected RegisterResponse doInBackground(RegisterRequest... params) {
        RegisterRequest request = params[0];
        RegisterResponse response;
        ServerProxy proxy = new ServerProxy("10.0.2.2", "8080");
        try {
            response = proxy.register(request);
        } catch (NetException e) {
            response = null;
        }
        return response;
    }

    @Override
    protected void onPostExecute(RegisterResponse response) {
        listener.registerComplete(response);
    }


}
