package validation;

import domain.Student;
import org.junit.jupiter.api.Test;

class StudentValidatorTest {
    private Validator<Student> vali = new StudentValidator();

    @Test
    void validate() {
        try {
            vali.validate(new Student(0, "Pop", "Andrei", 215, "Pop.Andrei@gmail.com", 0));
            assert (true);
        } catch (ValidationException e) {
            assert (false);
        }
        try {
            vali.validate(new Student(0, "", "", -4, "", null));
            assert (false);
        } catch (ValidationException e) {
            assert (true);
        }
    }
}