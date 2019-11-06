package service;

import domain.Professor;
import domain.UniversityYearStructure;
import repository.CrudRepository;
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
        for (var i : super.findAll())
            if (i.getId() > max)
                max = i.getId();
        return max;
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
        Professor professor = new Professor(++lastId, familyName, firstName, email);
        vali.validate(professor);
        return professor;
    }
}
