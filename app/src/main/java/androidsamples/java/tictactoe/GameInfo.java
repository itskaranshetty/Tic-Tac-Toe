package androidsamples.java.tictactoe;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@IgnoreExtraProperties
public class GameInfo {
    @Exclude
    private String gameID = UUID.randomUUID().toString();
    private List<List<String>> board = new ArrayList<>();
    private String currentPlayerLetter;
    private GameStatus status;

    static final int ROWS = 3;
    static final int COLUMNS = 3;
    static final int GRID_SIZE = 9;

    public GameInfo(){}

    public GameInfo(String currentPlayerLetter, GameStatus status){
        this.currentPlayerLetter = currentPlayerLetter;
        this.status = status;
        for(int i = 0; i < ROWS; i++){
            ArrayList <String> temp = new ArrayList<>();
            for(int j = 0; j < COLUMNS;j++){
                temp.add("");
            }
            this.board.add(temp);
        }
    }

    public int getNumberOfEmptyCells(){
        int numberOfEmptyCells = 0;
        for(int i=0;i<ROWS;i++){
            for(int j=0;j<COLUMNS;j++){
                if(board.get(i).get(j).equals(""))
                    numberOfEmptyCells++;
            }
        }
        return numberOfEmptyCells;
    }

    public boolean makeMove(int i, int j, String letter){
        if(board.get(i).get(j).equals("")) {
            board.get(i).set(j, letter);
            return true;
        }
        return false;
    }

    public void makeRandomMove(String letter){
        Random random = new Random();
        int randomNumber = random.nextInt(getNumberOfEmptyCells());
        for(int i=0;i<ROWS;i++){
            for(int j=0;j<COLUMNS;j++){
                if(board.get(i).get(j).equals("")) {
                    if(randomNumber == 0){
                        board.get(i).set(j,letter);
                        return;
                    }
                    randomNumber--;
                }
            }
        }
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public List<List<String>> getBoard() {
        return board;
    }

    public void setBoard(List<List<String>> board) {
        this.board = board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getCurrentPlayerLetter() {
        return currentPlayerLetter;
    }

    public void setCurrentPlayerLetter(String currentPlayerLetter) {
        this.currentPlayerLetter = currentPlayerLetter;
    }


    @NonNull
    @Override
    public String toString() {
        return "Game ID: " + gameID + "Status: " + status.toString();
    }
}
