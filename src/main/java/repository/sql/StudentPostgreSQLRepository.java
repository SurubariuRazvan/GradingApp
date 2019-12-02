package repository.sql;

import domain.Student;
import validation.Validator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentPostgreSQLRepository extends AbstractPostgreSQLRepository<Integer, Student> {
    public StudentPostgreSQLRepository(Validator<Student> validator, String url, String user, String password) throws SQLException, ClassNotFoundException {
        super(validator, url, user, password);
    }

    @Override
    protected Student readEntity(ResultSet result) throws SQLException {
        Integer id = result.getInt("id");
        String familyName = result.getString("familyName");
        String firstName = result.getString("firstName");
        Integer group = result.getInt("group");
        String email = result.getString("email");
        Integer labProfessorId = result.getInt("labProfessorId");
        return new Student(id, familyName, firstName, group, email, labProfessorId);
    }

    @Override
    protected String findOneString(Integer id) {
        return "SELECT * from STUDENT where ID = " + id + ";";
    }

    @Override
    protected String findAllString() {
        return "SELECT * from STUDENT;";
    }

    @Override
    protected String insertString(Student student) {
        return "INSERT INTO STUDENT (id, familyName, firstName, \"group\", email, labProfessorId ) " +
                "VALUES (" + student.getId()
                + ",'" + student.getFamilyName()
                + "','" + student.getFirstName()
                + "'," + student.getGroup()
                + ",'" + student.getEmail()
                + "'," + student.getLabProfessorId() + ");";
    }

    @Override
    protected String deleteString(Integer id) {
        return "DELETE from STUDENT where ID = " + id + ";";
    }

    @Override
    protected String updateString(Student student) {
        return "UPDATE STUDENT SET "
                + "id= " + student.getId()
                + ", familyName= '" + student.getFamilyName()
                + "', firstName= '" + student.getFirstName()
                + "', \"group\"= " + student.getGroup()
                + ", email= '" + student.getEmail()
                + "', labProfessorId= " + student.getLabProfessorId()
                + " where ID = " + student.getId() + ";";
    }
}
