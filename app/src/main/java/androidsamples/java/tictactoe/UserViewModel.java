package androidsamples.java.tictactoe;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserViewModel extends ViewModel {

    private static final String TAG = "UserViewModel";
    private final FirebaseAuth mAuth;
    private final DatabaseReference mUserDatabase;
    private String mMessage = "";
    private final MutableLiveData<String> mCurrentUserIDLiveData = new MutableLiveData<>("");
    private final MutableLiveData<UserInfo> mCurrentUserInfoLiveData = new MutableLiveData<>(new UserInfo(null, 0,0,0));

    public UserViewModel(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://tic-tac-toe-ks-default-rtdb.asia-southeast1.firebasedatabase.app/");
        db.setPersistenceEnabled(true);
        mUserDatabase = db.getReference("users");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
            mCurrentUserIDLiveData.setValue(currentUser.getUid());
    }

    public boolean checkIfUserLoggedIn(){
        return !"".equals(mCurrentUserIDLiveData.getValue());
    }

    public void signInOrRegisterUser(String emailId, String passwd) {
        mAuth.signInWithEmailAndPassword(emailId, passwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() == null || task.getResult().getUser() == null) {
                            mMessage = "Login Failed, null object received";
                            mCurrentUserIDLiveData.setValue("");
                        }
                        mMessage = "Logged In Successfully.";
                        mCurrentUserIDLiveData.setValue(task.getResult().getUser().getUid());
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            registerNewUser(emailId, passwd);
                        } catch (Exception e) {
                            mMessage = "Login Failed, "+ e.getMessage();
                            mCurrentUserIDLiveData.setValue("");
                        }
                    }
                });
    }


    public void registerNewUser(String emailId, String passwd) {
        mAuth.createUserWithEmailAndPassword(emailId, passwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() == null || task.getResult().getUser() == null) {
                            mMessage = "Registration Failed, null object received";
                            mCurrentUserIDLiveData.setValue("");
                        }
                        Log.d(TAG, "registerNewUser:success");
                        mMessage = "New user Registered Successfully";
                        mUserDatabase.child(task.getResult().getUser().getUid()).setValue(new UserInfo(task.getResult().getUser().getEmail(),0, 0, 0));
                        mCurrentUserIDLiveData.setValue(task.getResult().getUser().getUid());
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            mMessage = "Registration failed: Weak Password";
                            mCurrentUserIDLiveData.setValue("");
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            mMessage = "Registration failed: Invalid Email";
                            mCurrentUserIDLiveData.setValue("");
                        } catch (Exception e) {
                            mMessage = "Registration failed.";
                            mCurrentUserIDLiveData.setValue("");
                        }
                    }
                });
    }

    public void initCurrentUserInfo(FirebaseUser user){
        mUserDatabase.child(user.getUid()).setValue(new UserInfo(user.getEmail(), 0, 0, 0));
    }

    public void getUserInfo(){
        OnCompleteListener <DataSnapshot> oneTimeUserInfoListener = (task -> {
            if (!task.isSuccessful() || task.getResult()==null) {
                Log.e(TAG, "Error getting data", task.getException());
                mMessage = "Couldn't get user data.";
                mCurrentUserInfoLiveData.setValue(null);
            }
            else {
                Log.d(TAG, String.valueOf(task.getResult().getValue(UserInfo.class)));
                UserInfo userInfo = task.getResult().getValue(UserInfo.class);
                if (userInfo == null){
                    mMessage = "Error in getting user data.";
                    mCurrentUserInfoLiveData.setValue(null);
                }
                else{
                    mMessage = "Retrieved user Data successfully.";
                    mCurrentUserInfoLiveData.setValue(userInfo);
                }
            }
        });
        if(getCurrentUserID().getValue() != null) {
            mUserDatabase.child(getCurrentUserID().getValue()).get().addOnCompleteListener(oneTimeUserInfoListener);
            Log.d(TAG,"One time User Info Listener Registered.");
        }
        else{
            mMessage = "Don't have current user ID.";
            mCurrentUserInfoLiveData.setValue(null);
        }
    }

    public void signOut() {
        mMessage = "";
        mCurrentUserIDLiveData.setValue("");
        mAuth.signOut();
    }

    public void updateScore(GameResult gameResult) {
        UserInfo currentUserInfo = mCurrentUserInfoLiveData.getValue();
        if (gameResult.equals(GameResult.Win))
            currentUserInfo.incrementWinCount();
        else if(gameResult.equals(GameResult.Loss))
            currentUserInfo.incrementLossCount();
        else if(gameResult.equals(GameResult.Tie))
            currentUserInfo.incrementTieCount();

        mCurrentUserInfoLiveData.setValue(currentUserInfo);
        mUserDatabase.child(mCurrentUserIDLiveData.getValue()).setValue(mCurrentUserInfoLiveData.getValue());

    }

    public MutableLiveData<UserInfo> getCurrentUserInfo() {
        return mCurrentUserInfoLiveData;
    }

    public String getMessage() {
        return mMessage;
    }

    public MutableLiveData<String> getCurrentUserID() {
        return mCurrentUserIDLiveData;
    }


}
