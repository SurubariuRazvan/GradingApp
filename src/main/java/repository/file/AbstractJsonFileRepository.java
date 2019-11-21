package repository.file;

import domain.Entity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import validation.Validator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class AbstractJsonFileRepository<ID, E extends Entity<ID>> extends AbstractInFileRepository<ID, E> {
    protected AbstractJsonFileRepository(Validator<E> validator, String fileName) {
        super(validator, fileName);
    }

    /**
     * converts a single entity from file to memory
     *
     * @param entity json simple entity format
     * @return a entity that can be stored in memory
     */
    protected abstract E readEntity(JSONObject entity);

    /**
     * converts a single entity from memory to file
     *
     * @param entity in memory entity
     * @return a json simple entity that can be stored in a file
     */
    protected abstract JSONObject writeEntity(E entity);

    /**
     * reads all entities from file and stores them in memory
     */
    void readEntities() {
        super.deleteAll();
        JSONParser jsonParser = new JSONParser();
        try {
            JSONArray entities = (JSONArray) jsonParser.parse(new FileReader(fileName));
            for(Object entity : entities) {
                E e = readEntity((JSONObject) entity);
                super.save(e);
            }
        } catch (FileNotFoundException ignored) {

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * writes all entities from memory to file
     */
    @SuppressWarnings("unchecked")
    void writeEntities() {
        JSONArray entities = new JSONArray();
        for(E entity : super.findAll())
            entities.add(writeEntity(entity));

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(entities.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
