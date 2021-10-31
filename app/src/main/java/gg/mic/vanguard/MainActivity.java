package gg.mic.vanguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;

public class MainActivity extends AppCompatActivity {


    private void requestPermissions() {
        int PERMISSION_ALL = 1;
        if (!Handler.hasPermissions(this, Handler.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, Handler.PERMISSIONS, PERMISSION_ALL);
        }
    }


    public Button login_button, register_button, confirm_register;
    public EditText username, password, register_username, register_password, confirm_password, email_register;
    private View registerView;
    private PopupWindow popUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        init_elements();

        requestPermissions();
        if(Handler.fAuth.getCurrentUser() != null){
            login_event(Handler.fAuth.getCurrentUser().getEmail());
        }

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                    Snackbar failure_loginnull = Snackbar.make(findViewById(android.R.id.content).getRootView(), "Please fill out all fields", 1000);
                    failure_loginnull.show();
                }
                else if(!username.getText().equals("") && !password.getEditableText().equals("")){
                    Handler.fAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                login_event(username.getText().toString());
                            }else {
                                Snackbar failure_loginnull = Snackbar.make(findViewById(android.R.id.content).getRootView(), "Login Failure", 1000);
                                failure_loginnull.show();
                            }
                        }
                    });
                }
                else{
                    Snackbar failure_loginnull = Snackbar.make(findViewById(android.R.id.content).getRootView(), "An unexpected internal error occurred. Please contact the Flaurapedia devs.", 1000);
                    failure_loginnull.show();
                }

            }
         });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_register(findViewById(android.R.id.content).getRootView());
                System.out.println("Register Button Pressed");
            }
        });

    }

    private void login_event(String user) {
        System.out.println("Login with username: " + user);
        Handler.SESSION_UUID = user;
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }


    public void show_register(View view) {

        if(Handler.fAuth.getCurrentUser() != null){
            login_event(Handler.fAuth.getCurrentUser().getEmail());
        }

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);

        try {
            registerView = inflater.inflate(R.layout.view_register, null);
        } catch (InflateException e) {
        }

        int width = view.getWidth();
        int height = view.getHeight();
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(registerView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        init_register(registerView);


        confirm_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int status = Handler.check_register(register_username.getText().toString(), register_password.getText().toString(), confirm_password.getText().toString(), email_register.getText().toString());
                switch (status) {
                    case 1:
                        Snackbar failure_login1 = Snackbar.make(findViewById(android.R.id.content).getRootView(), "The passwords do not match", 1000);
                        failure_login1.show();
                        break;
                    case 3:
                        Snackbar failure_login3 = Snackbar.make(findViewById(android.R.id.content).getRootView(), "Please fill out all fields", 1000);
                        failure_login3.show();
                        break;
                    case 0:
                        Handler.fAuth.createUserWithEmailAndPassword(email_register.getText().toString(), register_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    Snackbar success_snack = Snackbar.make(findViewById(android.R.id.content).getRootView(), "Successful Registration! Thank you", 1000);
                                    success_snack.show();
                                    popupWindow.dismiss();
                                }else {
                                    Snackbar failure_loginnull = Snackbar.make(findViewById(android.R.id.content).getRootView(), "This email is either in use or invalid", 1000);
                                    failure_loginnull.show();
                                }
                            }
                        });
                        break;
                }
            }
        });
    }

    protected void init_elements() {

        Handler.init_helper();
        getSupportActionBar().hide();
        login_button = (Button) findViewById(R.id.login_login);
        register_button = (Button) findViewById(R.id.login_register);

        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
    }

    protected void init_register(View view){

        email_register = (EditText) view.findViewById(R.id.register_email);
        confirm_register = (Button) view.findViewById(R.id.register_confirm);
        register_username = (EditText) view.findViewById(R.id.register_username);
        register_password = (EditText) view.findViewById(R.id.register_password);
        confirm_password = (EditText) view.findViewById(R.id.confirm_password);

    }

}