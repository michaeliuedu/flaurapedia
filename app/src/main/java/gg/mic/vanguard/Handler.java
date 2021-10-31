package gg.mic.vanguard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Handler {

    private static DatabaseReference mDatabase;

    public static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            android.Manifest.permission.CAMERA
    };

    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_CAMERA_ROLL = 3645;
    public static final int REQUEST_PERMISSION = 300;
    public static final String DEV_INFORMATAION = "Michael Liu";

    public static String SESSION_UUID;
    public static FirebaseAuth fAuth;
    public static String RELEASE_TYPE;
    public static String VERSION;

    public static void init_helper(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        RELEASE_TYPE = "development";
        VERSION = "0.0.1";
        fAuth = FirebaseAuth.getInstance();
    }


    public static int check_register(String username, String password_main, String password_confirm, String email){

        if(username.equals("") || password_confirm.equals("") || password_main.equals("") || email.equals("")) return 3;
        if(!(password_main.equals(password_confirm))) return 1;

        return 0;
    }

    public static void sign_out(){

        Handler.SESSION_UUID = null;
        Handler.fAuth.getInstance().signOut();
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}

