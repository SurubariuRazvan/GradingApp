package validation;

import domain.Professor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProfessorValidatorTest {
    private Validator<Professor> vali = new ProfessorValidator();

    @Test
    void validate() {
        Assertions.assertDoesNotThrow(() -> vali.validate(new Professor("pop", "ion", "@hotmail.com")));
        Assertions.assertThrows(ValidationException.class, () -> vali.validate(new Professor("", "", "")), "Emainul trebuie sa existe\nNumele trebuie sa existe\nPrenumele trebuie sa existe\n");
        Assertions.assertThrows(ValidationException.class, () -> vali.validate(new Professor(null, null, "")), "Null fields");
    }


}