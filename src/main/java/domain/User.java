package domain;

import org.apache.commons.codec.digest.DigestUtils;
import ui.gui.CleranceLevel;

public class User extends Entity<String> {
    private String password;
    private CleranceLevel cleranceLevel;

    public User(String username, String password, CleranceLevel cleranceLevel) {
        super(username);
        this.password = password;
        this.cleranceLevel = cleranceLevel;
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

    public static String encodePassword(String password) {
        return DigestUtils.md5Hex(password);
    }

}
