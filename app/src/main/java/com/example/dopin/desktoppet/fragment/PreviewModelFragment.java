package com.example.dopin.desktoppet.fragment;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.dopin.desktoppet.R;
import com.example.dopin.desktoppet.activity.MainActivity;
import com.example.dopin.desktoppet.adapter.ImageViewAdapter;
import com.example.dopin.desktoppet.service.FloatWindowService;
import com.example.dopin.desktoppet.util.AssetIO;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by mpi on 2017/5/6.
 */

public class PreviewModelFragment extends Fragment implements ViewPager.OnPageChangeListener
{
   private ViewPager showModelImageView;//展示图片的viewpager
   private String[] dirName={"cat","totoro"};
   private ArrayList<ImageView> imageViews;//viewpager的子元素

    private View view;
    private int selectedModel=0;//当前屏幕显示的模型，0对应pictureName下标为0的图片
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.layout_preview_model_fragment,container,false);

        showModelImageView=(ViewPager) view.findViewById(R.id.show_model);
        Button btnChangePet=(Button)view.findViewById(R.id.btn_change_pet);
        btnChangePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String style=dirName[selectedModel];
                ((MainActivity)getActivity()).changePet(style);
            }
        });

        MainActivity.curFragment=MainActivity.previewFragment;

        setImageViews();

        //设置Adapter
        showModelImageView.setAdapter(new ImageViewAdapter(imageViews));
        //设置监听，主要是设置点点的背景
        showModelImageView.addOnPageChangeListener(this);

        showModelImageView.setCurrentItem(0);

        return view;
    }

    private void setImageViews(){
        imageViews=new ArrayList<>();
        for(String path:dirName){
            Bitmap bitmap=AssetIO.getBitmap(path + "/default1.png");
            ImageView imageView=new ImageView(getContext());
            imageView.setImageBitmap(bitmap);
            imageViews.add(imageView);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectedModel=position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
