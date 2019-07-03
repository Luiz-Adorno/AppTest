package miips.com.AdaptersHome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import miips.com.Models.HorizontalModel;
import miips.com.R;

public class ItemHorizontalRecyclerViewAdapter extends RecyclerView.Adapter<ItemHorizontalRecyclerViewAdapter.HorizontalRVViewHolder> {
    Context context;
    ArrayList<HorizontalModel> arrayList;

    public ItemHorizontalRecyclerViewAdapter(Context context, ArrayList<HorizontalModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public HorizontalRVViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_horizontal,viewGroup, false);
        return new HorizontalRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalRVViewHolder horizontalRVViewHolder, int i) {
        final HorizontalModel horizontalModel = arrayList.get(i);
        horizontalRVViewHolder.textViewTitle.setText(horizontalModel.getName());
        horizontalRVViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, horizontalModel.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class HorizontalRVViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        ImageView imageViewThumb;

        public HorizontalRVViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.txtTitleHorizontal);
            imageViewThumb = itemView.findViewById(R.id.ivThumb);
        }
    }
}