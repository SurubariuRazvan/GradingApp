package repository;

import domain.Entity;
import validation.ValidationException;
import validation.Validator;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements CrudRepository<ID, E> {
    private final Map<ID, E> data = new HashMap<>();
    private final Validator<E> validator;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
    }

    @Override
    public E findOne(ID id) {
        if (id == null)
            throw new IllegalArgumentException("id is null");
        return data.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return data.values();
    }

    @Override
    public E save(E entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("entity is null");
        validator.validate(entity);
        return data.put(entity.getId(), entity);
    }

    @Override
    public E delete(ID id) {
        if (id == null)
            throw new IllegalArgumentException("id is null");
        return data.remove(id);
    }

    @Override
    public E update(E entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity is null");
        validator.validate(entity);
        if (data.replace(entity.getId(), entity) == null)
            return entity;
        return null;
    }

    /**
     * removes all entities from memory
     */
    protected void deleteAll() {
        data.clear();
    }
}
