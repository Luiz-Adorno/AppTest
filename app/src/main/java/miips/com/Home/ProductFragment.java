package miips.com.Home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import miips.com.Models.RecyclerViewModels.HorizontalModel;
import miips.com.Models.RecyclerViewModels.VerticalModel;
import miips.com.R;
import miips.com.AdaptersHome.VerticalRecyclerViewAdapter;

public class ProductFragment extends Fragment {

    RecyclerView verticalRecyclerView;
    VerticalRecyclerViewAdapter adapter;
    ArrayList<VerticalModel> arrayListVertical;
    Image im;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produtcs, container, false);
        verticalRecyclerView = view.findViewById(R.id.vertical_recycler_view);
        setupRecycler();
        setData();


        return view;
    }


    @SuppressLint("WrongConstant")
    private void setupRecycler() {
        arrayListVertical = new ArrayList<>();

        verticalRecyclerView.setHasFixedSize(true);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new VerticalRecyclerViewAdapter(getActivity(), arrayListVertical);
        //make vertical adapter for recyclerview
        verticalRecyclerView.setAdapter(adapter);

    }

    private void setData() {
        for (int i = 1; i <= 5; i++) {

            VerticalModel verticalModel = new VerticalModel();
            verticalModel.setTitle("Title ");
            if (i == 1) {
                verticalModel.setColorTitle(ContextCompat.getColor(getContext(), R.color.red_fly));
            } else if (i == 2) {
                verticalModel.setColorTitle(ContextCompat.getColor(getContext(), R.color.green));
            }
            ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

            for (int j = 0; j <= 5; j++) {
                HorizontalModel horizontalModel = new HorizontalModel();
                //set each product from db
                horizontalModel.setName("Equivalente: " + j);
                horizontalModel.setImage(R.drawable.ad);
                arrayListHorizontal.add(horizontalModel);
            }

            verticalModel.setArrayList(arrayListHorizontal);
            arrayListVertical.add(verticalModel);

        }
        adapter.notifyDataSetChanged();
    }

}
