package e.user301.worksininternet;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPrefereces {
    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_LAST_RESULT = "lastResultId";

    public static String getStoreQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, null);
    }

    public static void setStoreQuery(Context context, String query){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY,query)
                .apply();
    }

    public static String getLastResult (Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_RESULT, null);
    }

    public static void setLastResult (Context context, String lastResultId){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_RESULT, lastResultId)
                .apply();
    }
}
