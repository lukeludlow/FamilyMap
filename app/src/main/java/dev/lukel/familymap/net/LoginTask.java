package dev.lukel.familymap.net;

import android.os.AsyncTask;

import dev.lukel.familymap.net.message.LoginRequest;
import dev.lukel.familymap.net.message.LoginResponse;

public class LoginTask extends AsyncTask<LoginRequest, Void, LoginResponse> {

   public interface LoginAsyncListener {
       void loginComplete(LoginResponse response);
   }

   private LoginAsyncListener listener;

   public LoginTask(LoginAsyncListener listener) {
       this.listener = listener;
   }

   @Override
    protected LoginResponse doInBackground(LoginRequest... params) {
       LoginRequest request = params[0];
       LoginResponse response;
       ServerProxy proxy = new ServerProxy("10.0.2.2", "8080");
       try {
           response = proxy.login(request);
       } catch (NetException e) {
           response = null;
       }
       return response;
   }

   @Override
    protected void onPostExecute(LoginResponse response) {
       listener.loginComplete(response);
   }

}
