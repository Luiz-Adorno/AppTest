package miips.com.Adapters.MessagesAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import miips.com.Models.MessagesModels.MessageModel;
import miips.com.R;

public class RecyclerViewMessagesAdapter extends RecyclerView.Adapter<RecyclerViewMessagesAdapter.ViewHolder> {
    private static final String TAG = "RcMessage";
    Context mContext;
    ArrayList<MessageModel> arrayList;

    public RecyclerViewMessagesAdapter(Context mContext, ArrayList<MessageModel> arrayList) {
        this.arrayList = arrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        MessageModel message = arrayList.get(position);
        Log.d(TAG, "onBindViewHolder: title ta assim: "+ message.getTitle());

        holder.title.setText(message.getTitle());
        holder.subtitle.setText(message.getSubtitle());
        holder.date.setText(message.getDate());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, subtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.sub_title);
            date = itemView.findViewById(R.id.date_number);
        }
    }
}
