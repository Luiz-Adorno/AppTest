package miips.com.Adapters.CartsListAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import miips.com.Cart.CartActivity;
import miips.com.Models.LocalCart;
import miips.com.R;
import miips.com.Utils.MyPreference;

public class RecyclerViewCartsListAdapter extends RecyclerView.Adapter<RecyclerViewCartsListAdapter.ViewHolder> {
    private static final String TAG = "RcMessage";
    Context mContext;
    ArrayList<LocalCart> arrayList;

    public RecyclerViewCartsListAdapter(Context mContext, ArrayList<LocalCart> arrayList) {
        this.arrayList = arrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carts_list_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final LocalCart localCart = arrayList.get(position);

        Picasso.get().load(localCart.getImg()).resize(400, 400).centerCrop().error(R.drawable.ad).into(holder.img);
        holder.title.setText(localCart.getName_local());
        String ende = localCart.getRua() + ", " + localCart.getNumero();
        holder.location.setText(ende);
        holder.telefone.setText(localCart.getTelefone());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPreference myPreference = new MyPreference(mContext);
                myPreference.setIDLOCAL(localCart.getIdLocal());
                v.getContext().startActivity(new Intent(v.getContext(), CartActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, location, telefone;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.ic_profile);
            title = itemView.findViewById(R.id.title);
            telefone = itemView.findViewById(R.id.telefone);
            location = itemView.findViewById(R.id.location);
        }
    }
}
