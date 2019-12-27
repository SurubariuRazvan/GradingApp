package domain;

import org.apache.commons.codec.digest.DigestUtils;
import ui.gui.CleranceLevel;

public class User extends Entity<String> {
    private String password;
    private CleranceLevel cleranceLevel;
    private Integer usernameID;

    public User(String username, String password, CleranceLevel cleranceLevel, Integer usernameID) {
        super(username);
        this.password = password;
        this.cleranceLevel = cleranceLevel;
        this.usernameID = usernameID;
    }

    public static String encodePassword(String password) {
        return DigestUtils.md5Hex(password);
    }

    public CleranceLevel getCleranceLevel() {
        return cleranceLevel;
    }

    public void setCleranceLevel(CleranceLevel cleranceLevel) {
        this.cleranceLevel = cleranceLevel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUsernameID() {
        return usernameID;
    }

    public void setUsernameID(Integer usernameID) {
        this.usernameID = usernameID;
    }
}
