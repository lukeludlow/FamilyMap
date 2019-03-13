package dev.lukel.familymap.net;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.lukel.familymap.net.request.LoginRequest;
import dev.lukel.familymap.net.request.RegisterRequest;
import dev.lukel.familymap.net.response.LoginResponse;
import dev.lukel.familymap.net.response.RegisterResponse;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerProxyTest {

    @Test
    void testLogin() throws Exception {
        LoginRequest request = new LoginRequest("lukeludlow", "hunter2");
        LoginResponse expected = new LoginResponse("random_token", "lukeludlow", "random_person_id");
        LoginResponse actual;
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        actual = proxy.login(request);
        // unique token and id are always random, so set them for comparison
        actual.setAuthToken("random_token");
        actual.setPersonID("random_person_id");
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("wrong password")
    void testLoginFail() throws Exception {
        LoginRequest request = new LoginRequest("lukeludlow", "wrong_password");
        LoginResponse actual;
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        actual = proxy.login(request);
        // a bad response code makes the login method return null.
        // TODO better error handling, read error message and response exception stuff.
        assertNull(actual);
    }

    @Test
    @DisplayName("wrong username")
    void testLoginFail2() throws Exception {
        LoginRequest request = new LoginRequest("wrong_username", "hunter2");
        LoginResponse actual;
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        actual = proxy.login(request);
        // a bad response code makes the login method return null.
        // TODO better error handling, read error message and response exception stuff.
        assertNull(actual);
    }



    @Test
    void testRegister() throws Exception {
        RegisterRequest request = new RegisterRequest("lukeludlow", "hunter2",
                                                        "ll@live.com", "luke",
                                                        "ludlow", "m");
        RegisterResponse expected = new RegisterResponse("random_token", "lukeludlow", "random_person_id");
        RegisterResponse actual;
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        actual = proxy.register(request);
        // unique token and id are always random, so set them for comparison
        actual.setAuthToken("random_token");
        actual.setPersonID("random_person_id");
        assertEquals(expected, actual);
    }





    @Test
    void testConstructor() {
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        System.out.println(proxy.getHost() + ":" + proxy.getPort());
    }

    @Test
    void testBuildURL() throws Exception {
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        String expected = "http://" + "localhost:8080" + "/user/login";
        String actual = proxy.buildURL("/user/login").toString();
    }


}