package validation;

import domain.Homework;
import org.junit.jupiter.api.Test;

class HomeworkValidatorTest {
    private Validator<Homework> vali = new HomeworkValidator();

    @Test
    void validate1() {
        try {
            vali.validate(new Homework(1, "yuyu", 7, 6));
            assert (false);
        } catch (Exception e) {
            assert (e.getMessage().equals("Saptamana de inceput trebuie sa fie inainte de saptamana de finalizat\n"));
        }
    }

    @Test
    void validate2() {
        try {
            vali.validate(new Homework(1, "yuu", 70, 6));
            assert (false);
        } catch (Exception e) {
            assert (e.getMessage().equals("Saptamana de inceput trebuie sa fie in intervalul [1,14]\n" +
                    "Saptamana de inceput trebuie sa fie inainte de saptamana de finalizat\n"));
        }
    }

    @Test
    void validate3() {
        try {
            vali.validate(new Homework(1, "yu", 76, 80));
            assert (false);
        } catch (Exception e) {
            assert (e.getMessage().equals("Saptamana de inceput trebuie sa fie in intervalul [1,14]\n" +
                    "Saptamana de finalizat trebuie sa fie in intervalul [1,14]\n"));
        }
    }

    @Test
    void validate4() {
        try {
            vali.validate(new Homework(1, "yu", 5, 80));
            assert (false);
        } catch (Exception e) {
            assert (e.getMessage().equals("Saptamana de finalizat trebuie sa fie in intervalul [1,14]\n"));
        }
    }

    @Test
    void validate5() {
        try {
            vali.validate(new Homework(1, "yuyu", 7, 8));
            assert (true);
        } catch (Exception e) {
            assert (false);
        }

    }

}