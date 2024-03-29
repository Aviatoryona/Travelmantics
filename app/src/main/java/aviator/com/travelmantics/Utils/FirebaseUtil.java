package aviator.com.travelmantics.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aviator.com.travelmantics.Model.TravelDeal;
import aviator.com.travelmantics.TravelDealList;

public class FirebaseUtil {

    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static StorageReference storageReference;
    public static FirebaseStorage firebaseStorage;
    private static FirebaseUtil firebaseUtil;
    public static ArrayList<TravelDeal> travelDealArrayList;
    public static FirebaseAuth firebaseAuth;
    private static FirebaseAuth.AuthStateListener authStateListener;
    private static TravelDealList callerActivity;
    private static final int RC_SIGN_IN = 123;
    public static boolean isAdmin;

    private FirebaseUtil() {
    }

    public static void openFbReference(String reference, final TravelDealList activity) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            firebaseDatabase = FirebaseDatabase.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();
            firebaseAuth = FirebaseAuth.getInstance();
            callerActivity = activity;
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    } else {
                        String userID = firebaseAuth.getUid();
                        checkIfAdmin(userID);
                    }
                }
            };
            connectStorage();
        }
        travelDealArrayList = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference().child(reference);
    }

    private static void connectStorage() {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("TravelDeal_Images");
    }

    private static void checkIfAdmin(String userID) {
//        FirebaseUtil.isAdmin = false;
        FirebaseUtil.isAdmin = true;
        DatabaseReference reference = firebaseDatabase.getReference().child("administrators")
                .child(userID);
        ChildEventListener eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                callerActivity.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addChildEventListener(eventListener);
    }

    public static void signIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        callerActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachListener() {
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static void detachListener() {
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

}
