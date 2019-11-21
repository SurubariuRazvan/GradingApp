package repository.sql;

import domain.Homework;
import validation.Validator;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeworkPostgreSQLRepository extends AbstractPostgreSQLRepository<Integer, Homework> {
    public HomeworkPostgreSQLRepository(Validator<Homework> validator, String url, String user, String password) throws SQLException, ClassNotFoundException {
        super(validator, url, user, password);
    }

    @Override
    protected Homework readEntity(ResultSet result) throws SQLException {
        Integer id = result.getInt("id");
        String description = result.getString("description");
        Integer startWeek = result.getInt("startWeek");
        Integer deadlineWeek = result.getInt("deadlineWeek");
        return new Homework(id, description, startWeek, deadlineWeek);
    }

    @Override
    protected String findOneString(Integer id) {
        return "SELECT * from HOMEWORK where ID = " + id + ";";
    }

    @Override
    protected String findAllString() {
        return "SELECT * from HOMEWORK;";
    }

    @Override
    protected String insertString(Homework homework) {
        return "INSERT INTO HOMEWORK (id, description, startWeek, deadlineWeek) " +
                "VALUES (" + homework.getId()
                + ",'" + homework.getDescription()
                + "'," + homework.getStartWeek()
                + "," + homework.getDeadlineWeek()
                + ");";
    }

    @Override
    protected String deleteString(Integer id) {
        return "DELETE from HOMEWORK where ID = " + id + ";";
    }
}
