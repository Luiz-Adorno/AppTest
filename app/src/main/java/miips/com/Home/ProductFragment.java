package miips.com.Home;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import miips.com.Adapters.HorizontalAdRecyclerViewAdapter;
import miips.com.Adapters.VerticalRecyclerViewAdapter;
import miips.com.Models.HomeModels.AdModel;
import miips.com.Models.HomeModels.HorizontalModel;
import miips.com.Models.HomeModels.VerticalModel;
import miips.com.R;

public class ProductFragment extends Fragment {

    private RecyclerView verticalRecyclerView, adRecyclerView;
    private VerticalRecyclerViewAdapter adapter;
    private HorizontalAdRecyclerViewAdapter adAdapter;
    private ArrayList<VerticalModel> arrayListVertical;
    private ArrayList<AdModel> listAd = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produtcs, container, false);
        verticalRecyclerView = view.findViewById(R.id.vertical_recycler_view);
        adRecyclerView = view.findViewById(R.id.ad_rc);

        setupRecyclerVertical();
        setupRecyclerViewAd();

        setDataVertical();
        setDataAd();


        return view;
    }


    @SuppressLint("WrongConstant")
    private void setupRecyclerVertical() {
        arrayListVertical = new ArrayList<>();

        verticalRecyclerView.setHasFixedSize(true);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new VerticalRecyclerViewAdapter(getActivity(), arrayListVertical);
        //make vertical adapter for recyclerview
        verticalRecyclerView.setAdapter(adapter);

    }

    private void setDataVertical() {
        for (int i = 1; i <= 5; i++) {

            VerticalModel verticalModel = new VerticalModel();
            verticalModel.setTitle("Title ");

            //set title color
            verticalModel.setColorTitle(getContext().getResources().getDrawable(R.drawable.title_color));

            ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

            for (int j = 0; j <= 5; j++) {
                HorizontalModel horizontalModel = new HorizontalModel();
                //set each product from db
                horizontalModel.setImage(R.drawable.ad);
                arrayListHorizontal.add(horizontalModel);
            }

            verticalModel.setArrayList(arrayListHorizontal);
            arrayListVertical.add(verticalModel);

        }
        adapter.notifyDataSetChanged();
    }

    private void setupRecyclerViewAd(){
        listAd = new ArrayList<>();

        adRecyclerView.setHasFixedSize(true);
        adRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adAdapter = new HorizontalAdRecyclerViewAdapter(getActivity(), listAd);
        //make vertical adapter for recyclerview
        adRecyclerView.setAdapter(adAdapter);
    }

    private void setDataAd() {
        AdModel adModel = new AdModel();

        for (int i = 0; i <= 5; i++) {
            adModel.setAd_one(R.drawable.ad_one);
            adModel.setAd_two(R.drawable.ad_two);

            listAd.add(adModel);
        }
        adAdapter.notifyDataSetChanged();
    }
}
