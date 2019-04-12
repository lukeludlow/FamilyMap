package dev.lukel.familymap.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FamilyUtils {

    public static String getRelationshipType(Person p, Person relative) {
        String relationship = "no relationship found";
        if (relative.getPersonID().equals(p.getMother())) {
            relationship = "mother";
        } else if (relative.getPersonID().equals(p.getFather())) {
            relationship = "father";
        } else if (relative.getPersonID().equals(p.getSpouse())) {
            relationship = "spouse";
        } else if (!"".equals(relative.getMother()) && relative.getMother().equals(p.getPersonID())) {
            relationship = "child";
        } else if (!"".equals(relative.getFather()) && relative.getFather().equals(p.getPersonID())) {
            relationship = "child";
        }
        return relationship;
    }

    public static Person getSpouse(String personID) {
        Person person = getPersonById(personID);
        if (person == null || "".equals(person.getSpouse())) {
            return null;
        }
        return getPersonById(person.getSpouse());
    }
    public static Person getMother(String personID) {
        Person person = getPersonById(personID);
        if (person == null || "".equals(person.getMother())) {
            return null;
        }
        return getPersonById(person.getMother());
    }
    public static Person getFather(String personID) {
        Person person = getPersonById(personID);
        if (person == null || "".equals(person.getFather())) {
            return null;
        }
        return getPersonById(person.getFather());
    }

    public static Person getPersonById(String personID) {
        for (Person p : DataSingleton.getFamilyTree().getAllPeople()) {
            if (p.getPersonID().equals(personID)) {
                return p;
            }
        }
        return null;
    }

    public static List<Event> sortEventsChronological(List<Event> events) {
        Collections.sort(events, new YearComparator());
        return events;
    }

    public static List<Event> getChronologicalEvents(Person p) {
        PersonNode node = DataSingleton.getFamilyTree().getPersonToNodeMap().get(p);
        List<Event> orderedEvents = new ArrayList<>(node.getEvents().values());
        Collections.sort(orderedEvents, new YearComparator());
        return orderedEvents;
    }

    private static class YearComparator implements Comparator<Event> {
        @Override
        public int compare(Event a, Event b) {
            int i = 0;
            if (a.getYear() < b.getYear()) {
                i = -1;
            } else if (a.getYear() == b.getYear()) {
                i = 0;
            } else if (a.getYear() > b.getYear()) {
                i = 1;
            }
            return i;
        }
    }

    public static int getBirthDate(Person p) {
        PersonNode node = DataSingleton.getFamilyTree().getPersonToNodeMap().get(p);
        Map<String, Event> eventMap = node.getEvents();
        if (eventMap.containsKey("birth")) {
            return eventMap.get("birth").getYear();
        }
        return -1;
    }

    public static String getGenderById(String personId) {
        Person p = getPersonById(personId);
        if (p == null) {
            return "person not found";
        }
        return p.getGender();
    }

    public static boolean isMale(String personId) {
        String gender = getGenderById(personId);
        return "m".equals(gender);
    }

    public static boolean isFemale(String personId) {
        String gender = getGenderById(personId);
        return "f".equals(gender);
    }

    public static boolean isOnMotherSide(String personId) {
        Person mother = getPersonById(DataSingleton.getUser().getMother());
        return isOnFamilySide(mother, personId);
    }
    public static boolean isOnFatherSide(String personId) {
        Person father = getPersonById(DataSingleton.getUser().getFather());
        return isOnFamilySide(father, personId);
    }
    private static boolean isOnFamilySide(Person current, String personId) {
        if (current == null) {
            return false;
        }
        if (current.getPersonID().equals(personId)) {
            return true;
        }
        boolean found = false;
        if (!"".equals(current.getMother())) {
            if (isOnFamilySide(getPersonById(current.getMother()), personId)) {
                found = true;
            }
        }
        if (!"".equals(current.getFather())) {
            if (isOnFamilySide(getPersonById(current.getFather()), personId)) {
                found = true;
            }
        }
        return found;
    }

}

