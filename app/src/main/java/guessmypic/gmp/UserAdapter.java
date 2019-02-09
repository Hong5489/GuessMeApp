package guessmypic.gmp;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by HONGWEI on 2018/9/17.
 */

public class UserAdapter extends RecyclerView.Adapter<FindOpponentActivity.UserViewHolder> {

    private Context context;
    private List<UserOnline> userList;

    public UserAdapter(Context context, List<UserOnline> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public FindOpponentActivity.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_view,null);
        return new FindOpponentActivity.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FindOpponentActivity.UserViewHolder holder, int position) {
        UserOnline userOnline = userList.get(position);
        holder.nameText.setText(userOnline.getName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


}
