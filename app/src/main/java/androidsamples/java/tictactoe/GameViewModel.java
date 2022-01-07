package androidsamples.java.tictactoe;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameViewModel extends ViewModel {

    private static final String TAG = "Game View Model";
    private final DatabaseReference mOpenGamesDatabase, mGamesDatabase;

    private String mCurrentUserEmailID;
    private GameType mGameType;

    private MutableLiveData<GameInfo> mGameInfoLiveData = new MutableLiveData<>();
    private String mPlayerLetter;

    public GameViewModel(GameType gameType, String gameID, String currentUserEmailID){
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://tic-tac-toe-ks-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mOpenGamesDatabase = db.getReference("games_in_queue");
        mGamesDatabase = db.getReference("games");
        mGameType = gameType;
        mCurrentUserEmailID = currentUserEmailID;
        if (gameType.equals(GameType.TwoPlayer))
            addOrJoinGame(gameID);
        else
            addSinglePlayerGame();
    }

    private void addOrJoinGame(String gameID){
        if(gameID == null) {
            gameID = addTwoPlayerGame();
            addNewOpenGame(gameID);
        }
        else {
            mOpenGamesDatabase.child(gameID).removeValue();
            joinExistingGame(gameID);
        }

        mGamesDatabase.child(gameID).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GameInfo gameInfo = snapshot.getValue(GameInfo.class);
                        if(gameInfo != null) {
                            gameInfo.setGameID(snapshot.getKey());
                            mGameInfoLiveData.setValue(gameInfo);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "Game Cancelled",error.toException());
                    }
                });
    }

    private void addSinglePlayerGame() {
        mPlayerLetter = "X";
        GameInfo gameInfo = new GameInfo(mPlayerLetter, GameStatus.InProgress);
        mGameInfoLiveData.setValue(gameInfo);
    }

    public String addTwoPlayerGame(){
        mPlayerLetter = "X";
        GameInfo newGameInfo = new GameInfo(mPlayerLetter, GameStatus.Waiting);

        DatabaseReference ref = mGamesDatabase.push();
        ref.setValue(newGameInfo);
        newGameInfo.setGameID(ref.getKey());

        mGameInfoLiveData.setValue(newGameInfo);

        return ref.getKey();
    }

    public void addNewOpenGame(String gameID){
        OpenGameInfo openGame = new OpenGameInfo(gameID, mCurrentUserEmailID);
        mOpenGamesDatabase.child(gameID).setValue(openGame);
    }

    private void joinExistingGame(String gameID) {
        mPlayerLetter = "O";

        OnCompleteListener<DataSnapshot> oneTimeGameInfoListener = task -> {
            if (!task.isSuccessful() || task.getResult()==null) {
                Log.e(TAG, "Error getting data", task.getException());
            }
            else {
                Log.d(TAG, String.valueOf(task.getResult().getValue()));
                GameInfo joinedGame = task.getResult().getValue(GameInfo.class);
                if(joinedGame == null)
                    Log.e(TAG, "Error getting data", task.getException());
                else{
                    joinedGame.setGameID(gameID);
                    joinedGame.setStatus(GameStatus.InProgress);
                    mGamesDatabase.child(gameID).setValue(joinedGame);
                    mGameInfoLiveData.setValue(joinedGame);
                }
            }
        };

        mGamesDatabase.child(gameID).get().addOnCompleteListener(oneTimeGameInfoListener);
        Log.d(TAG,"One time Game Info Listener Registered.");
    }

    public String getFlippedPlayerLetter(){
        switch (mPlayerLetter){
            case "X":
                return "O";
            case "O":
                return "X";
            default:
                return "";
        }
    }

    public boolean makeMove(int i, int j) {
        GameInfo currentGame = mGameInfoLiveData.getValue();
        if(currentGame != null) {

            if(!currentGame.makeMove(i,j,mPlayerLetter))
                return false;

            currentGame.setCurrentPlayerLetter(getFlippedPlayerLetter());
            Log.d(TAG, currentGame.toString());

            if(hasGameFinished(currentGame))
                currentGame.setStatus(GameStatus.Completed);

            mGameInfoLiveData.setValue(currentGame);
            if(mGameType.equals(GameType.TwoPlayer))
                mGamesDatabase.child(currentGame.getGameID()).setValue(currentGame);
            else if(!hasGameFinished(currentGame))
                makeRandomMove();
            return true;
        }
        return false;
    }

    public void makeRandomMove(){
        GameInfo currentGame = mGameInfoLiveData.getValue();
        if(currentGame != null){
            currentGame.makeRandomMove(getFlippedPlayerLetter());
            currentGame.setCurrentPlayerLetter(mPlayerLetter);

            if(!checkGameResult(currentGame).equals(GameResult.Undecided))
                currentGame.setStatus(GameStatus.Completed);
            mGameInfoLiveData.setValue(currentGame);
        }
    }

    public GameResult checkGameResult(GameInfo gameInfo){
        // Check vertically
        for(int j=0;j<GameInfo.COLUMNS;j++){
            int numOfX = 0;
            int numOfO = 0;
            for(int i=0; i<GameInfo.ROWS; i++){
                if(gameInfo.getBoard().get(i).get(j).equals("X"))
                    numOfX++;
                else if (gameInfo.getBoard().get(i).get(j).equals("O"))
                    numOfO++;
            }
            if((mPlayerLetter.equals("X") && numOfX == GameInfo.COLUMNS) || (mPlayerLetter.equals("O") && numOfO == GameInfo.COLUMNS))
                return GameResult.Win;
            if((mPlayerLetter.equals("O") && numOfX == GameInfo.COLUMNS) || (mPlayerLetter.equals("X") && numOfO == GameInfo.COLUMNS))
                return GameResult.Loss;
        }

        // Check horizontally
        for(int i=0;i<GameInfo.ROWS;i++){
            int numOfX = 0;
            int numOfO = 0;
            for(int j=0; j<GameInfo.COLUMNS; j++){
                if(gameInfo.getBoard().get(i).get(j).equals("X"))
                    numOfX++;
                else if (gameInfo.getBoard().get(i).get(j).equals("O"))
                    numOfO++;
            }
            if((mPlayerLetter.equals("X") && numOfX == GameInfo.ROWS) || (mPlayerLetter.equals("O") && numOfO == GameInfo.ROWS))
                return GameResult.Win;
            if((mPlayerLetter.equals("O") && numOfX == GameInfo.ROWS) || (mPlayerLetter.equals("X") && numOfO == GameInfo.ROWS))
                return GameResult.Loss;
        }

        // Check Diagonally
        int numOfX = 0;
        int numOfO = 0;
        for(int i=0;i<GameInfo.ROWS;i++){
            if(gameInfo.getBoard().get(i).get(i).equals("X"))
                numOfX++;
            else if (gameInfo.getBoard().get(i).get(i).equals("O"))
                numOfO++;
        }
        if((mPlayerLetter.equals("X") && numOfX == GameInfo.ROWS) || (mPlayerLetter.equals("O") && numOfO == GameInfo.ROWS))
            return GameResult.Win;
        if((mPlayerLetter.equals("O") && numOfX == GameInfo.ROWS) || (mPlayerLetter.equals("X") && numOfO == GameInfo.ROWS))
            return GameResult.Loss;

        numOfX = 0;
        numOfO = 0;
        for(int i=0;i<GameInfo.ROWS;i++){
            if(gameInfo.getBoard().get(i).get(GameInfo.ROWS-i-1).equals("X"))
                numOfX++;
            else if (gameInfo.getBoard().get(i).get(GameInfo.ROWS-i-1).equals("O"))
                numOfO++;
        }
        if((mPlayerLetter.equals("X") && numOfX == GameInfo.ROWS) || (mPlayerLetter.equals("O") && numOfO == GameInfo.ROWS))
            return GameResult.Win;
        if((mPlayerLetter.equals("O") && numOfX == GameInfo.ROWS) || (mPlayerLetter.equals("X") && numOfO == GameInfo.ROWS))
            return GameResult.Loss;

        if(gameInfo.getNumberOfEmptyCells() == 0)
            return GameResult.Tie;
        return GameResult.Undecided;
    }

    private boolean hasGameFinished(GameInfo currentGame) {
        return !checkGameResult(currentGame).equals(GameResult.Undecided);
    }

    public void removeGame(){
        if(mGameType.equals(GameType.TwoPlayer))
            mGamesDatabase.child(mGameInfoLiveData.getValue().getGameID()).removeValue();
    }

    public void forfeitGame(){
        GameInfo currentGame = mGameInfoLiveData.getValue();

        if(currentGame != null){
            currentGame.setCurrentPlayerLetter(getFlippedPlayerLetter());
            if(mGameType.equals(GameType.TwoPlayer)){
                mOpenGamesDatabase.child(currentGame.getGameID()).removeValue();
                if(currentGame.getStatus().equals(GameStatus.InProgress)){
                    currentGame.setStatus(GameStatus.Forfeited);
                    mGamesDatabase.child(currentGame.getGameID()).setValue(currentGame);
                }
                else
                    mGamesDatabase.child(currentGame.getGameID()).removeValue();
            }
        }
    }

    public String getPlayerLetter() {
        return mPlayerLetter;
    }

    public MutableLiveData<GameInfo> getGameInfoLiveData() {
        return mGameInfoLiveData;
    }
}
