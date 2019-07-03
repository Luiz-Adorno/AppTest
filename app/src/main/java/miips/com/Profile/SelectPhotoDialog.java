package miips.com.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SelectPhotoDialog extends DialogFragment {

    private static final String TAG = "SelectPhotoDialog";
    private static final int PICKFILE_REQUEST_CODE = 12;
    private static final int CAMERA_REQUEST_CODE = 21;

    public interface OnPhotoSelectedListener{
        void getImagePath(Uri imagePah);
    }
    OnPhotoSelectedListener mOnPhotoSelectedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onClick: accessing phones memory");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,PICKFILE_REQUEST_CODE);
        getDialog().dismiss();
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Results when selecting a new image from memory
        if(requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: image uri: " + selectedImageUri);

            //send the uri to PostFragment & dismiss dialog
            mOnPhotoSelectedListener.getImagePath(selectedImageUri);
            getDialog().dismiss();
        }

    }

    @Override
    public void onAttach(Context context) {
        try{
            mOnPhotoSelectedListener = (OnPhotoSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
        super.onAttach(context);
    }
}
