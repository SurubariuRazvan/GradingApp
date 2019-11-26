package validation;

import domain.Grade;

public class GradeValidator implements Validator<Grade> {
    @Override
    public void validate(Grade n) throws ValidationException {
        String Errors = "";
        if (n.getGivenGrade() == null || n.getFeedback() == null || n.getHandOverDate() == null || n.getProfessorId() == null)
            throw new ValidationException("Null fields");
        if (n.getGivenGrade() < 1 || n.getGivenGrade() > 10)
            Errors = Errors + "Nota trebuie sa fie in intervalul [1,10]\n";

        if (Errors.length() > 0)
            throw new ValidationException(Errors);
    }
}
