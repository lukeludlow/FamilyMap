package dev.lukel.familymap.model;

import java.util.ArrayList;

public class FamilyTree {

    private PersonNode root;
    private ArrayList<PersonNode> nodes;
    private ArrayList<Person> allPeople;
    private ArrayList<Event> allEvents;

    public FamilyTree() {
        this.root = null;
        this.nodes = new ArrayList<>();
        this.allPeople = new ArrayList<>();
        this.allEvents = new ArrayList<>();
    }

    public void addNode(PersonNode n) {
        nodes.add(n);
        allPeople.add(n.getPerson());
        for (Event e : n.getEvents().values()) {
            allEvents.add(e);
        }
    }

}




