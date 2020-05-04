package miips.com.Adapters.SearchAdapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import miips.com.Models.Post;
import miips.com.R;

public class PostViewHolder extends RecyclerView.ViewHolder {
    ImageView imageViewThumb;
    Context context;

    public PostViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        imageViewThumb = itemView.findViewById(R.id.imageview_widget);
    }

    public void bind(Post post) {
        Picasso.get().load(post.getUrl_product()).error(R.drawable.ad).into(imageViewThumb);
    }

}