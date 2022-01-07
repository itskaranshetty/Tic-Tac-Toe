package androidsamples.java.tictactoe;

import androidx.annotation.NonNull;

public class UserInfo {
    private String emailID;
    private int winCount;
    private int lossCount;
    private int tieCount;

    public UserInfo(){}

    public UserInfo(String emailID, int winCount, int lossCount, int tieCount){
        this.emailID = emailID;
        this.winCount = winCount;
        this.lossCount = lossCount;
        this.tieCount = tieCount;
    }

    public int getWinCount() {
        return winCount;
    }

    public int getLossCount() {
        return lossCount;
    }

    public int getTieCount() {
        return tieCount;
    }

    public String getEmailID() {
        return emailID;
    }

    public void incrementWinCount() {
        this.winCount++;
    }
    public void incrementLossCount() {
        this.lossCount++;
    }
    public void incrementTieCount() {
        this.tieCount++;
    }

    @NonNull
    @Override
    public String toString() {
        return "Wins: " + winCount + " Losses: " + lossCount + " Tie: " + tieCount;
    }
}
