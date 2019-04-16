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

import dev.lukel.familymap.net.message.EventsRequest;
import dev.lukel.familymap.net.message.EventsResponse;
import dev.lukel.familymap.net.message.LoginRequest;
import dev.lukel.familymap.net.message.LoginResponse;
import dev.lukel.familymap.net.message.PeopleRequest;
import dev.lukel.familymap.net.message.PeopleResponse;
import dev.lukel.familymap.net.message.RegisterRequest;
import dev.lukel.familymap.net.message.RegisterResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerProxyTest {

    private Process serverProcess;

    @BeforeAll
    void init() throws Exception {
        System.out.println("init server");
        startServer();
    }

    @AfterAll()
    void destroy() throws Exception {
        System.out.println("kill server");
        Thread.sleep(1000);
        killServer();
    }

    @BeforeEach
    void setUp() throws Exception {
        if (!serverProcess.isAlive()) {
            System.out.println("BeforeEach setup is restarting the server...");
            startServer();
        }
    }

    @AfterEach
    void tearDown() {}

    // to check whether server is running properly, do
    // ps -A | grep fm_server
    private void startServer() throws Exception {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("java", "-jar", "out/artifacts/fm_server_jar/fm_server.jar", "8080");
        pb.directory(new File("/Users/luke/code/fm_server/"));
        serverProcess = pb.start();
        Thread.sleep(2000); // give server a few seconds to start up
    }

    // if process isn't killed properly, do
    // pkill -9 -f fm_server
    private void killServer() {
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
            fail("login exception should have been thrown");
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
            fail("login exception should have been thrown");
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
            fail("register exception should have been thrown");
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
            fail("register exception should have been thrown");
        } catch (NetException e) {
            assertTrue(e.getMessage().contains("register failed. Connection refused"));
        }
    }

    @Test
    @DisplayName("get people")
    void testGetPeople() {
        ServerProxy proxy = new ServerProxy("127.0.0.1", "8080");
        try {
            // login to get an authtoken
            LoginRequest loginRequest = new LoginRequest("lukeludlow", "hunter2");
            LoginResponse loginResponse = proxy.login(loginRequest);
            PeopleRequest request = new PeopleRequest(loginResponse.getAuthToken());
            PeopleResponse actual = proxy.getPeople(request);
            assertNotNull(actual);
            // assert the size of person array because i'm not gonna check every single one lol
            // 31 people are added into database when someone registers.
            // user is 1 person, then fill adds 4 generations of ancestors which is 30 people
            int numPeopleExpected = 31;
            assertEquals(numPeopleExpected, actual.getData().length);

        } catch (NetException e) {
            fail("get people unexpected exception");
        }
    }

    @Test
    @DisplayName("get people fail (bad authtoken)")
    void testGetPeopleFail() {
        ServerProxy proxy = new ServerProxy("127.0.0.1", "8080");
        try {
            String randomInvalidToken = UUID.randomUUID().toString();
            PeopleRequest request = new PeopleRequest(randomInvalidToken);
            PeopleResponse actual = proxy.getPeople(request);
            fail("get people exception should have been thrown");
        } catch (NetException e) {
            assertTrue(e.getMessage().contains("get people failed"));
        }
    }

    @Test
    @DisplayName("get events")
    void testGetEvents() {
        ServerProxy proxy = new ServerProxy("127.0.0.1", "8080");
        try {
            // login to get an authtoken
            LoginRequest loginRequest = new LoginRequest("lukeludlow", "hunter2");
            LoginResponse loginResponse = proxy.login(loginRequest);
            EventsRequest request = new EventsRequest(loginResponse.getAuthToken());
            EventsResponse actual = proxy.getEvents(request);
            assertNotNull(actual);
            // assert the size of event array because i'm not gonna check every single one lol
            // 91 events are added into database when someone registers.
            int numEventsExpected = 91;
            assertEquals(numEventsExpected, actual.getData().length);

        } catch (NetException e) {
            System.err.println(e.toString());
            fail("get people unexpected exception");
        }
    }

    @Test
    @DisplayName("get events fail (bad authtoken)")
    void testGetEventsFail() {
        ServerProxy proxy = new ServerProxy("127.0.0.1", "8080");
        try {
            String randomInvalidToken = UUID.randomUUID().toString();
            EventsRequest request = new EventsRequest(randomInvalidToken);
            EventsResponse actual = proxy.getEvents(request);
            fail("get events exception should have been thrown");
        } catch (NetException e) {
            assertTrue(e.getMessage().contains("get events failed"));
        }
    }

}