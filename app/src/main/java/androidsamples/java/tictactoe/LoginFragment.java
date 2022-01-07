package androidsamples.java.tictactoe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginFragment extends Fragment {

    private static final String TAG = "loginFragment";
    private UserViewModel mAuthViewModel;

    private EditText mEditEmail, mEditPassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Handle the back press by adding a confirmation dialog
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back pressed");
                // Popping twice to exit the app.
                Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).popBackStack();
                Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mEditEmail = view.findViewById(R.id.edit_email);
        mEditPassword = view.findViewById(R.id.edit_password);

        mAuthViewModel.getCurrentUserID().observe(getViewLifecycleOwner(), userID -> {
            Toast.makeText(requireActivity(), mAuthViewModel.getMessage(), Toast.LENGTH_SHORT).show();
            if(!"".equals(userID)){
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
            }
        });

        view.findViewById(R.id.btn_log_in)
                .setOnClickListener(v -> onClickRegisterOrSignInUser(view));

        return view;
    }

    public void onClickRegisterOrSignInUser(View view){
        String email = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();

        // Check if not empty
        if (!"".equals(email) && !"".equals(password)){
            mAuthViewModel.signInOrRegisterUser(email, password);
        }
    }

    // No options menu in login fragment.
}