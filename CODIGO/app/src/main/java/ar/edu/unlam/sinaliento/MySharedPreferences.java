package ar.edu.unlam.sinaliento;
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

}
