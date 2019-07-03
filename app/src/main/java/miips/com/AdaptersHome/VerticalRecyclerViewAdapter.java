package miips.com.AdaptersHome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import miips.com.Models.HorizontalModel;
import miips.com.Models.VerticalModel;
import miips.com.R;

public class VerticalRecyclerViewAdapter extends RecyclerView.Adapter<VerticalRecyclerViewAdapter.VerticalRVViewHolder> {
    Context context;
    ArrayList<VerticalModel> arrayList;

    public VerticalRecyclerViewAdapter(Context context, ArrayList<VerticalModel> arrayList){
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public VerticalRVViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_vertical, viewGroup, false);
        return new VerticalRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalRVViewHolder verticalRVViewHolder, int i) {
        VerticalModel verticalModel = arrayList.get(i);
        String title = verticalModel.getTitle();
        ArrayList<HorizontalModel> singleItem = verticalModel.getArrayList();

        verticalRVViewHolder.textViewTitle.setText(title);

        ItemHorizontalRecyclerViewAdapter horizontalRecyclerViewAdapter = new ItemHorizontalRecyclerViewAdapter(context, singleItem);

        verticalRVViewHolder.recyclerView.setHasFixedSize(true);
        verticalRVViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        verticalRVViewHolder.recyclerView.setAdapter(horizontalRecyclerViewAdapter);


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class VerticalRVViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView textViewTitle;
        public VerticalRVViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recycler_view1);
            textViewTitle = itemView.findViewById(R.id.textTitle1);
        }
    }

}