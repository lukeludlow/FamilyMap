package dev.lukel.familymap.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        Person person = getPersonFromID(personID);
        if (person == null || "".equals(person.getSpouse())) {
            return null;
        }
        return getPersonFromID(person.getSpouse());
    }
    public static Person getMother(String personID) {
        Person person = getPersonFromID(personID);
        if (person == null || "".equals(person.getMother())) {
            return null;
        }
        return getPersonFromID(person.getMother());
    }
    public static Person getFather(String personID) {
        Person person = getPersonFromID(personID);
        if (person == null || "".equals(person.getFather())) {
            return null;
        }
        return getPersonFromID(person.getFather());
    }

    public static Person getPersonFromID(String personID) {
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

}

