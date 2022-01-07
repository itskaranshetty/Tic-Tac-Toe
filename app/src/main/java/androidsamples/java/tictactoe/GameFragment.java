package androidsamples.java.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.List;

public class GameFragment extends Fragment {
  private static final String TAG = "GameFragment";

  private final Button[] mButtons = new Button[GameInfo.GRID_SIZE];
  private NavController mNavController;

  private GameViewModel mGameViewModel;
  private UserViewModel mUserViewModel;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    setHasOptionsMenu(true); // Needed to display the action menu for this fragment

    // Extract the argument passed with the action in a type-safe way
    GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
    Log.d(TAG, "New game type = " + args.getGameType());
    GameViewModelFactory factory;
    if(args.getGameType().equals(getString(R.string.two_player)))
      factory = new GameViewModelFactory(GameType.TwoPlayer, null, mUserViewModel.getCurrentUserInfo().getValue().getEmailID());
    else if(args.getGameType().equals(getString(R.string.one_player)))
      factory = new GameViewModelFactory(GameType.SinglePlayer, null, mUserViewModel.getCurrentUserInfo().getValue().getEmailID());
    else
      factory = new GameViewModelFactory(GameType.TwoPlayer, args.getGameType(), mUserViewModel.getCurrentUserInfo().getValue().getEmailID());

    mGameViewModel = new ViewModelProvider(this, factory).get(GameViewModel.class);

    // Handle the back press by adding a confirmation dialog
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        Log.d(TAG, "Back pressed");

        // TODO show dialog only when the game is still in progress
        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
            .setTitle(R.string.confirm)
            .setMessage(R.string.forfeit_game_dialog_message)
            .setPositiveButton(R.string.yes, (d, which) -> {
              mUserViewModel.updateScore(GameResult.Loss);
              mGameViewModel.removeGame();
              mNavController.popBackStack();
            })
            .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
            .create();
        dialog.show();
      }
    };
    requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_game, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mNavController = Navigation.findNavController(view);

    mButtons[0] = view.findViewById(R.id.button0);
    mButtons[1] = view.findViewById(R.id.button1);
    mButtons[2] = view.findViewById(R.id.button2);

    mButtons[3] = view.findViewById(R.id.button3);
    mButtons[4] = view.findViewById(R.id.button4);
    mButtons[5] = view.findViewById(R.id.button5);

    mButtons[6] = view.findViewById(R.id.button6);
    mButtons[7] = view.findViewById(R.id.button7);
    mButtons[8] = view.findViewById(R.id.button8);

    disableAllButtons();

    for (int i = 0; i < mButtons.length; i++) {
      int finalI = i;
      mButtons[i].setOnClickListener(v -> {
        disableAllButtons();
        Log.d(TAG, "Button " + finalI + " clicked");
        if(!mGameViewModel.makeMove(finalI/3,finalI%3)){
          Activity activity = getActivity();
          if(activity != null)
            Toast.makeText(activity,"Invalid Move, please pick another cell.", Toast.LENGTH_SHORT).show();
        }
        enableAllButtons();
      });
    }

    mGameViewModel.getGameInfoLiveData().observe(getViewLifecycleOwner(),
            (Observer<GameInfo>) gameInfo -> {
              Log.i(TAG, gameInfo.getStatus()+String.valueOf(mGameViewModel.getPlayerLetter().equals(gameInfo.getCurrentPlayerLetter())));
              if (gameInfo.getStatus().equals(GameStatus.InProgress) && mGameViewModel.getPlayerLetter().equals(gameInfo.getCurrentPlayerLetter()))
                enableAllButtons();
              updateBoard(gameInfo.getBoard());

              if (gameInfo.getStatus().equals(GameStatus.Completed)) {
                mUserViewModel.updateScore(mGameViewModel.checkGameResult(gameInfo));
                Activity activity = getActivity();
                if(activity != null) {
                  if(mGameViewModel.checkGameResult(gameInfo).equals(GameResult.Win))
                    Toast.makeText(activity, "Congratulations!", Toast.LENGTH_SHORT).show();
                  else if(mGameViewModel.checkGameResult(gameInfo).equals(GameResult.Loss))
                    Toast.makeText(activity, "Sorry :(", Toast.LENGTH_SHORT).show();
                  else if(mGameViewModel.checkGameResult(gameInfo).equals(GameResult.Tie))
                    Toast.makeText(activity, "It's a tie!", Toast.LENGTH_SHORT).show();
                }
                // Other player lost
                if (mGameViewModel.getPlayerLetter().equals(gameInfo.getCurrentPlayerLetter())) {
                  mGameViewModel.removeGame();
                }
                mNavController.popBackStack();
              }
              else if(gameInfo.getStatus().equals(GameStatus.Forfeited) && mGameViewModel.getPlayerLetter().equals(gameInfo.getCurrentPlayerLetter()) ){
                mUserViewModel.updateScore(GameResult.Win);
                mGameViewModel.removeGame();
                mNavController.popBackStack();
              }

            });
  }

  public void updateBoard(List<List<String>> board){
    for(int i = 0; i < GameInfo.ROWS ; i++){
      for(int j = 0; j < GameInfo.COLUMNS; j++){
        mButtons[i*GameInfo.ROWS + j].setText(board.get(i).get(j));
      }
    }
  }

  public void disableAllButtons(){
    for(int j = 0; j < GameInfo.GRID_SIZE; j++){
      mButtons[j].setEnabled(false);
    }
  }

  public void enableAllButtons(){
    for(int j = 0; j < GameInfo.GRID_SIZE; j++){
      mButtons[j].setEnabled(true);
    }
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_logout, menu);
    // this action menu is handled in MainActivity
  }
}