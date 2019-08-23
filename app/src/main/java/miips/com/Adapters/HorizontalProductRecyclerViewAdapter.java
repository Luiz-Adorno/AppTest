package miips.com.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import miips.com.Models.RecyclerViewModels.HorizontalModel;
import miips.com.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HorizontalProductRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalProductRecyclerViewAdapter.HorizontalRVViewHolder> {
    Context context;
    ArrayList<HorizontalModel> arrayList;

    public HorizontalProductRecyclerViewAdapter(Context context, ArrayList<HorizontalModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public HorizontalRVViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_horizontal,viewGroup, false);
        return new HorizontalRVViewHolder(view);
    }

    //associa cada campo e atribui valor
    @Override
    public void onBindViewHolder(@NonNull HorizontalRVViewHolder horizontalRVViewHolder, int i) {
        final HorizontalModel horizontalModel = arrayList.get(i);

        //set image from firebase db
        int image = horizontalModel.getImage();
        Log.d(TAG, "HorizontalRVViewHolder: image ta assim: "+image);
            Picasso.get().load(image).error(R.drawable.ad).into(horizontalRVViewHolder.imageViewThumb);



        //Set the action in each product clicked
        horizontalRVViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    //layout reference
    public class HorizontalRVViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        ImageView imageViewThumb;

        public HorizontalRVViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumb = itemView.findViewById(R.id.ad_thumb);
        }
    }
}
