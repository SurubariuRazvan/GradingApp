package repository.sql;

import domain.User;
import ui.gui.CleranceLevel;
import validation.ValidationException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserPostgreSQLRepository {
    private final Connection c;

    public UserPostgreSQLRepository(Connection c) {
        this.c = c;
    }

    private String insertString(User user) {
        return "INSERT INTO \"user\" (\"username\", \"password\", \"cleranceLevel\") " +
                "VALUES ('" + user.getId()
                + "','" + user.getPassword()
                + "','" + user.getCleranceLevel().toString()
                + "'," + user.getUsernameID() + ");";
    }

    public User save(User entity) throws ValidationException {
        if (entity == null)
            throw new IllegalArgumentException("entity is null");
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

    private String findOneString(String username) {
        return "SELECT * from \"user\" where \"username\" = '" + username + "';";
    }

    private User readEntity(ResultSet result) throws SQLException {
        String username = result.getString("username");
        String password = result.getString("password");
        CleranceLevel cleranceLevel = CleranceLevel.valueOf(result.getString("cleranceLevel"));
        Integer usernameID = Integer.parseInt(result.getString("usernameID"));
        return new User(username, password, cleranceLevel, usernameID);
    }

    public User findUser(String username) {
        if (username == null)
            throw new IllegalArgumentException("username is null");
        User entity = null;
        try {
            Statement stmt = c.createStatement();
            var f = stmt.executeQuery(findOneString(username));
            if (f.next())
                entity = readEntity(f);
            f.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public void closeConnection() throws SQLException {
        c.close();
    }
}
