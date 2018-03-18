package tarun.djangorestclient.com.djangorestclient.model;

/**
 * Model class subclassing the Header class with more Basic Authentication related fields.
 */

public class AuthBasicHeader extends Header {

    private String userName;
    private String password;

    public AuthBasicHeader(String headerValue, String userName, String password) {
        super(HeaderType.AUTHORIZATION_BASIC, headerValue);
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
