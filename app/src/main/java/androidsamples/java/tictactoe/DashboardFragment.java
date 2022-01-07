package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

  private static final String TAG = "DashboardFragment";
  private NavController mNavController;
  private UserViewModel mUserViewModel;
  private GameListViewModel mGameListViewModel;
  private TextView mTextScore;
  private OpenGamesAdapter mAdapter;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public DashboardFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    mGameListViewModel = new ViewModelProvider(this).get(GameListViewModel.class);
    setHasOptionsMenu(true); // Needed to display the action menu for this fragment
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_dashboard, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    mTextScore = view.findViewById(R.id.txt_score);

    if(!mUserViewModel.checkIfUserLoggedIn()){
      NavDirections action = DashboardFragmentDirections.actionNeedAuth();
      mNavController.navigate(action);
    }
    else {
      RecyclerView entriesListRecyclerView = view.findViewById(R.id.list);
      entriesListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      mAdapter = new OpenGamesAdapter();
      entriesListRecyclerView.setAdapter(mAdapter);
    }

    // Show a dialog when the user clicks the "new game" button
    view.findViewById(R.id.fab_new_game).setOnClickListener(v -> {

      // A listener for the positive and negative buttons of the dialog
      DialogInterface.OnClickListener listener = (dialog, which) -> {
        String gameType = "No type";
        if (which == DialogInterface.BUTTON_POSITIVE) {
          gameType = getString(R.string.two_player);
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
          gameType = getString(R.string.one_player);
        }
        Log.d(TAG, "New Game: " + gameType);

        // Passing the game type as a parameter to the action
        // extract it in GameFragment in a type safe way
        NavDirections action = DashboardFragmentDirections.actionGame(gameType);
        mNavController.navigate(action);
      };

      // create the dialog
      AlertDialog dialog = new AlertDialog.Builder(requireActivity())
          .setTitle(R.string.new_game)
          .setMessage(R.string.new_game_dialog_message)
          .setPositiveButton(R.string.two_player, listener)
          .setNegativeButton(R.string.one_player, listener)
          .setNeutralButton(R.string.cancel, (d, which) -> d.dismiss())
          .create();
      dialog.show();
    });

    ChildEventListener persistentGameInfoListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

        OpenGameInfo newGame = dataSnapshot.getValue(OpenGameInfo.class);
        mAdapter.insertGame(newGame);

      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

        OpenGameInfo newGame = dataSnapshot.getValue(OpenGameInfo.class);
        mAdapter.removeGame(newGame);
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "Sync Error", databaseError.toException());
      }
    };

    mGameListViewModel.setPersistentGameInfoListener(persistentGameInfoListener);

  }

  @Override
  public void onResume() {
    super.onResume();

    mUserViewModel.getCurrentUserInfo().observe(getViewLifecycleOwner(), userInfo -> {
      if(userInfo == null)
        Toast.makeText(requireActivity(), mUserViewModel.getMessage(), Toast.LENGTH_SHORT).show();
      else
        mTextScore.setText(String.format(getResources().getString(R.string.userScore),userInfo.getWinCount(),userInfo.getLossCount(),userInfo.getTieCount()));
    });
    mUserViewModel.getUserInfo();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mGameListViewModel.removePersistentGameInfoListener();
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_logout, menu);
    // this action menu is handled in MainActivity
  }


}