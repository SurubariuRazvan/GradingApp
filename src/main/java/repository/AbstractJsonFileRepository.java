package repository;

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

public abstract class AbstractJsonFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String fileName;

    AbstractJsonFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        readEntities();
    }

    /**
     * converts a single entity from file to memory
     *
     * @param entity json simple entity format
     * @return a entity that can be stored in memory
     */
    abstract E readEntity(JSONObject entity);

    /**
     * converts a single entity from memory to file
     *
     * @param entity in memory entity
     * @return a json simple entity that can be stored in a file
     */
    abstract JSONObject writeEntity(E entity);

    /**
     * reads all entities from file and stores them in memory
     */
    private void readEntities() {
        super.deleteAll();
        JSONParser jsonParser = new JSONParser();
        try {
            JSONArray entities = (JSONArray) jsonParser.parse(new FileReader(fileName));
            for (Object entity : entities) {
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
    private void writeEntities() {
        JSONArray entities = new JSONArray();
        for (E entity : super.findAll())
            entities.add(writeEntity(entity));

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(entities.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * deletes all entities from memory and file
     */
    @Override
    public void deleteAll() {
        super.deleteAll();
        writeEntities();
    }

    /**
     * saves all the entities from memory to file
     */
    public void saveAll() {
        writeEntities();
    }
}
