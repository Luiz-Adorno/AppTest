package miips.com.Adapters.LocalAdapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import miips.com.Models.Products.Products;
import miips.com.Products.ProductActivity;
import miips.com.R;
import miips.com.Utils.MyPreference;

/**
 * Created by User on 1/17/2018.
 */

public class StaggeredRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "StaggeredRecyclerViewAd";

    private Context mContext;
    ArrayList<Products> products;

    public StaggeredRecyclerViewAdapter(Context context, ArrayList<Products> products) {
        mContext = context;
        this.products = products;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Products pro = products.get(position);
        Log.d(TAG, "onBindViewHolder: called.");

        Picasso.get().load(pro.getUrl_product()).error(R.drawable.ad).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPreference myPreference = new MyPreference(mContext);
                myPreference.setIDCLICK(pro.getDocId());

                //go go product activity
                view.getContext().startActivity(new Intent(view.getContext(), ProductActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.imageview_widget);
        }
    }
}
