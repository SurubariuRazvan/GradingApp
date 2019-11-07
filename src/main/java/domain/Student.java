package domain;

public class Student extends Entity<Integer> {
    private String familyName;
    private String firstName;
    private Integer group;
    private String email;
    private Integer labProfessorId;

    public Student(String familyName, String firstName, Integer group, String email, Integer labProfessorId) {
        this.familyName = familyName;
        this.firstName = firstName;
        this.group = group;
        this.email = email;
        this.labProfessorId = labProfessorId;
    }

    public Student(Integer id, String familyName, String firstName, Integer group, String email, Integer labProfessorId) {
        super.setId(id);
        this.familyName = familyName;
        this.firstName = firstName;
        this.group = group;
        this.email = email;
        this.labProfessorId = labProfessorId;
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

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getLabProfessorId() {
        return labProfessorId;
    }

    public void setLabProfessorId(Integer labProfessorId) {
        this.labProfessorId = labProfessorId;
    }

    @Override
    public String toString() {
        return "Student{" + " id=" + super.getId() +
                ", familyName='" + familyName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", group=" + group +
                ", email='" + email + '\'' +
                ", labProfessor=" + labProfessorId +
                '}';
    }
}
