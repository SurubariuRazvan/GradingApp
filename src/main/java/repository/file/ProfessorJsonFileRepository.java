package repository.file;

import domain.Professor;
import org.json.simple.JSONObject;
import validation.Validator;

public class ProfessorJsonFileRepository extends AbstractJsonFileRepository<Integer, Professor> {
    public ProfessorJsonFileRepository(Validator<Professor> validator, String fileName) {
        super(validator, fileName);
    }

    /**
     * converts a single Professor object from file to memory
     *
     * @param entity json simple entity format
     * @return new Professor object
     */
    @Override
    protected Professor readEntity(JSONObject entity) {
        Integer id = ((Long) entity.get("id")).intValue();
        String familyName = (String) entity.get("familyName");
        String firstName = (String) entity.get("firstName");
        String email = (String) entity.get("email");
        return new Professor(id, familyName, firstName, email);
    }

    /**
     * converts a single Professor object from memory to file
     *
     * @param entity Professor object from memory
     * @return new json simple format entity
     */
    @Override
    @SuppressWarnings("unchecked")
    protected JSONObject writeEntity(Professor entity) {
        JSONObject o = new JSONObject();
        o.put("id", entity.getId());
        o.put("familyName", entity.getFamilyName());
        o.put("firstName", entity.getFirstName());
        o.put("email", entity.getEmail());
        return o;
    }
}
