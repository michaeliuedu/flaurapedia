package gg.mic.vanguard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Browse extends AppCompatActivity {

    public ImageButton back, search_button;
    public EditText search;
    private String search_query;
    public DatabaseReference mDatabase;
    public ArrayList<String> data_title = new ArrayList<String>();
    public ArrayList<String> data_description = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        init_elements();
        initialize_rendering();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDashboard();
            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDatabase();
            }
        });
    }

    protected void initialize_rendering(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        getdata();

    }

    private void assert_data(Post post){
        data_title.add(post.title);
        data_description.add(post.description);
    }

    private void render_data(ArrayList<String> datatitle, ArrayList<String> datadescription){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.post_view);
        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);

        for( int i = 0; i < datatitle.size(); i++ )
        {
            TextView title = new TextView(this);
            title.setTypeface(boldTypeface);
            title.setText(datatitle.get(i));
            title.setTextSize(15f);
            linearLayout.addView(title);

            TextView description = new TextView(this);
            description.setText(datadescription.get(i));
            description.setTextSize(12f);
            linearLayout.addView(description);

            TextView spacing = new TextView(this);
            spacing.setText("");
            spacing.setTextSize(15f);
            linearLayout.addView(spacing);

        }
    }
    private void getdata() {
        mDatabase.child("flauratic-community")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post postinst = snapshot.getValue(Post.class);
                            assert_data(postinst);
                        }
                        render_data(data_title, data_description);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println(databaseError.toString());
                    }
                });

    }

    //Depreciated this method
    protected void searchDatabase() {
        search_query = search.getText().toString();
        if(search_query.equals("")){
            Snackbar fail = Snackbar.make(findViewById(android.R.id.content).getRootView(), "Please enter a value to search", 2000);
            fail.show();


        }else if(!(search_query.equals(""))){
            Query firebaseSearchQuery = mDatabase.child("Scientists").orderByChild("name").startAt(search_query)
                    .endAt(search_query + "uf8ff");
            firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                           System.out.println("Search logic");
                        }
                    }else {
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    protected void showDashboard(){
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    protected void init_elements(){

        back = (ImageButton)findViewById(R.id.back);
        search = (EditText)findViewById(R.id.searchbar);
        search_button = (ImageButton) findViewById(R.id.search_button);
        getSupportActionBar().hide();
        mDatabase = FirebaseDatabase.getInstance().getReference();


    }

}