package androidsamples.java.tictactoe;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameListViewModel extends ViewModel {


    private static final String TAG = "GameListViewModel";
    private final DatabaseReference mOpenGamesDatabase;
    private ChildEventListener mChildEventListener;

    public GameListViewModel(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://tic-tac-toe-ks-default-rtdb.asia-southeast1.firebasedatabase.app/");
        this.mOpenGamesDatabase = db.getReference("games_in_queue");
    }

    public void setPersistentGameInfoListener(ChildEventListener childEventListener){
        mChildEventListener = childEventListener;
        mOpenGamesDatabase.addChildEventListener(childEventListener);
    }

    public void removePersistentGameInfoListener() {
        mOpenGamesDatabase.removeEventListener(mChildEventListener);
    }
}
