package com.example.roulette;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int RC_SIGN_IN = 9001;
    private FusedLocationProviderClient fusedLocationClient;
    private String apiKey = BuildConfig.API_KEY;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private Button signInButton, logoutButton, rouletteButton;
    private SwitchCompat themeSwitch;
    private ConstraintLayout mainLayout;
    private boolean userX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize UI components
        signInButton = findViewById(R.id.sign_in_button);
        logoutButton = findViewById(R.id.logout_button);
        themeSwitch = findViewById(R.id.theme_switch);
        mainLayout = findViewById(R.id.main_layout); // Initialize mainLayout
        rouletteButton = findViewById(R.id.roulette_button);

        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        // Set initial theme and background color based on saved preference
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            themeSwitch.setChecked(true);
            mainLayout.setBackgroundColor(getResources().getColor(R.color.darker_gray)); // Dark theme background
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            themeSwitch.setChecked(false);
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white)); // Light theme background
        }

        // Set up listener for theme switching
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                mainLayout.setBackgroundColor(getResources().getColor(R.color.darker_gray)); // Switch to dark background
                editor.putBoolean("isDarkModeOn", true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                mainLayout.setBackgroundColor(getResources().getColor(R.color.white)); // Switch to light background
                editor.putBoolean("isDarkModeOn", false);
            }
            editor.apply();
        });

        Button mapButton = findViewById(R.id.directions_button);
        mapButton.setOnClickListener(v -> {
            if (!userX) {
                Toast.makeText(MainActivity.this, "Please sign in to use this feature", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        // Google sign-in setup
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(v -> signIn());
        logoutButton.setOnClickListener(v -> signOut());

        rouletteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Place.list.isEmpty()) {
                    getUserLocationAndFindPlaces(2000);
                }
                if (!Place.list.isEmpty()) {
                    Place.populateRandomList();
                    Intent intent = new Intent(MainActivity.this, RouletteActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No Places Found Nearby", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateView(mAuth.getCurrentUser());
    }

    private void getUserLocationAndFindPlaces(int radius) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Place.findNearbyPlaces(MainActivity.this, location.getLatitude(), location.getLongitude(), radius, apiKey);
                    } else {
                        Toast.makeText(MainActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocationAndFindPlaces(2000);
            } else {
                Toast.makeText(this, "Location permission is required to find nearby places", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        userX = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("Google Sign-In", "Google sign in failed", e);
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Sign in successful! Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        updateView(user);
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            updateView(null);
            userX = false;
        });
    }

    private void updateView(FirebaseUser user) {
        if (user != null) {
            signInButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            signInButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }
    }
}
