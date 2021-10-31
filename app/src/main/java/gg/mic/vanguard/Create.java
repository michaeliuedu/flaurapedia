package gg.mic.vanguard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Create extends AppCompatActivity {

    public EditText title_edit;
    public EditText description_edit;
    public ImageButton back;
    public Button post;
    public DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        init_elements();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDashboard();
            }
        });

        post.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(title_edit.getText().toString().equals("") || description_edit.getText().toString().equals("")){
                    Snackbar failure_loginnull = Snackbar.make(findViewById(android.R.id.content).getRootView(), "Please fill out all fields", 1000);
                    failure_loginnull.show();
                } else if(!title_edit.getText().equals("") && !description_edit.getEditableText().equals("")){
                    writeNewPost(String.valueOf(System.currentTimeMillis()), title_edit.getText().toString(), description_edit.getText().toString());
                    Snackbar success = Snackbar.make(findViewById(android.R.id.content).getRootView(), "Success! Your post has been added", 1000);
                    title_edit.setText(null);
                    description_edit.setText(null);
                    success.show();
                }
            }
        });
    }

    public void writeNewPost(String POST_IDENTIFICATION_TIME, String name, String email) {
        Post post = new Post(name, email);
        mDatabase.child("flauratic-community").child(POST_IDENTIFICATION_TIME).setValue(post);
    }

    protected void showDashboard(){
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    protected void init_elements() {
        getSupportActionBar().hide();
        back = (ImageButton)findViewById(R.id.back);
        title_edit = (EditText)findViewById(R.id.input_title);
        description_edit = (EditText)findViewById(R.id.input_desc);
        post = (Button)findViewById(R.id.post_button);
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }
}