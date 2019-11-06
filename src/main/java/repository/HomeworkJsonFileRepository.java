package repository;

import domain.Homework;
import org.json.simple.JSONObject;
import validation.Validator;

public class HomeworkJsonFileRepository extends AbstractJsonFileRepository<Integer, Homework> {
    public HomeworkJsonFileRepository(Validator<Homework> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    Homework readEntity(JSONObject entity) {
        Integer id = ((Long) entity.get("id")).intValue();
        String description = (String) entity.get("description");
        Integer startWeek = ((Long) entity.get("startWeek")).intValue();
        Integer deadlineWeek = ((Long) entity.get("deadlineWeek")).intValue();
        return new Homework(id, description, startWeek, deadlineWeek);
    }

    @Override
    @SuppressWarnings("unchecked")
    JSONObject writeEntity(Homework entity) {
        JSONObject o = new JSONObject();
        o.put("id", entity.getId());
        o.put("description", entity.getDescription());
        o.put("startWeek", entity.getStartWeek());
        o.put("deadlineWeek", entity.getDeadlineWeek());
        return o;
    }
}
