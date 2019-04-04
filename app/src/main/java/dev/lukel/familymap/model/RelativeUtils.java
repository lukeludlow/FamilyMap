package dev.lukel.familymap.model;


public class RelativeUtils {

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

}

