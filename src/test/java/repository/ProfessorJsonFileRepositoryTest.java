package repository;

import domain.Professor;
import org.junit.jupiter.api.Test;
import repository.file.ProfessorJsonFileRepository;
import validation.ProfessorValidator;
import validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfessorJsonFileRepositoryTest {
    private final Validator<Professor> vali = new ProfessorValidator();

    @Test
    void writeAndReadEntity() {
        ProfessorJsonFileRepository repo = new ProfessorJsonFileRepository(vali, "./src/test/resources/Professor.json");
        Professor g = new Professor(0, "Ion", "Pop", "@yahoo.com");
        repo.deleteAll();
        repo.save(g);
        repo.saveAll();
        ProfessorJsonFileRepository repo2 = new ProfessorJsonFileRepository(vali, "./src/test/resources/Professor.json");
        assertEquals(repo2.findOne(0), g);
    }
}