package repository;

import domain.Student;
import org.junit.jupiter.api.Test;
import repository.file.StudentJsonFileRepository;
import validation.StudentValidator;
import validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudentJsonFileRepositoryTest {
    private final Validator<Student> vali = new StudentValidator();

    @Test
    void writeAndReadEntity() {
        StudentJsonFileRepository repo = new StudentJsonFileRepository(vali, "./src/test/resources/Student.json");
        Student g = new Student(0, "Ion", "Pop", 234, "@yahoo.com", 7);
        repo.deleteAll();
        repo.save(g);
        repo.saveAll();
        StudentJsonFileRepository repo2 = new StudentJsonFileRepository(vali, "./src/test/resources/Student.json");
        assertEquals(repo2.findOne(0), g);
    }
}