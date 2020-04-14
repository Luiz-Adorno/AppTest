package miips.com.Adapters.SearchAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import miips.com.Models.SearchModels.HorizontalSearchModel;
import miips.com.Models.SearchModels.VerticalSearchModel;
import miips.com.R;

public class VerticalRecyclerViewSearchAdapter extends RecyclerView.Adapter<VerticalRecyclerViewSearchAdapter.VerticalRVViewHolder> {
    private static final String TAG = "Vertical";
    Context context;
    ArrayList<VerticalSearchModel> arrayList;

    public VerticalRecyclerViewSearchAdapter(Context context, ArrayList<VerticalSearchModel> arrayList){
        this.arrayList = arrayList;
        this.context = context;
    }

    @SuppressLint("LongLogTag")
    @NonNull
    @Override
    public VerticalRVViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_vertical, viewGroup, false);
        return new VerticalRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalRVViewHolder verticalRVViewHolder, int i) {
        VerticalSearchModel verticalModel = arrayList.get(i);
        String title = verticalModel.getTitle();
        Drawable color = verticalModel.getColorTitle();
        ArrayList<HorizontalSearchModel> singleItem = verticalModel.getArrayList();

        verticalRVViewHolder.textViewTitle.setText(title);
        verticalRVViewHolder.titleLayout.setBackground(color);

        HorizontalSearchRecyclerViewAdapter horizontalRecyclerViewAdapter = new HorizontalSearchRecyclerViewAdapter(context, singleItem);

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
        RelativeLayout titleLayout;
        TextView textViewTitle;
        public VerticalRVViewHolder(@NonNull View itemView) {
            super(itemView);
             titleLayout = itemView.findViewById(R.id.title_template);
            recyclerView = itemView.findViewById(R.id.recycler_view1);
            textViewTitle = itemView.findViewById(R.id.textTitle1);
        }
    }

}