package validation;

import domain.Student;

public class StudentValidator implements Validator<Student> {
    @Override
    public void validate(Student s) throws ValidationException {
        String Errors = "";
        if (s.getFirstName() == null || s.getFamilyName() == null || s.getEmail() == null || s.getGroup() == null)
            throw new ValidationException("Null fields");
        if (s.getEmail().equals(""))
            Errors = Errors + "Emainul trebuie sa existe\n";
        if (s.getFamilyName().equals(""))
            Errors = Errors + "Numele trebuie sa existe\n";
        if (s.getFirstName().equals(""))
            Errors = Errors + "Prenumele trebuie sa existe\n";
        if (s.getGroup() < 0)
            Errors = Errors + "Numarul grupei trebuie sa fie pozitiv\n";
        if (s.getLabProfessorId() == null)
            Errors = Errors + "Fiecare grupa trebuie sa aibe un profesor\n";
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
