package com.explodeman.castles;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.explodeman.castles.constants.IConst;
import com.explodeman.castles.utils.IToast;
import com.explodeman.castles.utils.UtilPicture;

public class PictureFragment extends Fragment implements IConst, IOnBackProcessed, IToast {

    private ImageView ivPicture;
    private IMain iMain;

    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 2.0f;

    public PictureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iMain = (IMain) context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);

        initView(view);

        if(getArguments() != null) {
//            shortToast(getContext(),"!!!");
            byte[] bytes = getArguments().getByteArray(KEY_BYTES_PICTURE);
            Bitmap bitmap = UtilPicture.BytesToBitmap(bytes);
            ivPicture.setImageBitmap(bitmap);

        }

        return view;
    }

    private void initView(View view) {
        ivPicture = view.findViewById(R.id.ivOnePicture);
    }


    @Override
    public void onBackProcessed() {
        iMain.popBackStack();
    }


}