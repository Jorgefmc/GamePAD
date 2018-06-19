package gamepad.pad;

/**
 * Created by Ynscription on 19/06/2018.
 */

public class LoginException extends Exception {
    private boolean incorrectLogin = false;
    private boolean usedEmail = false;
    private boolean usedName = false;
    private boolean other = false;

    public static final int INCORRECT_LOGIN = 0;
    public static final int USED_EMAIL = 1;
    public static final int USED_NAME = 2;
    public static final int OTHER = 3;


    public boolean isUsedEmail() {
        return usedEmail;
    }

    public boolean isUsedName() {
        return usedName;
    }

    public boolean isIncorrectLogin() {
        return incorrectLogin;
    }

    public boolean isOther() {
        return other;
    }

    public LoginException (String message, int cause) {
        super(message);
        incorrectLogin = cause == INCORRECT_LOGIN;
        usedEmail = cause == USED_EMAIL;
        usedName = cause == USED_NAME;
        other = cause == OTHER;
    }



}
