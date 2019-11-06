package validation;

public interface Validator<E> {
    /**
     * @param entity the entity to be validated
     * @throws ValidationException if the entity doesnt meet the specified characteristics
     */
    void validate(E entity) throws ValidationException;
}
