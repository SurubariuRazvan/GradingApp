package validation;

import domain.Student;

public class StudentValidator implements Validator<Student> {
    @Override
    public void validate(Student s) throws ValidationException {
        String Errors = "";
        if (s.getFirstName() == null || s.getFamilyName() == null || s.getEmail() == null || s.getGroup() == null || s.getLabProfessorId() == null)
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
        if (Errors.length() > 0)
            throw new ValidationException(Errors);
    }
}
