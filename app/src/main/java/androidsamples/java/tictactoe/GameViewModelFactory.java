package androidsamples.java.tictactoe;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GameViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final GameType gameType;
    private final String gameID;
    private final String emailID;

    public GameViewModelFactory(GameType gameType, String gameID, String emailID) {
        this.gameType = gameType;
        this.gameID = gameID;
        this.emailID = emailID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GameViewModel(gameType, gameID, emailID);
    }
}
