package dev.lukel.familymap.net;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.UUID;

import dev.lukel.familymap.net.message.LoginRequest;
import dev.lukel.familymap.net.message.LoginResponse;
import dev.lukel.familymap.net.message.RegisterRequest;
import dev.lukel.familymap.net.message.RegisterResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerProxyTest {

    private Process serverProcess;

    @BeforeAll
    void init() throws Exception {
        System.out.println("init server");
        startServer();
        Thread.sleep(3000); // give server a few seconds to start up
    }

    @AfterAll()
    void destroy() throws Exception {
        System.out.println("kill server");
        Thread.sleep(1000);
        killServer();
    }

    // to check whether server is running properly, do
    // ps -A | grep fm_server
    void startServer() throws Exception {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("java", "-jar", "out/artifacts/fm_server_jar/fm_server.jar", "8080");
        pb.directory(new File("/Users/luke/code/fm_server/"));
        serverProcess = pb.start();
    }

    // if process isn't killed properly, do
    // pkill -9 -f fm_server
    void killServer() {
        if (serverProcess.isAlive()) {
            serverProcess.destroy();
        }
    }

    @Test
    @DisplayName("login success")
    void testLogin() {
        ServerProxy proxy = new ServerProxy("127.0.0.1", "8080");
        // authtoken is random every time, and person id is also randomly generated,
        // so i just test that login is successful, username matches, and authtoken and personID are not null strings
        LoginRequest request = new LoginRequest("lukeludlow", "hunter2");
        LoginResponse expected = new LoginResponse("n/a", "lukeludlow", "n/a");
        LoginResponse actual = null;
        try {
            actual = proxy.login(request);
        } catch (NetException e) {
            System.err.println(e.toString());
        }
        assertNotNull(actual);
        assertNotEquals("", actual.getAuthToken());
        assertNotEquals("", actual.getPersonID());
        assertEquals(expected.getUserName(), actual.getUserName());
    }

    @Test
    @DisplayName("login fail (wrong password)")
    void testLoginFail() {
        ServerProxy proxy = new ServerProxy("127.0.0.1", "8080");
        LoginRequest request = new LoginRequest("lukeludlow", "wrong_password");
        LoginResponse actual = null;
        try {
            actual = proxy.login(request);
            fail("login exception not thrown");
        } catch (NetException e) {
            assertTrue(e.getMessage().contains("login failed"));
        }
    }

    @Test
    @DisplayName("login fail (server is down)")
    void testLoginFail2() {
        ServerProxy proxy = new ServerProxy("127.0.0.1", "8080");
        LoginRequest request = new LoginRequest("lukeludlow", "wrong_password");
        LoginResponse actual = null;
        killServer();
        try {
            actual = proxy.login(request);
            fail("login exception not thrown");
        } catch (NetException e) {
            assertTrue(e.getMessage().contains("login failed. Connection refused"));
        }
    }

    @Test
    @DisplayName("register success")
    void testRegister() {
        ServerProxy proxy = new ServerProxy("127.0.0.1", "8080");
        // generate a random username because i don't want to manually reach over and clear my database
        String randomUsername = UUID.randomUUID().toString();
        RegisterRequest request = new RegisterRequest(randomUsername, "password123", "t@test.com", "test", "ing", "m");
        RegisterResponse expected = new RegisterResponse("n/a", randomUsername, "n/a");
        RegisterResponse actual = null;
        try {
            actual = proxy.register(request);
        } catch (NetException e) {
            System.err.println(e.toString());
        }
        assertNotNull(actual);
        assertNotEquals("", actual.getAuthToken());
        assertNotEquals("", actual.getPersonID());
        assertEquals(expected.getUserName(), actual.getUserName());
    }

    @Test
    @DisplayName("register fail (user already exists)")
    void testRegisterFail() {
        ServerProxy proxy = new ServerProxy("127.0.0.1", "8080");
        RegisterRequest request = new RegisterRequest("lukeludlow", "password123", "t@test.com", "test", "ing", "m");
        RegisterResponse actual = null;
        try {
            actual = proxy.register(request);
            fail("register exception not thrown");
        } catch (NetException e) {
            assertTrue(e.getMessage().contains("register failed"));
        }
    }

    @Test
    @DisplayName("register fail (server is down)")
    void testRegisterFail2() {
        ServerProxy proxy = new ServerProxy("127.0.0.1", "8080");
        String randomUsername = UUID.randomUUID().toString();
        RegisterRequest request = new RegisterRequest(randomUsername, "password123", "t@test.com", "test", "ing", "m");
        RegisterResponse actual = null;
        killServer();
        try {
            actual = proxy.register(request);
            fail("register exception not thrown");
        } catch (NetException e) {
            assertTrue(e.getMessage().contains("register failed. Connection refused"));
        }
    }

    @Test
    @DisplayName("get people")
    void testGetPeople() {
        // get an authtoken
        // just assert size of person array because i'm not gonna check every single one lol
    }

    @Test
    @DisplayName("get people fail (bad authtoken)")
    void testGetPeopleFail() {

    }

    @Test
    @DisplayName("get events")
    void testGetEvents() {

    }

    @Test
    @DisplayName("get events fail (bad authtoken)")
    void testGetEventsFail() {

    }


//    @Test
////    @DisplayName("wrong password")
//    void testLoginFail() throws Exception {
//        LoginRequest request = new LoginRequest("lukeludlow", "wrong_password");
//        LoginResponse actual = null;
//        ServerProxy proxy = new ServerProxy("localhost", "8080");
////        try {
////            actual = proxy.login(request);
////        } catch (NetException e) {
////            System.err.println(e.getMessage());
////        }
////        assertNotNull(actual);
//        // a bad response code makes the login method return null.
//        // TODO better error handling, read error message and response exception stuff.
////        assertNull(actual);
//    }
//
//    @Test
////    @DisplayName("wrong username")
//    void testLoginFail2() throws Exception {
//        LoginRequest request = new LoginRequest("wrong_username", "hunter2");
//        LoginResponse actual;
//        ServerProxy proxy = new ServerProxy("localhost", "8080");
////        actual = proxy.login(request);
//        // a bad response code makes the login method return null.
//        // TODO better error handling, read error message and response exception stuff.
////        assertNull(actual);
//    }
//
//
//
//    @Test
//    void testRegister() throws Exception {
//        RegisterRequest request = new RegisterRequest("lukeludlow", "hunter2",
//                                                        "ll@live.com", "luke",
//                                                        "ludlow", "m");
//        RegisterResponse expected = new RegisterResponse("random_token", "lukeludlow", "random_person_id");
//        RegisterResponse actual;
//        ServerProxy proxy = new ServerProxy("localhost", "8080");
//        actual = proxy.register(request);
//        // unique token and id are always random, so set them for comparison
//        actual.setAuthToken("random_token");
//        actual.setPersonID("random_person_id");
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void testGetPeople() throws Exception {
//        // TODO use the fill service to set up this test
//        RegisterRequest registerRequest = new RegisterRequest("lukeludlow", "hunter2",
//                "ll@live.com", "luke",
//                "ludlow", "m");
//        ServerProxy proxy = new ServerProxy("localhost", "8080");
//        RegisterResponse registerResponse = proxy.register(registerRequest);
//        PeopleRequest request = new PeopleRequest(registerResponse.getAuthToken());
//        PeopleResponse expected;
//        PeopleResponse actual;
//        actual = proxy.getPeople(request);
//        assertNotNull(actual);
//        System.out.println(actual);
//    }
//
//    @Test
//    void testGetEvents() throws Exception {
//        // TODO use the fill service to set up this test
//        RegisterRequest registerRequest = new RegisterRequest("lukeludlow", "hunter2",
//                "ll@live.com", "luke",
//                "ludlow", "m");
//        ServerProxy proxy = new ServerProxy("localhost", "8080");
//        RegisterResponse registerResponse = proxy.register(registerRequest);
//        EventsRequest request = new EventsRequest(registerResponse.getAuthToken());
//        EventsResponse expected;
//        EventsResponse actual;
//        actual = proxy.getEvents(request);
//        assertNotNull(actual);
//        System.out.println(actual);
//    }
//
//
//
//    @Test
//    void testConstructor() {
//        ServerProxy proxy = new ServerProxy("localhost", "8080");
//        System.out.println(proxy.getHost() + ":" + proxy.getPort());

}