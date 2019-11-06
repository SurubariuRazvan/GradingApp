package validation;

import domain.Homework;

public class HomeworkValidator implements Validator<Homework> {
    @Override
    public void validate(Homework t) throws ValidationException {
        String Errors = "";
        if (t.getDeadlineWeek() == null || t.getStartWeek() == null || t.getDescription() == null)
            throw new ValidationException("Null fields");
        if (t.getStartWeek() > 14 || t.getStartWeek() < 1)
            Errors = Errors + "Saptamana de inceput trebuie sa fie in intervalul [1,14]\n";
        if (t.getDeadlineWeek() > 14 || t.getDeadlineWeek() < 1)
            Errors = Errors + "Saptamana de finalizat trebuie sa fie in intervalul [1,14]\n";
        if (t.getStartWeek() >= t.getDeadlineWeek())
            Errors = Errors + "Saptamana de inceput trebuie sa fie inainte de saptamana de finalizat\n";
        if (Errors.length() > 0)
            throw new ValidationException(Errors);
    }
}
