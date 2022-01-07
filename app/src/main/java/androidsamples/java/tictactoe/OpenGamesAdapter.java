package androidsamples.java.tictactoe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OpenGamesAdapter extends RecyclerView.Adapter<OpenGamesAdapter.ViewHolder> {

  private List<OpenGameInfo> mOpenGamesList;

  public OpenGamesAdapter() {
    mOpenGamesList = new ArrayList<>();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    OpenGameInfo currentGame = mOpenGamesList.get(position);
    holder.mIdView.setText(currentGame.getPlayerID());
    holder.mContentView.setText(currentGame.getGameID());
  }

  @Override
  public int getItemCount() {
    return (mOpenGamesList == null) ? 0 : mOpenGamesList.size();
  }

  public void setGames(List<OpenGameInfo> openGamesList) {
    this.mOpenGamesList = openGamesList;
    notifyDataSetChanged();
  }

  public void insertGame(OpenGameInfo game) {
    this.mOpenGamesList.add(game);
    notifyItemInserted(mOpenGamesList.size()-1);
  }

  public void removeGame(OpenGameInfo game){
    int index = this.mOpenGamesList.indexOf(game);
    this.mOpenGamesList.remove(index);
    notifyItemRemoved(index);
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mIdView = view.findViewById(R.id.item_number);
      mContentView = view.findViewById(R.id.content);
      mView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
      DashboardFragmentDirections.ActionGame action = DashboardFragmentDirections.actionGame(mOpenGamesList.get(getBindingAdapterPosition()).getGameID().toString());
      Navigation.findNavController(view).navigate(action);
    }

    @NonNull
    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }
  }
}