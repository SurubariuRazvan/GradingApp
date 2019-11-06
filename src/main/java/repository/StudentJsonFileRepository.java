package repository;

import domain.Student;
import org.json.simple.JSONObject;
import validation.Validator;

public class StudentJsonFileRepository extends AbstractJsonFileRepository<Integer, Student> {
    public StudentJsonFileRepository(Validator<Student> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    Student readEntity(JSONObject entity) {
        Integer id = ((Long) entity.get("id")).intValue();
        String familyName = (String) entity.get("familyName");
        String firstName = (String) entity.get("firstName");
        Integer group = ((Long) entity.get("group")).intValue();
        String email = (String) entity.get("email");
        Integer labProfessorId = ((Long) entity.get("labProfessorId")).intValue();
        return new Student(id, familyName, firstName, group, email, labProfessorId);
    }

    @Override
    @SuppressWarnings("unchecked")
    JSONObject writeEntity(Student entity) {
        JSONObject o = new JSONObject();
        o.put("id", entity.getId());
        o.put("familyName", entity.getFamilyName());
        o.put("firstName", entity.getFirstName());
        o.put("group", entity.getGroup());
        o.put("email", entity.getEmail());
        o.put("labProfessorId", entity.getLabProfessorId());
        return o;
    }
}
