package com.example.coinfolio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.google.android.gms.auth.api.signin.GoogleSignIn.*;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    Button signOutBtn, signUpbtn;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    EditText email_input, password_input;
    TextView forgot_password;
    private static final String TAaG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_login);
        signInButton = findViewById(R.id.siginBtn);
        signUpbtn = findViewById(R.id.create_account_btn);
        email_input = findViewById(R.id.email_signin_input);
        password_input = findViewById(R.id.password_signin_input);
        forgot_password = findViewById(R.id.forgotPasswordTV);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Request();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        signOutBtn = findViewById(R.id.signin);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email_input.getText().toString() == null || password_input.getText().toString() == null)
                {
                    Toast.makeText(getApplicationContext(), "Enter the Email and Password", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    EmailPasswordSignin(email_input.getText().toString(), password_input.getText().toString(), mAuth);
                }
            }
        });

        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SignUp.class);
                startActivity(intent);
            }
        });


        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetEmail = new EditText(getApplicationContext());
                final AlertDialog.Builder resetDialog = new AlertDialog.Builder(Login.this, R.style.Theme_MaterialComponents_Light_DarkActionBar_Bridge);

                resetDialog.setTitle("Reset Password");
                resetDialog.setMessage("Enter Email.\nWe Will Send A Password Reset Link To This Email.");
                resetDialog.setView(resetEmail);

                resetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(TextUtils.isEmpty(resetEmail.getText()))
                        {
                            Toast.makeText(getApplicationContext(), "No Email Address Entered", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            String mail = resetEmail.getText().toString();
                            mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Sent Password Reset Link To Email.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Couldn't Send Link To Email", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });

                resetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                resetDialog.show();


            }
        });


    }

    public void EmailPasswordSignin(String email, String password, FirebaseAuth a)
    {
        if(email.equals("") || password.equals(""))
            Toast.makeText(getApplicationContext(), "Enter the Email and Password", Toast.LENGTH_SHORT).show();
        else
        {
            a.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(getApplication(), MainActivity.class);
                                intent.putExtra("uuid", mAuth.getCurrentUser().getUid());
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    public void Request(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = getClient(this, gso);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(getApplicationContext(),"Failed to login " +e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null)
                            {
                                Intent intent = new Intent(getApplication(), MainActivity.class);
                                intent.putExtra("uuid", mAuth.getCurrentUser().getUid());
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"not signed in", Toast.LENGTH_SHORT).show();
                            }
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            intent.putExtra("uuid", mAuth.getCurrentUser().getUid());
            startActivity(intent);
            finish();
        }
        //updateUI(currentUser);
    }

}
