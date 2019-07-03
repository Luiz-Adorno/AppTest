package miips.com.Home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import miips.com.Models.HorizontalModel;
import miips.com.Models.VerticalModel;
import miips.com.R;
import miips.com.AdaptersHome.VerticalRecyclerViewAdapter;

public class ProductFragment extends Fragment {


    RecyclerView verticalRecyclerView;
    VerticalRecyclerViewAdapter adapter;
    ArrayList<VerticalModel> arrayListVertical;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produtcs, container, false);
        verticalRecyclerView = view.findViewById(R.id.vertical_recycler_view);
        setupRecycler();
        setData();


        return view;
    }


    private void setupRecycler(){
        arrayListVertical = new ArrayList<>();

        verticalRecyclerView.setHasFixedSize(true);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new VerticalRecyclerViewAdapter(getActivity(),arrayListVertical);
        //make vertical adapter for recyclerview
        verticalRecyclerView.setAdapter(adapter);

    }
    private void setData() {
        for(int i = 1; i<=5; i++){
            VerticalModel verticalModel = new VerticalModel();
            verticalModel.setTitle("Title: "+i);
            ArrayList<HorizontalModel> arrayListHorizontal = new ArrayList<>();

            for(int j = 0; j<= 5; j++){
                HorizontalModel horizontalModel = new HorizontalModel();
                horizontalModel.setDescription("Description: "+j);
                horizontalModel.setName("Name: "+j);
                arrayListHorizontal.add(horizontalModel);
            }

            verticalModel.setArrayList(arrayListHorizontal);
            arrayListVertical.add(verticalModel);
        }
        adapter.notifyDataSetChanged();
    }

}
