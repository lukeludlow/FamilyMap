package dev.lukel.familymap.net;

import android.os.AsyncTask;

import dev.lukel.familymap.net.request.LoginRequest;
import dev.lukel.familymap.net.response.LoginResponse;

public class LoginTask extends AsyncTask<LoginRequest, Void, LoginResponse> {

   public interface LoginAsyncListener {
       void loginComplete(LoginResponse response);
   }

   private LoginAsyncListener delegate;

   public LoginTask(LoginAsyncListener delegate) {
       this.delegate = delegate;
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
       delegate.loginComplete(response);
   }

}
