package repository;

import domain.Grade;
import domain.GradeId;
import org.junit.jupiter.api.Test;
import repository.file.GradeJsonFileRepository;
import validation.GradeValidator;
import validation.Validator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GradeJsonFileRepositoryTest {
    private Validator<Grade> vali = new GradeValidator();

    @Test
    void writeAndReadEntity() {
        GradeJsonFileRepository repo = new GradeJsonFileRepository(vali, "./src/test/resources/Grade.json");
        GradeId id = new GradeId(5, 1);
        Grade g = new Grade(id, LocalDate.now(), 0, 6.3, 5, "mer");
        repo.deleteAll();
        repo.save(g);
        repo.saveAll();
        GradeJsonFileRepository repo2 = new GradeJsonFileRepository(vali, "./src/test/resources/Grade.json");
        assertEquals(repo2.findOne(id), g);
    }
}