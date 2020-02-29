package com.example.cartalyst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;

public class Register extends AppCompatActivity {

    EditText name,email,password;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.register_name);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        register = findViewById(R.id.register_button);

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String e,p;
                e = email.getEditableText().toString();
                p = password.getEditableText().toString();
                Toast.makeText(Register.this, e+" "+p, Toast.LENGTH_SHORT).show();
                firebaseAuth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Snackbar.make(v,"User Created",Snackbar.LENGTH_LONG).show();
                            try {
                                //wait(Snackbar.LENGTH_LONG);
                                startActivity(new Intent(getApplicationContext(), LogIn.class));

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        else {
                            Snackbar.make(v, "Error, unable to register", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
