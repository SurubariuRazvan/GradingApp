package repository.sql;

import domain.Grade;
import domain.GradeId;
import validation.Validator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class GradePostgreSQLRepository extends AbstractPostgreSQLRepository<GradeId, Grade> {
    public GradePostgreSQLRepository(Validator<Grade> validator, String url, String user, String password) throws SQLException, ClassNotFoundException {
        super(validator, url, user, password);
    }

    @Override
    protected Grade readEntity(ResultSet result) throws SQLException {
        Integer studentId = result.getInt("studentId");
        Integer homeworkId = result.getInt("homeworkId");
        GradeId gradeId = new GradeId(homeworkId, studentId);
        Integer professorId = result.getInt("professorId");
        LocalDate handOverDate = LocalDate.parse(result.getString("handOverDate"));
        Double givenGrade = result.getDouble("givenGrade");
        String feedback = result.getString("feedback");
        return new Grade(gradeId, handOverDate, professorId, givenGrade, feedback);
    }

    @Override
    protected String findOneString(GradeId id) {
        return "SELECT * from GRADE where studentID = " + id.getStudentId() + "and homeworkID = " + id.getHomeworkId() + ";";
    }

    @Override
    protected String findAllString() {
        return "SELECT * from GRADE;";
    }

    @Override
    protected String insertString(Grade grade) {
        return "INSERT INTO GRADE (studentId, homeworkId, professorId, handOverDate, givenGrade, feedback ) " +
                "VALUES (" + grade.getId().getStudentId()
                + "," + grade.getId().getHomeworkId()
                + "," + grade.getProfessorId()
                + ",'" + grade.getHandOverDate().toString()
                + "'," + grade.getGivenGrade()
                + ",'" + grade.getFeedback() + "');";
    }

    @Override
    protected String deleteString(GradeId id) {
        return "DELETE from GRADE where studentID = " + id.getStudentId() + "and homeworkID = " + id.getHomeworkId() + ";";
    }

    @Override
    protected String updateString(Grade grade) {
        return "UPDATE GRADE SET "
                + "studentId= " + grade.getId().getStudentId()
                + ", homeworkId= " + grade.getId().getHomeworkId()
                + ", professorId= " + grade.getProfessorId()
                + ", handOverDate= '" + grade.getHandOverDate().toString()
                + "', givenGrade= " + grade.getGivenGrade()
                + ", feedback= '" + grade.getFeedback()
                + "' where studentID = " + grade.getId().getStudentId()
                + "and homeworkID = " + grade.getId().getHomeworkId() + ";";
    }
}
