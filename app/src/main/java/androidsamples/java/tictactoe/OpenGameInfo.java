package androidsamples.java.tictactoe;

import com.google.firebase.database.Exclude;

public class OpenGameInfo {
    private String gameID;
    private String playerID;

    public OpenGameInfo(){
    }

    public OpenGameInfo(String gameID) {
        this.gameID = gameID;
    }

    public OpenGameInfo(String gameID, String playerID) {
        this.gameID = gameID;
        this.playerID = playerID;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }
}
