package service;

import domain.Homework;
import domain.UniversityYearStructure;
import repository.CrudRepository;
import validation.Validator;

import java.time.LocalDate;

public class HomeworkService extends Service<Integer, Homework> {
    private Integer lastId;

    public HomeworkService(CrudRepository<Integer, Homework> repo, Validator<Homework> vali, UniversityYearStructure year) {
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
     * creates a new Homework object, auto-generating the fields id and startingWeek
     *
     * @param description  Homework field
     * @param deadlineWeek Homework field
     * @return a new Homework object
     */
    public Homework createHomework(String description, Integer deadlineWeek) {
        Homework homework = new Homework(++lastId, description, year.getWeek(LocalDate.now()), deadlineWeek);
        vali.validate(homework);
        return homework;
    }
}
