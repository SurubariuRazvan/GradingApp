package service;

import domain.Entity;
import domain.UniversityYearStructure;
import repository.AbstractJsonFileRepository;
import repository.CrudRepository;
import validation.ValidationException;
import validation.Validator;

public abstract class Service<ID, E extends Entity<ID>> {
    UniversityYearStructure year;
    Validator<E> vali;
    private CrudRepository<ID, E> repo;

    Service(CrudRepository<ID, E> repo, Validator<E> vali, UniversityYearStructure year) {
        this.year = year;
        this.repo = repo;
        this.vali = vali;
    }

    /**
     * returns the entity with the given id or throws an exception
     * if there isn't an entity with the given id
     *
     * @param id of the entity
     * @return the entity with the given id
     * @throws ValidationException if the entity isn't in the repo
     */
    public E findOne(ID id) throws ValidationException {
        E e = repo.findOne(id);
        if (e == null)
            throw new ValidationException("Entitate inexistenta");
        return e;
    }

    /**
     * @return all entities from the repo
     */
    public Iterable<E> findAll() {
        return repo.findAll();
    }

    /**
     * saves the entity or throws an exception if an entity with the given id already exists
     *
     * @param entity to be saved
     * @throws ValidationException if the entity already exists in the repo
     */
    public void save(E entity) throws ValidationException {
        if (repo.save(entity) != null)
            throw new ValidationException("Entitate deja existenta");
    }

    /**
     * deletes the entity with the given id or throws an exception
     * if there isn't an entity with the given id
     *
     * @param id of the entity to be deleted
     * @throws ValidationException if the entity isn't in the repo
     */
    public void delete(ID id) throws ValidationException {
        if (repo.delete(id) == null)
            throw new ValidationException("Entitate inexistenta");
    }

    /**
     * updates the entity or throws an exception if there isn't an entity with the given id
     *
     * @param entity to be updated
     * @throws ValidationException if the entity isn't in the repo
     */
    public void update(E entity) throws ValidationException {
        if (repo.update(entity) != null)
            throw new ValidationException("Entitate inexistenta");
    }

    /**
     * saves the entities from memory to file if the repo is in file
     */
    public void saveAll() {
        if (repo instanceof AbstractJsonFileRepository)
            ((AbstractJsonFileRepository<ID, E>) repo).saveAll();
    }
}
