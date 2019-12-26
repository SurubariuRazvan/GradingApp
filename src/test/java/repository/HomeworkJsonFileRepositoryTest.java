package repository;

import domain.Homework;
import org.junit.jupiter.api.Test;
import repository.file.HomeworkJsonFileRepository;
import validation.HomeworkValidator;
import validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeworkJsonFileRepositoryTest {
    private final Validator<Homework> vali = new HomeworkValidator();

    @Test
    void writeAndReadEntity() {
        HomeworkJsonFileRepository repo = new HomeworkJsonFileRepository(vali, "./src/test/resources/Homework.json");
        Homework g = new Homework(0, "tema 1", 5, 8);
        repo.deleteAll();
        repo.save(g);
        repo.saveAll();
        HomeworkJsonFileRepository repo2 = new HomeworkJsonFileRepository(vali, "./src/test/resources/Homework.json");
        assertEquals(repo2.findOne(0), g);
    }
}