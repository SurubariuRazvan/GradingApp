package validation;

import domain.Grade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class GradeValidatorTest {
    private Validator<Grade> vali = new GradeValidator();

    @Test
    void validate() {
        Assertions.assertDoesNotThrow(() -> vali.validate(new Grade(LocalDate.now(), 0, 6.8, "merge")));
        Assertions.assertThrows(ValidationException.class, () -> vali.validate(new Grade(LocalDate.now(), 0, 12.3, "merge")), "Nota trebuie sa fie in intervalul [1,10]\n");
        Assertions.assertThrows(ValidationException.class, () -> vali.validate(new Grade(null, null, null, null)), "Null fields");
    }

}