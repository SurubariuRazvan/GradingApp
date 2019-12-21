package repository.sql;

import domain.Entity;
import repository.CrudRepository;
import repository.RepositoryException;
import validation.ValidationException;
import validation.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// extends PagingAndSortingRepository<E, Long>
public abstract class AbstractPostgreSQLRepository<ID, E extends Entity<ID>> implements CrudRepository<ID, E> {
    private Connection c;
    private Validator<E> validator;

    AbstractPostgreSQLRepository(Validator<E> validator, String url, String user, String password) throws SQLException, ClassNotFoundException {
        this.validator = validator;
        Class.forName("org.postgresql.Driver");
        c = DriverManager.getConnection(url,user,password);
        //c.close();
    }

    protected abstract E readEntity(ResultSet result) throws SQLException;

    @Override
    public E findOne(ID id) {
        if (id == null)
            throw new IllegalArgumentException("id is null");
        E entity = null;
        try {
            Statement stmt = c.createStatement();
            var f = stmt.executeQuery(findOneString(id));
            if (f.next())
                entity = readEntity(f);
            f.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    protected abstract String findOneString(ID id);

    @Override
    public Iterable<E> findAll() {
        List<E> entities = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            var f = stmt.executeQuery(findAllString());
            while(f.next())
                entities.add(readEntity(f));
            f.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entities;
    }

    protected abstract String findAllString();

    @Override
    public E save(E entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("entity is null");
        validator.validate(entity);
        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(insertString(entity));
            stmt.close();
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate"))
                return entity;
            e.printStackTrace();
        }
        return null;
    }

    protected abstract String insertString(E entity);

    @Override
    public E delete(ID id) {
        if (id == null)
            throw new IllegalArgumentException("id is null");
        try {
            Statement stmt = c.createStatement();
            E entity = this.findOne(id);
            if (entity != null) {
                stmt.executeUpdate(deleteString(id));
                stmt.close();
                return entity;
            }
            stmt.close();
        } catch (SQLException e) {
            if (e.getMessage().contains("violates foreign key constraint"))
                throw new RepositoryException("Apare in alt tabel.");
            e.printStackTrace();
        }
        return null;
    }

    protected abstract String deleteString(ID id);

    @Override
    public E update(E entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity is null");
        validator.validate(entity);
        try {
            Statement stmt = c.createStatement();
            int action = stmt.executeUpdate(updateString(entity));
            stmt.close();
            if (action == 0)
                return entity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract String updateString(E entity);
}
