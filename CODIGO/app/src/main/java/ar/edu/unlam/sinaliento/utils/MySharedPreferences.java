package ar.edu.unlam.sinaliento.utils;
import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private static MySharedPreferences mySharedPreferences;
    private Context context;

    private MySharedPreferences(Context context) {
        this.context = context;
    }

    public static MySharedPreferences getSharedPreferences(Context context) {
        if (mySharedPreferences == null) {
            mySharedPreferences = new MySharedPreferences(context);
        }

        return mySharedPreferences;
    }

    private final static String PREFS_NAME = "tpandroidsoa_prefs";

    private final static String SAVE_PATTERN_KEY = "pattern_code";
    private final static String NOT_EXISTS = "Not exists";

    private final static String TOKEN_KEY = "token";
    private final static String TOKEN_REFRESH_KEY = "token_refresh";

    private final static String EMAIL_KEY = "email";
    private final static String ADDITIONAL_EMAIL_KEY = "additional_email";
    private final static String PHONE_KEY = "phone";

    private final static String ENABLE_BEEP_KEY = "enable_beep";
    private final static String ENABLE_EMAIL_KEY = "enable_email";
    private final static String ENABLE_ADDITIONAL_EMAIL_KEY = "enable_additional_email";
    private final static String ENABLE_PHONE_KEY = "enable_phone";

    /*
        Pattern
     */
    public String getPattern() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(SAVE_PATTERN_KEY, NOT_EXISTS);
    }

    public void setPattern(final String PATTERN) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SAVE_PATTERN_KEY, PATTERN);
        editor.apply();
    }

    public boolean patternExists() {
        return !getPattern().equals(NOT_EXISTS);
    }

    /*
        Token
     */
    public String getToken() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN_KEY, NOT_EXISTS);
    }

    public void setToken(final String TOKEN) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN_KEY, TOKEN);
        editor.apply();
    }

    /*
        Token Refresh
     */
    public String getTokenRefresh() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN_REFRESH_KEY, NOT_EXISTS);
    }

    public void setTokenRefresh(final String TOKEN_REFRESH) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN_REFRESH_KEY, TOKEN_REFRESH);
        editor.apply();
    }

    /*
        Phone
     */
    public String getPhone() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PHONE_KEY, NOT_EXISTS);
    }

    public void setPhone(final String phone) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PHONE_KEY, phone);
        editor.apply();
    }

    public boolean phoneExists() {
        return !getPhone().equals(NOT_EXISTS);
    }

    /*
        Email
     */
    public String getEmail() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(EMAIL_KEY, NOT_EXISTS);
    }

    public void setEmail(final String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(EMAIL_KEY, email);
        editor.apply();
    }

    /*
        Additional Email
     */
    public String getAdditionalEmail() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(ADDITIONAL_EMAIL_KEY, NOT_EXISTS);
    }

    public void setAdditionalEmail(final String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ADDITIONAL_EMAIL_KEY, email);
        editor.apply();
    }

    public boolean additionalEmailExists() {
        return !getAdditionalEmail().equals(NOT_EXISTS);
    }

    /*
        Phone
     */
    public Boolean isEnablePhone() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(ENABLE_PHONE_KEY, false);
    }

    public void setEnablePhone(final Boolean enablePhone) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ENABLE_PHONE_KEY, enablePhone);
        editor.apply();
    }

    /*
        Enable Email
     */
    public Boolean isEnableEmail() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(ENABLE_EMAIL_KEY, false);
    }

    public void setEnableEmail(final Boolean enableEmail) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ENABLE_EMAIL_KEY, enableEmail);
        editor.apply();
    }

    /*
        Enable Additional Email
     */
    public Boolean isEnableAdditionalEmail() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(ENABLE_ADDITIONAL_EMAIL_KEY, false);
    }

    public void setEnableAdditionalEmail(final Boolean enableAdditionalEmail) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ENABLE_ADDITIONAL_EMAIL_KEY, enableAdditionalEmail);
        editor.apply();
    }

    /*
        Enable Beep
     */
    public Boolean isEnableBeep() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(ENABLE_BEEP_KEY, false);
    }

    public void setEnableBeep(final Boolean enableBeep) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ENABLE_BEEP_KEY, enableBeep);
        editor.apply();
    }

}
