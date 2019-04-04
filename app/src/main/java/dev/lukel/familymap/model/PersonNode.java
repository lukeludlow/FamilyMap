package dev.lukel.familymap.model;

import java.util.HashMap;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonNode{

    private Person person;
    private Person mom;
    private Person dad;
    private Person spouse;
    private Person child;
    private Set<Person> relatives;
    private HashMap<String, Event> events; // key val is the event's type name

    public PersonNode(Person p) {
        this.person = p;
        this.mom = null;
        this.dad = null;
        this.spouse = null;
        this.events = new HashMap<>();
    }
    public PersonNode() {
        this(null);
    }

    public void setMom(Person mom) {
        this.mom = mom;
        this.relatives.add(mom);
    }

    public void setDad(Person dad) {
        this.dad = dad;
        this.relatives.add(dad);
    }

    public void setSpouse(Person spouse) {
        this.spouse = spouse;
        relatives.add(spouse);
    }

    public String getPersonID() {
        return this.person.getPersonID();
    }

    public Event getEvent(String type) {
        return events.get(type);
    }
    public Event addEvent(String key, Event val) {
        return events.put(key, val);
    }
    public boolean hasEventType(String type) {
        return events.containsKey(type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("person: " + person.getPersonID() + "\n");
        sb.append("mom: " + (mom != null ? mom.getPersonID() : null) + "\n");
        sb.append("dad: " + (dad != null ? dad.getPersonID() : null) + "\n");
        sb.append("spouse: " + (spouse != null ? spouse.getPersonID() : null) + "\n");
        sb.append("events:");
        for (String key : events.keySet()) {
            sb.append(" " + key + " ");
        }
        sb.append("\n");
        return sb.toString();
    }

}
