package miips.com.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import miips.com.Models.RecyclerViewModels.HorizontalModel;
import miips.com.Models.RecyclerViewModels.VerticalModel;
import miips.com.R;

public class VerticalRecyclerViewAdapter extends RecyclerView.Adapter<VerticalRecyclerViewAdapter.VerticalRVViewHolder> {
    private static final String TAG = "Vertical";
    Context context;
    ArrayList<VerticalModel> arrayList;

    public VerticalRecyclerViewAdapter(Context context, ArrayList<VerticalModel> arrayList){
        this.arrayList = arrayList;
        this.context = context;
    }

    @SuppressLint("LongLogTag")
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
        Drawable color = verticalModel.getColorTitle();
        ArrayList<HorizontalModel> singleItem = verticalModel.getArrayList();

        verticalRVViewHolder.textViewTitle.setText(title);
        verticalRVViewHolder.titleLayout.setBackground(color);

        HorizontalProductRecyclerViewAdapter horizontalRecyclerViewAdapter = new HorizontalProductRecyclerViewAdapter(context, singleItem);

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