package guessmypic.gmp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreHolder>{
    private Context context;
    private List<User> userList;

    public ScoreAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public ScoreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.score_view,null);
        return new ScoreHolder(view);
    }

    @Override
    public void onBindViewHolder(ScoreHolder holder, int position) {
        User user = userList.get(position);
        holder.username.setText(user.getName());
        holder.score.setText(String.valueOf(user.getCoin()));
        holder.number.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ScoreHolder extends RecyclerView.ViewHolder
    {
        public TextView username;
        public TextView score;
        public TextView number;
        public ScoreHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.usernameText);
            score = (TextView) itemView.findViewById(R.id.scoreText);
            number = (TextView) itemView.findViewById(R.id.numberText);
        }
    }

}
