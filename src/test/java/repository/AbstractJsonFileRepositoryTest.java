package repository;

import domain.Entity;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import validation.ValidationException;
import validation.Validator;

class AbstractJsonFileRepositoryTest {
    private static AbstractJsonFileRepository<Integer, Entity<Integer>> repo;

    @BeforeAll
    static void setUp() {
        var vali = new Validator<Entity<Integer>>() {
            @Override
            public void validate(Entity<Integer> entity) throws ValidationException {

            }
        };
        repo = new AbstractJsonFileRepository<>(vali, "./src/test/resources/AbstractJsonFileRepositoryTest.json") {
            @Override
            Entity<Integer> readEntity(JSONObject entity) {
                Integer id = ((Long) entity.get("id")).intValue();
                return new Entity<>(id);
            }

            @Override
            @SuppressWarnings("unchecked")
            JSONObject writeEntity(Entity<Integer> entity) {
                JSONObject o = new JSONObject();
                o.put("id", entity.getId());
                return o;
            }
        };
    }

    @Test
    void writeAndReadEntitiesTest() {
        repo.save(new Entity<>(0));
        repo.save(new Entity<>(1));
        repo.save(new Entity<>(2));
        repo.saveAll();
        repo.delete(0);
        repo.delete(1);
        repo.delete(2);
        for (var i : repo.findAll())
            assert (i.getId() == 0 || i.getId() == 1 || i.getId() == 2);
    }
}