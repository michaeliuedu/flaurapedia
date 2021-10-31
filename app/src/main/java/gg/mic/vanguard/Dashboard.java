package gg.mic.vanguard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;


public class Dashboard extends AppCompatActivity {

    public TextView username, greeting, release;
    public ImageButton signout, expand, profilepic, create, browse, classify;

    private void requestPermissions() {
        int PERMISSION_ALL = 1;
        if (!Handler.hasPermissions(this, Handler.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, Handler.PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        init_elements();
        requestPermissions();

        username.setText(Handler.SESSION_UUID);
        greeting.setText("Welcome back to your dashboard. We hope you enjoy your stay!");
        release.setText("unique: " + Handler.fAuth.getCurrentUser().getUid().toString() + " " + Handler.RELEASE_TYPE + ": " + Handler.VERSION);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sign_out();
                Handler.sign_out();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_create();
            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { view_browse(); }
        });

        classify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_classify();
            }
        });
    }

    protected void view_create(){
        Intent intent = new Intent(this, Create.class);
        startActivity(intent);
    }

    protected void view_classify() {
        Intent intent = new Intent(this, UserMenu.class);
        startActivity(intent);
    }

    protected void view_browse(){
        Intent intent = new Intent(this, Browse.class);
        startActivity(intent);
    }

    protected void init_elements(){

        username = (TextView) findViewById(R.id.UUID);
        greeting = (TextView) findViewById(R.id.dashboard);
        release = (TextView) findViewById(R.id.release_information);

        classify = (ImageButton) findViewById(R.id.classify_plant);
        signout = (ImageButton) findViewById(R.id.signout);
        expand = (ImageButton) findViewById(R.id.see_extras);
        profilepic = (ImageButton) findViewById(R.id.home);
        create = (ImageButton) findViewById(R.id.make_post);
        browse = (ImageButton) findViewById(R.id.browse_posts);
    }

    protected void sign_out(){
        System.out.println("Logout with username: " + username.getText());
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

