package guessmypic.gmp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by HONGWEI on 2018/9/18.
 */

public class UserManager {

    private static final String SHARED_PREF_NAME = "fcm";
    private static final String KEY_ACCESS_TOKEN = "token";
    private static final String KEY_ACCESS_NAME = "name";
    private static final String KEY_ACCESS_COIN = "coin";
    private static Context context;
    private static UserManager userManager;
    private UserManager(Context c){
        context = c;
    }
    public static synchronized UserManager getInstance(Context c){
        if(userManager == null)
            userManager = new UserManager(c);
        return userManager;
    }
    public boolean storeToken(String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN,token);
        editor.apply();
        return true;
    }
    public boolean storeUserInfo(User user){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_NAME,user.getName());
        editor.putInt(KEY_ACCESS_COIN,user.getCoin());
        editor.apply();
        return true;
    }

    public String getToken(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN,null);
    }

    public User getUserInfo(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        User user = new User(sharedPreferences.getString(KEY_ACCESS_NAME,null),sharedPreferences.getInt(KEY_ACCESS_COIN,0));
        return user;
    }
}
