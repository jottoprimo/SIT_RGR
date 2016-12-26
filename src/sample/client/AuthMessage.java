package sample.client;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class AuthMessage implements Message {

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean IsSignIn() {
        return isSignIn;
    }

    public void SignIn(Boolean signIn) {
        this.isSignIn = signIn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status{
        SUCCESS,
        WRONG_PASSW,
        LOGIN_REQUIRED,
        FAIL, UNKNOW
    }

    String user;
    String password;

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    int role;
    boolean isSignIn;
    Status status;

    public AuthMessage(String user, String password, boolean isSignIn){
        this.user = user;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-16"));
            byte[] digest = md.digest();
            password = String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.password = password;
        this.isSignIn = isSignIn;
    }

}
