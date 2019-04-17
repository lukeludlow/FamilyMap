package dev.lukel.familymap.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dev.lukel.familymap.net.NetException;
import dev.lukel.familymap.net.ServerProxy;
import dev.lukel.familymap.net.message.EventsRequest;
import dev.lukel.familymap.net.message.EventsResponse;
import dev.lukel.familymap.net.message.LoginRequest;
import dev.lukel.familymap.net.message.LoginResponse;
import dev.lukel.familymap.net.message.PeopleRequest;
import dev.lukel.familymap.net.message.PeopleResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ModelTest {

    // DataSingleton is eagerly instantiated, i just have to populate it
    private Process serverProcess;
    // test family data members to be used in multiple methods
    private Person luke;
    private Person mom;
    private Person dad;
    private Person lukesWife;
    private Person child;

    @BeforeAll
    void init() throws Exception {
        startServer();
        syncData();
    }

    @AfterAll
    void destroy() throws Exception {
        killServer();
    }

    @BeforeEach
    void setUp() throws Exception {
        if (serverProcess != null && !serverProcess.isAlive()) {
            startServer();
        }
        syncData();
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    @DisplayName("calculate family relationships")
    void testRelationships() {
        if (luke == null || mom == null || dad == null || lukesWife == null || child == null) {
            buildTestFamily();
        }
        assertEquals("spouse", FamilyUtils.getRelationshipType(luke, lukesWife));
        assertEquals("spouse", FamilyUtils.getRelationshipType(lukesWife, luke));
        assertEquals("spouse", FamilyUtils.getRelationshipType(mom, dad));
        assertEquals("spouse", FamilyUtils.getRelationshipType(dad, mom));
        assertEquals("mother", FamilyUtils.getRelationshipType(luke, mom));
        assertEquals("mother", FamilyUtils.getRelationshipType(child,lukesWife));
        assertEquals("father", FamilyUtils.getRelationshipType(luke, dad));
        assertEquals("father", FamilyUtils.getRelationshipType(child, luke));
        assertEquals("child", FamilyUtils.getRelationshipType(luke, child));
        assertEquals("child", FamilyUtils.getRelationshipType(lukesWife, child));
        assertEquals("child", FamilyUtils.getRelationshipType(mom, luke));
        assertEquals("child", FamilyUtils.getRelationshipType(dad, luke));
    }

    @Test
    @DisplayName("calculate family relationships negative")
    void testRelationshipsNegative() {
        if (luke == null || mom == null || dad == null || lukesWife == null || child == null) {
            buildTestFamily();
        }
        assertNotEquals("spouse", FamilyUtils.getRelationshipType(luke, mom));
        assertNotEquals("spouse", FamilyUtils.getRelationshipType(lukesWife, dad));
        assertNotEquals("mother", FamilyUtils.getRelationshipType(luke, dad));
        assertNotEquals("mother", FamilyUtils.getRelationshipType(child, mom)); // mom is luke's mother
        assertNotEquals("father", FamilyUtils.getRelationshipType(luke, mom));
        assertNotEquals("father", FamilyUtils.getRelationshipType(child, dad)); // dad is luke's father
        assertNotEquals("child", FamilyUtils.getRelationshipType(child, luke));
        assertNotEquals("child", FamilyUtils.getRelationshipType(child, lukesWife));
        assertNotEquals("child", FamilyUtils.getRelationshipType(luke, mom));
        assertNotEquals("child", FamilyUtils.getRelationshipType(luke, mom));
    }

    @Test
    @DisplayName("filter events")
    void testFilter() {
        initializeSettings();
        enableAllEventFilters();
        enableAllEventTypes();
        Person user = DataSingleton.getUser();
        List<Event> userEvents = FamilyUtils.getPersonEvents(user);
        // test filter event list methods
        final int NUM_EVENTS_TOTAL = 91;
        assertEquals(NUM_EVENTS_TOTAL, Settings.getFilteredEvents().size());
        assertEquals(userEvents.size(), Settings.filterEventList(userEvents).size());
        assertEquals(userEvents, Settings.filterEventList(userEvents));
        // make sure all the user's events pass the filter
        for (Event e : userEvents) {
            assertTrue(Settings.passesFilter(e));
        }
        // now try only allowing birthdays
        disableAllEventTypes();
        DataSingleton.getSettings().getEnabledEventTypes().put("birth", true);
        List<Event> expectedFilteredEvents = new ArrayList<>();
        for (Event e : userEvents) {
            if ("birth".equals(e.getEventType())) {
                expectedFilteredEvents.add(e);
            }
        }
        assertEquals(expectedFilteredEvents, Settings.filterEventList(userEvents));
    }

    @Test
    @DisplayName("filter events negative")
    void testFilterNegative() {
        initializeSettings();
        disableAllEventFilters();
        disableAllEventTypes();
        Person user = DataSingleton.getUser();
        List<Event> userEvents = FamilyUtils.getPersonEvents(user);
        final int NUM_FILTERED_EVENTS = 0;
        assertEquals(NUM_FILTERED_EVENTS, Settings.getFilteredEvents().size());
        assertEquals(NUM_FILTERED_EVENTS, Settings.filterEventList(userEvents).size());
        for (Event e : userEvents) {
            assertFalse(Settings.passesFilter(e));
        }
        enableAllEventFilters();
        disableAllEventTypes();
        assertEquals(NUM_FILTERED_EVENTS, Settings.getFilteredEvents().size());
        assertEquals(NUM_FILTERED_EVENTS, Settings.filterEventList(userEvents).size());
        disableAllEventFilters();
        enableAllEventTypes();
        assertEquals(NUM_FILTERED_EVENTS, Settings.getFilteredEvents().size());
        assertEquals(NUM_FILTERED_EVENTS, Settings.filterEventList(userEvents).size());
    }

    @Test
    @DisplayName("chronologically sort events")
    void testSortEvents() {
        List<Event> allEvents = Arrays.asList(DataSingleton.getEvents());
        Collections.shuffle(allEvents);
        List<Event> allEventsSorted = FamilyUtils.sortEventsChronological(allEvents);
        assertTrue(eventsAreSorted(allEventsSorted));
        // we will use the mother's events so that we can guarantee at least 3 events (birth, wedding, death)
        Person user = DataSingleton.getUser();
        Person mom = FamilyUtils.getMother(user.getMother());
        List<Event> userEventsChronological = FamilyUtils.getChronologicalEvents(mom);
        assertTrue(eventsAreSorted(userEventsChronological));
        List<Event> testEvents = new ArrayList<>();
        Event eventYear9999 = new Event("lukeludlow","xxx", "1", 40.0,-111.0,
                "country","city","lonely",9999);
        Event eventYearNegative500 = new Event("lukeludlow","xxx", "1", 40.0,-111.0,
                "country","city","lonely",-500);
        testEvents.add(eventYear9999);
        assertTrue(eventsAreSorted(testEvents)); // only one event means it's sorted
        testEvents.add(eventYearNegative500);
        assertTrue(eventsAreSorted(FamilyUtils.sortEventsChronological(testEvents)));
    }

    @Test
    @DisplayName("chronologically sort events negative/fail")
    void testSortEventsNegative() {
        List<Event> allEvents = Arrays.asList(DataSingleton.getEvents());
        Collections.shuffle(allEvents);
        assertFalse(eventsAreSorted(allEvents));
        Person user = DataSingleton.getUser();
        Person mom = FamilyUtils.getMother(user.getMother());
        List<Event> momEvents = FamilyUtils.getPersonEvents(mom);
        Collections.shuffle(momEvents);
        assertFalse(eventsAreSorted(momEvents));
        List<Event> testEvents = new ArrayList<>();
        Event eventYear9999 = new Event("lukeludlow","xxx", "1", 40.0,-111.0,
                "country","city","lonely",9999);
        Event eventYearNegative500 = new Event("lukeludlow","xxx", "1", 40.0,-111.0,
                "country","city","lonely",-500);
        testEvents.add(eventYear9999);
        testEvents.add(eventYearNegative500);
        assertFalse(eventsAreSorted(testEvents));
    }

    @Test
    @DisplayName("search people")
    void testSearchPeople() {
        // simple case, find the user
        Person user = DataSingleton.getUser();
        String userPersonName = user.getFirstName() + " " + user.getLastName();
        List<Person> found = searchPeople(userPersonName);
        assertEquals(user, found.get(0));
        // search finds substrings
        String complexName = UUID.randomUUID().toString();
        String complexNameSubstring = complexName.substring(5, 15);
        Person complexNamePerson = new Person("lukeludlow", "complex_name_person",
                complexName, "lastname", "m", "father_id", "mother_id", "spouse_id");
        addPersonToDataSingleton(complexNamePerson);
        found = searchPeople(complexNameSubstring);
        assertEquals(complexNamePerson, found.get(0));
        // search ignores capitalization
        Person uppercasePerson = new Person("lukeludlow", "uppercase",
                "FIRSTNAME", "LASTNAME", "m", "father_id", "mother_id", "spouse_id");
        Person lowercasePerson = new Person("lukeludlow", "lowercase",
                "firstname", "lastname", "m", "father_id", "mother_id", "spouse_id");
        addPersonToDataSingleton(uppercasePerson);
        addPersonToDataSingleton(lowercasePerson);
        List<Person> foundPeopleUppercase = searchPeople("FIRSTNAME");
        List<Person> foundPeopleLowercase = searchPeople("firstname");
        assertEquals(foundPeopleUppercase, foundPeopleLowercase);
        // search people checks for the person's first and last name, so "Luke Alfonso" and "Alfonso Luke" would both be found
        Person lukeAlfonso = new Person("lukeludlow", "abc123", "Luke", "Alfonso",
                        "m", "father_id", "mother_id", "spouse_id");
        Person alfonsoLuke = new Person("lukeludlow", "123abc", "Alfonso", "Luke",
                "m", "father_id", "mother_id", "spouse_id");
        addPersonToDataSingleton(lukeAlfonso);
        addPersonToDataSingleton(alfonsoLuke);
        found = searchPeople("Alfonso");
        assertEquals(2, found.size());
        assertTrue(found.contains(lukeAlfonso));
        assertTrue(found.contains(alfonsoLuke));
    }

    @Test
    @DisplayName("search events")
    void testSearchEvents() {
        // simple case
        Person user = DataSingleton.getUser();
        Event userBirth = FamilyUtils.getChronologicalEvents(user).get(0);
        int userBirthdate = FamilyUtils.getBirthDate(user);
        List<Event> found = searchEvents(Integer.toString(userBirthdate));
        assertEquals(userBirth, found.get(0));
        // search finds substrings
        Event createdFamilyMap = new Event("lukeludlow", "101", "1",10.1,-10.1, "japan", "tokyo",
                "created family map", 3019);
        String simpleSubstring = "created family map";
        String anotherSubstring = "tokyo, japan";
        addEventToDataSingleton(createdFamilyMap);
        found = searchEvents(simpleSubstring);
        assertEquals(createdFamilyMap, found.get(0));
        found = searchEvents(anotherSubstring);
        assertEquals(createdFamilyMap, found.get(0));
        // search events does all the same stuff that search people does, so i don't want to write more redundant test cases
    }

    @Test
    @DisplayName("search people negative")
    void testSearchPeopleNegative() {
        // search blank string should return empty array list
        List<Person> found = searchPeople("");
        assertEquals(0, found.size());
        // superstring won't be found (searching xxlukexx will not find luke)
        Person luke = new Person("lukeludlow", "xx", "luke", "ludlow",
                "m", "father_id", "mother_id", "spouse_id");
        addPersonToDataSingleton(luke);
        found = searchPeople("xxlukexx");
        assertEquals(0, found.size());
        // non-alphanumeric characters are ignored
        found = searchPeople("!@#$%^&*_13579");
        assertEquals(0, found.size());
    }

    @Test
    @DisplayName("search events negative")
    void testSearchEventsNegative() {
        // search blank string should return empty array list
        List<Event> found = searchEvents("");
        assertEquals(0, found.size());
        // incorrectly formatted date will not be found
        // e.g. searching "1999" or "(1999)" will find the event, but searching ")1999(" will not
        Event createdFamilyMap = new Event("lukeludlow", "101", "1",10.1,-10.1, "japan", "tokyo",
                "created family map", 1999);
        addEventToDataSingleton(createdFamilyMap);
        found = searchEvents(")1999(");
        assertEquals(0, found.size());
        // non-alphanumeric characters are ignored
        found = searchEvents("!@#$%^&*_13579");
        assertEquals(0, found.size());
    }

    private void syncData() {
        try {
            ServerProxy proxy = new ServerProxy("127.0.0.1", "8080");
            LoginRequest loginRequest = new LoginRequest("lukeludlow", "hunter2");
            LoginResponse loginResponse = proxy.login(loginRequest);
            DataSingleton.setUsername(loginRequest.getUserName());
            DataSingleton.setUserPersonID(loginResponse.getPersonID());
            DataSingleton.setAuthtoken(loginResponse.getAuthToken());
            PeopleResponse peopleResponse = proxy.getPeople(new PeopleRequest(loginResponse.getAuthToken()));
            EventsResponse eventsResponse = proxy.getEvents(new EventsRequest(loginResponse.getAuthToken()));
            DataSingleton.setPeople(peopleResponse.getData());
            DataSingleton.setEvents(eventsResponse.getData());
            // set DataSingleton user
            String id = DataSingleton.getUserPersonID();
            for (Person p : DataSingleton.getPeople()) {
                if (p.getPersonID().equals(id)) {
                    DataSingleton.setUser(p);
                }
            }
            DataSingleton.setFamilyTree(new FamilyTree());
        } catch (NetException e) {
            System.err.println(e.toString());
            System.err.println("error getting data. ending test execution.");
            System.exit(1);
        }
    }

    private void buildTestFamily() {
        luke = new Person("lukeludlow", "1", "luke", "ludlow",
                "m", "father_id", "mother_id", "spouse_id");
        mom = new Person("lukeludlow", "2", "mom", "ludlow",
                "f", "n/a", "n/a", "spouse_id");
        dad = new Person("lukeludlow", "3", "dad", "ludlow",
                "m", "n/a", "n/a", "spouse_id");
        lukesWife = new Person("lukeludlow", "4", "miss", "ludlow",
                "f", "n/a", "n/a", "spouse_id");
        child = new Person("lukeludlow", "5", "junior", "ludlow",
                "m", "father_id", "mother_id", "n/a");
        luke.setSpouse(lukesWife.getPersonID());
        luke.setMother(mom.getPersonID());
        luke.setFather(dad.getPersonID());
        mom.setSpouse(dad.getPersonID());
        dad.setSpouse(mom.getPersonID());
        lukesWife.setSpouse(luke.getPersonID());
        child.setMother(lukesWife.getPersonID());
        child.setFather(luke.getPersonID());
        Person[] peopleArray = DataSingleton.getPeople();
        // update DataSingleton
        List<Person> updatedPeople = new ArrayList<>(Arrays.asList(peopleArray));
        updatedPeople.add(luke);
        updatedPeople.add(mom);
        updatedPeople.add(dad);
        updatedPeople.add(lukesWife);
        updatedPeople.add(child);
        DataSingleton.setPeople(updatedPeople.toArray(new Person[0]));
        // rebuild family tree so that it includes these new relationships
        DataSingleton.setFamilyTree(new FamilyTree());
    }

    private void initializeSettings() {
        Settings settings = new Settings("don't create marker colors");
        DataSingleton.setSettings(settings);
    }

    private void enableAllEventFilters() {
        DataSingleton.getSettings().setMotherSide(true);
        DataSingleton.getSettings().setFatherSide(true);
        DataSingleton.getSettings().setMaleEvents(true);
        DataSingleton.getSettings().setFemaleEvents(true);
    }

    private void disableAllEventFilters() {
        DataSingleton.getSettings().setMotherSide(false);
        DataSingleton.getSettings().setFatherSide(false);
        DataSingleton.getSettings().setMaleEvents(false);
        DataSingleton.getSettings().setFemaleEvents(false);
    }

    private void enableAllEventTypes() {
        Map<String, Boolean> map = new HashMap<>();
        for (Event e : DataSingleton.getEvents()) {
            if (!map.containsKey(e.getEventType())) {
                map.put(e.getEventType(), true);
            }
        }
        DataSingleton.getSettings().setEnabledEventTypes(map);
    }

    private void disableAllEventTypes() {
        Map<String, Boolean> map = new HashMap<>();
        for (Event e : DataSingleton.getEvents()) {
            if (!map.containsKey(e.getEventType())) {
                map.put(e.getEventType(), false);
            }
        }
        DataSingleton.getSettings().setEnabledEventTypes(map);
    }

    private boolean eventsAreSorted(List<Event> events) {
        boolean sorted = true;
        for (int i = 0; i < events.size() - 1; i++) {
            if (events.get(i).getYear() > events.get(i + 1).getYear()) {
                sorted = false;
                break;
            }
        }
        return sorted;
    }

    private List<Person> searchPeople(String searchText) {
        if (StringUtils.isBlank(searchText)) {
            return new ArrayList<>();
        }
        List<Person> found = new ArrayList<>();
        for (Person p : DataSingleton.getPeople()) {
            String personName = p.getFirstName() + " " + p.getLastName();
            if (personName.toLowerCase().contains(searchText.toLowerCase())) {
                found.add(p);
            }
        }
        return found;
    }

    private List<Event> searchEvents(String searchText) {
        if (StringUtils.isBlank(searchText)) {
            return new ArrayList<>();
        }
        List<Event> found = new ArrayList<>();
        for (Event e : DataSingleton.getEvents()) {
            String titleText = e.getEventType() + ": " + e.getCity() + ", " + e.getCountry() + " (" + e.getYear() + ")";
            if (titleText.toLowerCase().contains(searchText.toLowerCase())) {
                found.add(e);
            }
        }
        return FamilyUtils.sortEventsChronological(found);
    }

    private void addPersonToDataSingleton(Person p) {
        Person[] peopleArray = DataSingleton.getPeople();
        List<Person> updatedPeople = new ArrayList<>(Arrays.asList(peopleArray));
        updatedPeople.add(p);
        DataSingleton.setPeople(updatedPeople.toArray(new Person[0]));
        DataSingleton.setFamilyTree(new FamilyTree());
    }

    private void addEventToDataSingleton(Event e) {
        Event[] eventArray = DataSingleton.getEvents();
        List<Event> updatedEvents = new ArrayList<>(Arrays.asList(eventArray));
        updatedEvents.add(e);
        DataSingleton.setEvents(updatedEvents.toArray(new Event[0]));
        DataSingleton.setFamilyTree(new FamilyTree());
    }

    // to check whether server is running properly, do
    // ps -A | grep fm_server
    private void startServer() throws Exception {
        System.out.println("start server");
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("java", "-jar", "out/artifacts/fm_server_jar/fm_server.jar", "8080");
        pb.directory(new File("/Users/luke/code/fm_server/"));
        serverProcess = pb.start();
        Thread.sleep(2000); // give server a few seconds to start up
    }

    // if process isn't killed properly, do
    // pkill -9 -f fm_server
    private void killServer() throws Exception {
        System.out.println("kill server checking if process is alive...");
        if (serverProcess != null && serverProcess.isAlive()) {
            System.out.println("kill server");
            Thread.sleep(1000);
            serverProcess.destroy();
            Thread.sleep(1000);
            serverProcess = null;
        }
    }

}