package repository.sql;

import domain.Professor;
import validation.Validator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfessorPostgreSQLRepository extends AbstractPostgreSQLRepository<Integer, Professor> {
    public ProfessorPostgreSQLRepository(Validator<Professor> validator, String url, String user, String password) throws SQLException, ClassNotFoundException {
        super(validator, url, user, password);
    }

    @Override
    protected Professor readEntity(ResultSet result) throws SQLException {
        Integer id = result.getInt("id");
        String familyName = result.getString("familyName");
        String firstName = result.getString("firstName");
        String email = result.getString("email");
        return new Professor(id, familyName, firstName, email);
    }

    @Override
    protected String findOneString(Integer id) {
        return "SELECT * from PROFESSOR where ID = " + id + ";";
    }

    @Override
    protected String findAllString() {
        return "SELECT * from PROFESSOR;";
    }

    @Override
    protected String insertString(Professor professor) {
        return "INSERT INTO PROFESSOR (id, familyName, firstName, email ) " +
                "VALUES (" + professor.getId()
                + ",'" + professor.getFamilyName()
                + "','" + professor.getFirstName()
                + "','" + professor.getEmail() + "');";
    }

    @Override
    protected String deleteString(Integer id) {
        return "DELETE from PROFESSOR where ID = " + id + ";";
    }

    @Override
    protected String updateString(Professor professor) {
        return "UPDATE PROFESSOR SET "
                + "id= " + professor.getId()
                + ", familyName= '" + professor.getFamilyName()
                + "', firstName= '" + professor.getFirstName()
                + "', email= '" + professor.getEmail()
                + "' where ID = " + professor.getId() + ";";
    }
}
