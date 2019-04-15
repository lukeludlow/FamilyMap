package dev.lukel.familymap.net;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import dev.lukel.familymap.net.message.EventsRequest;
import dev.lukel.familymap.net.message.LoginRequest;
import dev.lukel.familymap.net.message.PeopleRequest;
import dev.lukel.familymap.net.message.RegisterRequest;
import dev.lukel.familymap.net.message.EventsResponse;
import dev.lukel.familymap.net.message.LoginResponse;
import dev.lukel.familymap.net.message.PeopleResponse;
import dev.lukel.familymap.net.message.RegisterResponse;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerProxyTest {

    Process serverProcess;

    @Test
    void startServer() throws Exception {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("java", "-jar", "out/artifacts/fm_server_jar/fm_server.jar", "8080");
        pb.directory(new File("/Users/luke/code/fm_server/"));
        Process serverProcess = pb.start();
        Thread.sleep(10000);
        serverProcess.destroy();
        // to check whether server is running properly, do
        // ps -A | grep fm_server
        // Thread.sleep(10000) to let server run for 10 seconds
        // if process isn't killed properly, do
        // pkill -9 -f fm_server
    }






    @Test
    void testLogin() throws Exception {
        LoginRequest request = new LoginRequest("lukeludlow", "hunter2");
        LoginResponse expected = new LoginResponse("random_token", "lukeludlow", "random_person_id");
        LoginResponse actual;
        ServerProxy proxy = new ServerProxy("localhost", "8080");
//        actual = proxy.login(request);
        // unique token and id are always random, so set them for comparison
//        actual.setAuthToken("random_token");
//        actual.setPersonID("random_person_id");
//        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("wrong password")
    void testLoginFail() throws Exception {
        LoginRequest request = new LoginRequest("lukeludlow", "wrong_password");
        LoginResponse actual = null;
        ServerProxy proxy = new ServerProxy("localhost", "8080");
//        try {
//            actual = proxy.login(request);
//        } catch (NetException e) {
//            System.err.println(e.getMessage());
//        }
//        assertNotNull(actual);
        // a bad response code makes the login method return null.
        // TODO better error handling, read error message and response exception stuff.
//        assertNull(actual);
    }

    @Test
    @DisplayName("wrong username")
    void testLoginFail2() throws Exception {
        LoginRequest request = new LoginRequest("wrong_username", "hunter2");
        LoginResponse actual;
        ServerProxy proxy = new ServerProxy("localhost", "8080");
//        actual = proxy.login(request);
        // a bad response code makes the login method return null.
        // TODO better error handling, read error message and response exception stuff.
//        assertNull(actual);
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
    void testGetPeople() throws Exception {
        // TODO use the fill service to set up this test
        RegisterRequest registerRequest = new RegisterRequest("lukeludlow", "hunter2",
                "ll@live.com", "luke",
                "ludlow", "m");
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        RegisterResponse registerResponse = proxy.register(registerRequest);
        PeopleRequest request = new PeopleRequest(registerResponse.getAuthToken());
        PeopleResponse expected;
        PeopleResponse actual;
        actual = proxy.getPeople(request);
        assertNotNull(actual);
        System.out.println(actual);
    }

    @Test
    void testGetEvents() throws Exception {
        // TODO use the fill service to set up this test
        RegisterRequest registerRequest = new RegisterRequest("lukeludlow", "hunter2",
                "ll@live.com", "luke",
                "ludlow", "m");
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        RegisterResponse registerResponse = proxy.register(registerRequest);
        EventsRequest request = new EventsRequest(registerResponse.getAuthToken());
        EventsResponse expected;
        EventsResponse actual;
        actual = proxy.getEvents(request);
        assertNotNull(actual);
        System.out.println(actual);
    }



    @Test
    void testConstructor() {
        ServerProxy proxy = new ServerProxy("localhost", "8080");
        System.out.println(proxy.getHost() + ":" + proxy.getPort());
    }

}