package miips.com.Adapters.CartAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;

import miips.com.Cart.CartActivity;
import miips.com.Models.Cart;
import miips.com.Products.ProductActivity;
import miips.com.R;
import miips.com.Utils.MyPreference;

public class RecyclerViewCartAdapter extends RecyclerView.Adapter<RecyclerViewCartAdapter.ViewHolder> {
    private static final String TAG = "RcMessage";
    Context mContext;
    ArrayList<Cart> arrayList;
    private int totalPrice = 0;

    public RecyclerViewCartAdapter(Context mContext, ArrayList<Cart> arrayList) {
        this.arrayList = arrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Cart products = arrayList.get(position);

        holder.title.setText(products.getName());

        String category = products.getCategory();

        if (category.equals("Vestuário") || category.equals("Calçado")) {
            holder.sizeLayout.setVisibility(View.VISIBLE);
            String size = products.getSize();
            holder.size.setText(size);
        }

        Picasso.get().load(products.getImg()).resize(400, 400).centerCrop().error(R.drawable.ad).into(holder.image);

        holder.price.setText(products.getPrice());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPreference myPreference = new MyPreference(mContext);
                myPreference.setIDCLICK(products.getId());
                v.getContext().startActivity(new Intent(v.getContext(), ProductActivity.class));
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertSingOut = new AlertDialog.Builder(mContext);
                alertSingOut.setMessage("Deseja deletar produto do carrinho?");
                alertSingOut.setCancelable(true);
                alertSingOut.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        String userID = mAuth.getUid();
                        MyPreference myPreference = new MyPreference(mContext);
                        String localId = myPreference.getIdLocal();
                        String productId = products.getId();


                        DocumentReference documentReference = db.collection("users")
                                .document(userID).collection("cart").document(localId).collection("products").document(productId);
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //remove item from the recyclerview
                                arrayList.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(), arrayList.size());
                                holder.itemView.setVisibility(View.GONE);

                                //update the new total price
                                if (mContext instanceof CartActivity) {
                                    try {
                                        int i;
                                        for(i = 0; i < arrayList.size(); i++){
                                            Cart products = arrayList.get(i);
                                            String y = products.getPrice().replace(",", "");
                                            int value1 = Integer.parseInt(y);
                                            totalPrice = totalPrice + value1;
                                        }
                                        Log.d(TAG, "onSuccess: discruso: "+ totalPrice);
                                        ((CartActivity)mContext).setTotalPrice(totalPrice);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "Não foi possivel deletar produto, tente mais tarde.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                alertSingOut.setNegativeButton("Não", null);
                alertSingOut.create().show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, size, price, delete;
        ImageView image;
        LinearLayout sizeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ic_profile);
            sizeLayout = itemView.findViewById(R.id.sizelayout);
            title = itemView.findViewById(R.id.name_product);
            size = itemView.findViewById(R.id.size);
            price = itemView.findViewById(R.id.price);
            delete = itemView.findViewById(R.id.delete_product);
        }
    }
}
