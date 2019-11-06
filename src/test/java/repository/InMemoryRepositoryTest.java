package repository;

import domain.Homework;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.HomeworkValidator;
import validation.ValidationException;

class InMemoryRepositoryTest {
    private static HomeworkValidator vali;
    private static Homework t1;
    private static Homework t2;
    private InMemoryRepository<Integer, Homework> repo;

    @BeforeAll
    static void setAll() {
        t1 = new Homework(0, "abc", 5, 7);
        t2 = new Homework(1, "a", 8, 9);
        vali = new HomeworkValidator();
    }

    @BeforeEach
    void setUp() {
        repo = new InMemoryRepository<>(vali);
        repo.save(t1);
        repo.save(t2);
    }

    @Test
    void findOne1() {
        try {
            Homework t = repo.findOne(0);
            assert (t.equals(t1));
        } catch (Exception e) {
            assert (false);
        }
    }

    @Test
    void findOne2() {
        try {
            Homework t = repo.findOne(null);
            assert (false);
        } catch (IllegalArgumentException e) {
            assert (true);
        }
    }

    @Test
    void findAll() {
        for (Homework t : repo.findAll())
            assert t.equals(t1) || t.equals(t2);
    }

    @Test
    void save1() {
        try {
            repo.save(null);
            assert (false);
        } catch (IllegalArgumentException e) {
            assert (true);
        }
    }

    @Test
    void save2() {
        Homework t3 = new Homework(2, "tg", 5, 8);
        assert (repo.save(t3) == null);
        assert (repo.save(t3).equals(t3));
    }

    @Test
    void save3() {
        Homework t3 = new Homework(2, "tg", 5, 5);
        try {
            repo.save(t3);
            assert (false);
        } catch (ValidationException e) {
            assert (true);
        }
    }

    @Test
    void delete1() {
        try {
            repo.delete(null);
            assert (false);
        } catch (IllegalArgumentException e) {
            assert (true);
        }
    }

    @Test
    void delete2() {
        try {
            assert (repo.delete(1).equals(t2));
            for (Homework t : repo.findAll())
                assert t.getId().equals(0);
        } catch (IllegalArgumentException e) {
            assert (true);
        }
    }

    @Test
    void delete3() {
        try {
            assert (repo.delete(3) == null);
        } catch (Exception e) {
            assert (false);
        }
    }

    @Test
    void update1() {
        try {
            repo.update(null);
            assert (false);
        } catch (IllegalArgumentException e) {
            assert (true);
        }
    }

    @Test
    void update2() {
        Homework t0 = new Homework(0, "ab", 6, 7);
        try {
            assert (repo.update(t0) == null);
            assert (repo.findOne(0).equals(t0));
        } catch (Exception e) {
            assert (false);
        }
    }

    @Test
    void update3() {
        Homework t0 = new Homework(5, "ab", 6, 7);
        try {
            assert (repo.update(t0).equals(t0));
        } catch (Exception e) {
            assert (false);
        }
    }

    @Test
    void update4() {
        Homework t0 = new Homework(5, "ab", 8, 7);
        try {
            repo.update(t0);
            assert (false);
        } catch (ValidationException e) {
            assert (true);
        }
    }

    @Test
    void deleteAll() {
        repo.deleteAll();
        assert (!repo.findAll().iterator().hasNext());
    }
}