package com.example.dopin.desktoppet.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dopin on 2017/5/15.
 */
public class ImageViewAdapter extends PagerAdapter
{
    private ArrayList<ImageView> images;
    public ImageViewAdapter(ArrayList images){
        this.images=images;
    }
    @Override
    public int getCount()
    {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view==object;
    }

    @Override
    public void destroyItem(View container, int position, Object object)
    {
        ViewPager viewPager=(ViewPager)container;
        viewPager.removeView(images.get(position));
    }

    /**
     * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
     */
    @Override
    public Object instantiateItem(View container, int position)
    {
        ((ViewPager)container).addView(images.get(position));
        return images.get(position);

    }

}