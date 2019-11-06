package repository;

import domain.Grade;
import domain.GradeId;
import org.json.simple.JSONObject;
import validation.Validator;

import java.time.LocalDate;

public class GradeJsonFileRepository extends AbstractJsonFileRepository<GradeId, Grade> {
    public GradeJsonFileRepository(Validator<Grade> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    Grade readEntity(JSONObject entity) {
        Integer studentId = ((Long) entity.get("studentId")).intValue();
        Integer homeworkId = ((Long) entity.get("homeworkId")).intValue();
        Integer professorId = ((Long) entity.get("professorId")).intValue();
        GradeId gradeId = new GradeId(homeworkId, studentId);
        LocalDate handOverDate = LocalDate.parse((String) entity.get("handOverDate"));
        Double givenGrade = (Double) entity.get("givenGrade");
        String feedback = (String) entity.get("feedback");
        return new Grade(gradeId, handOverDate, professorId, givenGrade, homeworkId, feedback);
    }

    @Override
    @SuppressWarnings("unchecked")
    JSONObject writeEntity(Grade entity) {
        JSONObject o = new JSONObject();
        o.put("studentId", entity.getId().getStudentId());
        o.put("homeworkId", entity.getHomeworkId());
        o.put("professorId", entity.getProfessorId());
        o.put("handOverDate", entity.getHandOverDate().toString());
        o.put("givenGrade", entity.getGivenGrade());
        o.put("feedback", entity.getFeedback());
        return o;
    }
}
