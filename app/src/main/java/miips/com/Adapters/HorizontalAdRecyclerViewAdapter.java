package miips.com.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import miips.com.Models.HomeModels.AdModel;
import miips.com.Models.HomeModels.HorizontalModel;
import miips.com.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HorizontalAdRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalAdRecyclerViewAdapter.HorizontalRVViewHolder> {
    Context context;
    ArrayList<AdModel> arrayList;

    public HorizontalAdRecyclerViewAdapter(Context context, ArrayList<AdModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public HorizontalRVViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ad_horizontal, viewGroup, false);
        return new HorizontalRVViewHolder(view);
    }

    //associa cada campo e atribui valor
    @Override
    public void onBindViewHolder(@NonNull HorizontalRVViewHolder horizontalRVViewHolder, int i) {
        final AdModel adModel = arrayList.get(i);

        //set image from firebase db
        int image_one = adModel.getAd_one();
        int image_two = adModel.getAd_two();
        Log.d(TAG, "HorizontalRVViewHolder: image ta assim: " + image_one);

        Picasso.get().load(image_one).error(R.drawable.ad_one).into(horizontalRVViewHolder.ad_one);
        Picasso.get().load(image_two).error(R.drawable.ad_two).into(horizontalRVViewHolder.ad_two);


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
    public class HorizontalRVViewHolder extends RecyclerView.ViewHolder {
        ImageView ad_one, ad_two;

        public HorizontalRVViewHolder(@NonNull View itemView) {
            super(itemView);
            ad_one = itemView.findViewById(R.id.ad_one);
            ad_two = itemView.findViewById(R.id.ad_two);
        }
    }
}
