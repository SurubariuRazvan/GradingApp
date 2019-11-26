package service;

import domain.Professor;
import domain.UniversityYearStructure;
import repository.CrudRepository;
import validation.ValidationException;
import validation.Validator;

public class ProfessorService extends Service<Integer, Professor> {
    private Integer lastId;

    public ProfessorService(CrudRepository<Integer, Professor> repo, Validator<Professor> vali, UniversityYearStructure year) {
        super(repo, vali, year);
        this.lastId = idSetUp();
    }

    /**
     * @return the last id used for a student when the service is created
     */
    private Integer idSetUp() {
        Integer max = 0;
        for(var i : super.findAll())
            max = i.getId() > max ? i.getId() : max;
        return max;
    }

    public Integer getNextId() {
        return lastId + 1;
    }

    /**
     * creates a new Professor object, auto-generating the id
     *
     * @param familyName Professor field
     * @param firstName  Professor field
     * @param email      Professor field
     * @return a new Professor object
     */
    public Professor createProfessor(String familyName, String firstName, String email) {
        Professor professor = new Professor(getNextId(), familyName, firstName, email);
        vali.validate(professor);
        return professor;
    }

    @Override
    public void save(Professor entity) throws ValidationException {
        super.save(entity);
        lastId++;
    }
}
