package repository.file;

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
    protected Grade readEntity(JSONObject entity) {
        Integer studentId = ((Long) entity.get("studentId")).intValue();
        Integer homeworkId = ((Long) entity.get("homeworkId")).intValue();
        GradeId gradeId = new GradeId(homeworkId, studentId);
        Integer professorId = ((Long) entity.get("professorId")).intValue();
        LocalDate handOverDate = LocalDate.parse((String) entity.get("handOverDate"));
        Double givenGrade = (Double) entity.get("givenGrade");
        String feedback = (String) entity.get("feedback");
        return new Grade(gradeId, handOverDate, professorId, givenGrade, feedback);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected JSONObject writeEntity(Grade entity) {
        JSONObject o = new JSONObject();
        o.put("studentId", entity.getId().getStudentId());
        o.put("homeworkId", entity.getId().getHomeworkId());
        o.put("professorId", entity.getProfessorId());
        o.put("handOverDate", entity.getHandOverDate().toString());
        o.put("givenGrade", entity.getGivenGrade());
        o.put("feedback", entity.getFeedback());
        return o;
    }
}
