package dev.lukel.familymap.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class FamilyTree {

    private PersonNode root;
    private List<PersonNode> allNodes;
    private List<Person> allPeople;
    private List<Event> allEvents;
    private Map<Person, PersonNode> personToNodeMap;
    private Map<Person, PersonNode> parentMap;
    private Map<Person, PersonNode> spouseMap;
    private Map<Person, PersonNode> childMap;

    public FamilyTree() {
        root = null;
        allNodes = new ArrayList<>();
        setPeopleEventsFromDataSingleton();
        buildTree();
    }

    public void addNode(PersonNode n) {
        allNodes.add(n);
        allPeople.add(n.getPerson());
        for (Event e : n.getEvents().values()) {
            allEvents.add(e);
        }
    }

    public void setPeopleEventsFromDataSingleton() {
        allPeople = Arrays.asList(DataSingleton.getPeople());
        allEvents = Arrays.asList(DataSingleton.getEvents());
    }

    public PersonNode buildPersonNode(Person p) {
        PersonNode node = new PersonNode(p);
        node.setRelatives(new HashSet<>());
        for (Person familyMember : allPeople) {
            if (familyMember.getPersonID().equals(p.getMother())) {
                node.setMom(familyMember);
                node.getRelatives().add(familyMember);
            } else if (familyMember.getPersonID().equals(p.getFather())) {
                node.setDad(familyMember);
                node.getRelatives().add(familyMember);
            } else if (familyMember.getPersonID().equals(p.getSpouse())) {
                node.setSpouse(familyMember);
                node.getRelatives().add(familyMember);
            } else if (!TextUtils.isEmpty(familyMember.getFather()) && !TextUtils.isEmpty(familyMember.getMother())) {
                if (familyMember.getFather().equals(p.getPersonID()) || familyMember.getMother().equals(p.getPersonID())) {
                    node.setChild(familyMember);
                    node.getRelatives().add(familyMember);
                }
            }
        }
        for (Event event : allEvents) {
            if (event.getPersonID().equals(p.getPersonID())) {
                node.addEvent(event.getEventType(), event);
            }
        }
        return node;
    }

    public void buildTree() {
        personToNodeMap = new HashMap<>();
        for (Person p : allPeople) {
            PersonNode node = buildPersonNode(p);
            allNodes.add(node);
            personToNodeMap.put(p, node);
        }
    }

}




