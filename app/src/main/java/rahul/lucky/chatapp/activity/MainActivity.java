package rahul.lucky.chatapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.List;

import rahul.lucky.chatapp.R;
import rahul.lucky.chatapp.models.User;

public class MainActivity extends AppCompatActivity {

    public final String TAG = getClass().getName();
    private static final int RC_SIGN_IN = 2222;

    private CollectionReference collectionReference;

    private String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        collectionReference = FirebaseFirestore.getInstance().collection("users");
        getFCMToken();
    }


    private void getFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                fcmToken = instanceIdResult.getToken();
                launchFirebaseSignInIntent();
                Log.e("fcmToken", fcmToken);
            }
        });
    }

    public void launchFirebaseSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
//                        .setLogo(R.drawable.my_great_logo)      // Set logo drawable
//                        .setTheme(R.style.MySuperAppTheme)      // Set theme
                .setTosAndPrivacyPolicyUrls(
                        "https://www.google.com/",
                        "https://images.google.com/")
                .build();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Toast.makeText(this, "Success SignedIn", Toast.LENGTH_SHORT).show();
                checkForUserIsExistOrNot();

            } else {
                // Sign in failed
                if (response == null) {
                    Toast.makeText(this, "UnKnown error", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                FirebaseUiException error = response.getError();

                if (error !=null && error.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No Network Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, "UnKnown error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Sign-in error: ", error);
                finish();
            }
        }
    }

    private void checkForUserIsExistOrNot() {
        FirebaseAuth instance = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = instance.getCurrentUser();

        OnCompleteListener<DocumentSnapshot> onCompleteListener = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.e("NewUser", "Exits");
                    User user = document.toObject(User.class);
                    //setUser(user);
                    gotoNextActivity(user);
                } else {
                    Log.e("NewUser", "Not Exits");
                    saveUserToFirestore(currentUser);
                }
            }
        };

        collectionReference.document(currentUser.getUid()).get().addOnCompleteListener(onCompleteListener);
    }

    private void saveUserToFirestore(FirebaseUser currentUser) {
        if(currentUser!=null) {
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            if (email == null || email.isEmpty()) {
                email = currentUser.getPhoneNumber();
            }
            String uid = currentUser.getUid();
            DocumentReference document = collectionReference.document(uid);
            Uri photoUrl = currentUser.getPhotoUrl();
            final User user = new User(document, displayName, photoUrl, email, fcmToken, System.currentTimeMillis() + "");
            document.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    gotoNextActivity(user);
                }
            });
        }
    }

    private void gotoNextActivity(User user) {
        Intent intent = new Intent(MainActivity.this,ChatRoomActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("userId",user.getUserId().getPath());
        startActivity(intent);
        finish();
    }

}
