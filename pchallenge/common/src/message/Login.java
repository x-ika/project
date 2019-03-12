package message;

import com.simplejcode.commons.net.csbase.Message;

public class Login extends Message {

    protected String login;

    protected String password;

    public Login(Object sender, String login, String password) {
        super(sender);
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
