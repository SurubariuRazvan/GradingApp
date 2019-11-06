package domain;

public class Professor extends Entity<Integer> {
    private String familyName;
    private String firstName;
    private String email;

    public Professor(String familyName, String firstName, String email) {
        this.familyName = familyName;
        this.firstName = firstName;
        this.email = email;
    }

    public Professor(Integer id, String familyName, String firstName, String email) {
        super.setId(id);
        this.familyName = familyName;
        this.firstName = firstName;
        this.email = email;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Professor{" +
                "familyName='" + familyName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
