package validation;

import domain.Professor;

public class ProfessorValidator implements Validator<Professor> {
    @Override
    public void validate(Professor s) throws ValidationException {
        String Errors = "";
        if (s.getEmail() == null || s.getFamilyName() == null || s.getFirstName() == null)
            throw new ValidationException("Null fields");
        if (s.getEmail().equals(""))
            Errors = Errors + "Emainul trebuie sa existe\n";
        if (s.getFamilyName().equals(""))
            Errors = Errors + "Numele trebuie sa existe\n";
        if (s.getFirstName().equals(""))
            Errors = Errors + "Prenumele trebuie sa existe\n";
        if (Errors.length() > 0)
            throw new ValidationException(Errors);
    }
}
