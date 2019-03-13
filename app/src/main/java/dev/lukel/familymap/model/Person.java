package dev.lukel.familymap.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import dev.lukel.familymap.model.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private String descendant;
    private String personID; // unique ID assigned to this person
    private String firstName;
    private String lastName;
    private String gender; // either "f" or "m"
    private String father; // ID of person's father, possibly null
    private String mother; // ID of person's mother, possibly null
    private String spouse; // ID of person's spouse, possibly null

    public String getPrimaryKey() {
        return getPersonID();
    }

    public void setUserData(User u) {
        this.descendant = u.getUserName();
        this.firstName = u.getFirstName();
        this.lastName = u.getLastName();
        this.gender = u.getGender();
    }

}
