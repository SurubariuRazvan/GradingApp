package validation;

import domain.Professor;

public class ProfessorValidator implements Validator<Professor> {
    @Override
    public void validate(Professor s) throws ValidationException {
        String Errors = "";
        if (s.getEmail() == null || s.getFamilyName() == null || s.getFirstName() == null)
            throw new ValidationException("Null fields");
        if (s.getEmail().equals(""))
            Errors = Errors + "Emailul trebuie sa existe\n";
        if (s.getFamilyName().equals(""))
            Errors = Errors + "Numele trebuie sa existe\n";
        if (s.getFirstName().equals(""))
            Errors = Errors + "Prenumele trebuie sa existe\n";
        if (s.getEmail().length() > 45)
            Errors = Errors + "Emailul trebuie sa aibe sub 45 de caractere\n";
        if (s.getFamilyName().length() > 45)
            Errors = Errors + "Numele trebuie sa aibe sub 45 de caractere\n";
        if (s.getFirstName().length() > 95)
            Errors = Errors + "Prenumele trebuie sa aibe sub 95 de caractere\n";
        if (Errors.length() > 0)
            throw new ValidationException(Errors);
    }
}
