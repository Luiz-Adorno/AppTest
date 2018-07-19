package miips.com.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.zip.Inflater;

import miips.com.R;
import miips.com.Utils.UniversalImageLoader;

public class EditProfileFragment extends Fragment {

    private ImageView mProfilePhoto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);

        mProfilePhoto = view.findViewById(R.id.ic_profile);

        setProfileImage();

        //back arrow
        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void setProfileImage(){

        String imgURL ="www.google.com.br/search?q=not+image&safe=off&rlz=1C1EJFA_enBR736BR738&source=lnms&tbm=isch&sa=X&ved=0ahUKEwin4-HXzbDbAhXGIJAKHTOVA2kQ_AUICigB&biw=1366&bih=637#imgrc=PhwIv5U7lko00M:";

        UniversalImageLoader.setImage(imgURL, mProfilePhoto, null,  "https://");
    }



}
