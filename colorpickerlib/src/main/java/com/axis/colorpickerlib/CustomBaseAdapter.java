package com.axis.colorpickerlib;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DDA_Admin on 5/5/16.
 */
public class CustomBaseAdapter extends BaseAdapter implements View.OnClickListener {
    Context context;
    ArrayList<ColorCategory> rowItems;
    ColorCallBack callBack;
    String selectedColor;
    ThemeCallBack themeCallBack;



    public CustomBaseAdapter(Context context, ArrayList<ColorCategory> items) {
        this.context = context;
        this.rowItems = items;
    }



    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.color1) {
            selectedColor = v.getTag().toString();
            callBack.setColor(selectedColor);
            themeCallBack.setCircleColor(selectedColor);

        } else if (i == R.id.color2) {
            selectedColor = v.getTag().toString();
            callBack.setColor(selectedColor);
            themeCallBack.setCircleColor(selectedColor);

        } else if (i == R.id.color3) {
            selectedColor = v.getTag().toString();
            callBack.setColor(selectedColor);
            themeCallBack.setCircleColor(selectedColor);

        } else if (i == R.id.color4) {
            selectedColor = v.getTag().toString();
            callBack.setColor(selectedColor);
            themeCallBack.setCircleColor(selectedColor);

        } else if (i == R.id.color5) {
            selectedColor = v.getTag().toString();
            callBack.setColor(selectedColor);
            themeCallBack.setCircleColor(selectedColor);

        }

    }



    /*private view holder class*/
    private class ViewHolder {
        TextView txtTitle;
        ImageView color1;
        ImageView color2;
        ImageView color3;
        ImageView color4;
        ImageView color5;

        GradientDrawable dr_color_1;
        GradientDrawable dr_color_2;
        GradientDrawable dr_color_3;
        GradientDrawable dr_color_4;
        GradientDrawable dr_color_5;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_component, null);
            holder = new ViewHolder();
            Drawable border = convertView.getResources().getDrawable(R.drawable.themes_border);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.category);
            holder.color1 = (ImageView) convertView.findViewById(R.id.color1);

            holder.color1.setImageResource(R.drawable.themes_border);
            holder.color2 = (ImageView) convertView.findViewById(R.id.color2);

            holder.color2.setImageResource(R.drawable.themes_border);
            holder.color3 = (ImageView) convertView.findViewById(R.id.color3);

            holder.color3.setImageResource(R.drawable.themes_border);
            holder.color4 = (ImageView) convertView.findViewById(R.id.color4);

            holder.color4.setImageResource(R.drawable.themes_border);
            holder.color5 = (ImageView) convertView.findViewById(R.id.color5);

            holder.color5.setImageResource(R.drawable.themes_border);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();

        }
        ColorCategory rowItem = (ColorCategory) getItem(position);
        holder.txtTitle.setText(rowItem.getTitle());

        String[] colorArr=rowItem.getColors();

        holder.dr_color_1 = (GradientDrawable) holder.color1.getDrawable();
        holder.dr_color_1.setColor(Color.parseColor(colorArr[0]));
        holder.color1.setTag(colorArr[0]);

        holder.dr_color_2 = (GradientDrawable) holder.color2.getDrawable();
        holder.dr_color_2.setColor(Color.parseColor(colorArr[1]));
        holder.color2.setTag(colorArr[1]);

        holder.dr_color_3 = (GradientDrawable) holder.color3.getDrawable();
        holder.dr_color_3.setColor(Color.parseColor(colorArr[2]));
        holder.color3.setTag(colorArr[2]);

        holder.dr_color_4 = (GradientDrawable) holder.color4.getDrawable();
        holder.dr_color_4.setColor(Color.parseColor(colorArr[3]));
        holder.color4.setTag(colorArr[3]);

        holder.dr_color_5 = (GradientDrawable) holder.color5.getDrawable();
        holder.dr_color_5.setColor(Color.parseColor(colorArr[4]));
        holder.color5.setTag(colorArr[4]);






        holder.color1.setOnClickListener(this);
        holder.color2.setOnClickListener(this);
        holder.color3.setOnClickListener(this);
        holder.color4.setOnClickListener(this);
        holder.color5.setOnClickListener(this);




        return convertView;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    public void setCallBack(ColorCallBack callBack) {
        this.callBack = callBack;
    }

    public void setThemeCallBack(ThemeCallBack callBack) {
        this.themeCallBack = callBack;
    }


}
