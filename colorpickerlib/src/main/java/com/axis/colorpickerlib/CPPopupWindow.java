package com.axis.colorpickerlib;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.axis.colorpickerlib.models.ColorScheme;
import com.axis.colorpickerlib.models.ColorSchemeList;
import com.google.gson.Gson;


/**
 * Created by DDA_Admin on 5/5/16.
 */
public class CPPopupWindow extends ColorPickerPopup implements View.OnClickListener,TouchCallback,ThemeCallBack{


    int r=255;
    int g=255;
    int b=255;
    int colorSquare;
    int colormargin;
    float darkFacter=0;
    String color="#000000";
    int colorBtnBorder=R.drawable.recent_color_border;

    HSV_Circle setcolors;
    CustomBaseAdapter adapter;

    LinearLayout recent_colors;
    LinearLayout recent_colors_mask;
    LinearLayout pickerbg;
    ListView listView;
    EditText hashColor;
    EditText rValue;
    EditText gValue;
    EditText bValue;
    ImageButton pickerButton;
    SeekBar darkness;
    GradientDrawable reColor;
    ImageView colorBtn;
    GradientDrawable dr_colorBtn;
    Drawable border;

    ImageView im_themes;
    ImageView im_picker;
    Drawable dr_theme;
    Drawable dr_picker;

    ArrayList<ColorCategory> rowItems;

    private ColorCallBack tempCall;
    boolean onHSVcircle=false;
    int changeHash = 0;


    public CPPopupWindow(Context context, boolean canDismiss) {
        super(context, canDismiss);

        initLayout();

        File Root=Environment.getExternalStorageDirectory();
        File Dir=new File(Root,"Android/data/com.axis.drawingdesk.v3/files/colors");
        File file=new File(Dir,"colorjson.txt");

        if (file.exists()){
            readFromFile();
        }


        colorSquare =(int) context.getResources().getDimension(R.dimen.RecentColorSquare);
        border = context.getResources().getDrawable(R.drawable.recent_color_border);
        colormargin=(int) context.getResources().getDimension(R.dimen.ValuePointerSpace);


    }


    void initLayout(){


        LayoutInflater inflater =(LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate( R.layout.popup_layout_bg, null );
        addView(layout);


        pickerbg=(LinearLayout) layout.findViewById(R.id.picker_layout);
        listView = (ListView)layout.findViewById(R.id.list);

        im_themes=(ImageView)layout.findViewById(R.id.im_themes);
        im_picker=(ImageView)layout.findViewById(R.id.im_picker);

        dr_theme = mContext.getResources().getDrawable(R.drawable.themesicon);
        dr_picker = mContext.getResources().getDrawable(R.drawable.pickericon);

        im_themes.setImageDrawable(dr_theme);
        im_picker.setImageDrawable(dr_picker);

        im_themes.setColorFilter(Color.parseColor("#acabad"), PorterDuff.Mode.MULTIPLY);
        im_picker.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.MULTIPLY);


//===================themes==========================================
        RelativeLayout themes=(RelativeLayout)layout.findViewById(R.id.themes);
        themes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listView.setVisibility(View.VISIBLE);
                pickerbg.setVisibility(View.GONE);

                im_themes.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.MULTIPLY);
                im_picker.setColorFilter(Color.parseColor("#acabad"), PorterDuff.Mode.MULTIPLY);


            }
        });


        String[] Blue_sky = {"#16193b","#35478c","#4e7ac7","#7fb2f0","#add5f7"};
        String[] Happy_Mom = {"#3fb8af","#7fc7af","#dad8a7","#ff9e9d","#ff3d7f"};
        String[] Morning = {"#b3a184","#fff8ee","#ffefd6","#5084b3","#a2d3ff"};
        String[] Winter_season = {"#00275a","#1682a2","#5f9ace","#b0b0b0","#ffffff"};
        String[] Christmas = {"#2c8249","#c0c0b0","#fafbf7","#c19469","#b22417"};
        String[] Halloween = {"#ffbb4d","#fc962e","#e76807","#952903","#611704"};
        String[] Skin_Light = {"#fadbae","#ffc794","#ffa16d","#e87551","#ff7865"};
        String[] Skin_Dark = {"#68432e","#8e5533","#ac7451","#5f341a","#cc9164"};
        String[] Watermelon = {"#7d8a2e","#c9d787","#ffffff","#ffc0a9","#ff8598"};
        String[] Sandy = {"#e6e2af","#a7a37e","#efecca","#046380","#002f2f"};
        String[] Firenze = {"#468966","#fff0a5","#ffb03b","#b64926","#8e2800"};
        String[] Vitamin_C = {"#004358","#1f8a70","#bedb39","#ffe11a","#fd7400"};
        String[] Neutral_Blue = {"#fcfff5","#d1dbbd","#91aa9d","#3e606f","#193441"};
        String[] Phaedra = {"#ff6138","#ffff9d","#beeb9f","#79bd8f","#00a388"};
        String[] Afternoon = {"#cfc291","#fff6c5","#a0e7d8","#ff712c","#695d46"};


        String[] title = {"Blue Sky","Happy Mom","Morning","Winter Season","Christmas","Halloween","Skin Light","Skin Dark",
                "Watermelon","Sandy","Firenze","Vitamin C","Neutral Blue","Phaedra","Afternoon"};
        String[][] colors={Blue_sky,Happy_Mom,Morning,Winter_season,Christmas,Halloween,Skin_Light,Skin_Dark,Watermelon,
                Sandy,Firenze,Vitamin_C,Neutral_Blue,Phaedra,Afternoon};

        rowItems = new ArrayList<ColorCategory>();
        for (int i = 0; i < title.length; i++) {
            // make ColorCategory type object, name item
            ColorCategory item = new ColorCategory(title[i],colors[i]);
            rowItems.add(item);
        }
        Collections.reverse(rowItems);// reserse the local list
        adapter=new CustomBaseAdapter(mContext, rowItems);
        listView.setAdapter(adapter);

        //===================picker==========================================
        setcolors=(HSV_Circle)layout.findViewById(R.id.color_picker);
        setcolors.setTouchCallback(this);
        hashColor=(EditText) layout.findViewById(R.id.hash);
        rValue=(EditText) layout.findViewById(R.id.r_value);
        gValue=(EditText) layout.findViewById(R.id.g_value);
        bValue=(EditText) layout.findViewById(R.id.b_value);
        pickerButton = (ImageButton)layout. findViewById(R.id.pickerBtn);
        recent_colors=(LinearLayout) layout.findViewById(R.id.recent_colors);
        recent_colors_mask=(LinearLayout) layout.findViewById(R.id.recent_colors_mask);

        hashColor.setSelection(hashColor.getText().length());
        rValue.setSelection(rValue.getText().length());
        gValue.setSelection(gValue.getText().length());
        bValue.setSelection(bValue.getText().length());

        darkness = (SeekBar)layout.findViewById(R.id.dark);

        RelativeLayout picker=(RelativeLayout)layout. findViewById(R.id.picker);
        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickerbg.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);

                im_themes.setColorFilter(Color.parseColor("#acabad"), PorterDuff.Mode.MULTIPLY);
                im_picker.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.MULTIPLY);


                for(int i=0; i<10;i++){

                    colorBtn = new ImageView(mContext);
                    colorBtn.setPadding(0,0,0,0);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(colorSquare, colorSquare);
                    params.setMargins(0,0,colormargin,0);
                    colorBtn.setLayoutParams(params);
                    colorBtn.setImageResource(R.drawable.recent_color_border);
                    dr_colorBtn = (GradientDrawable) colorBtn.getDrawable();
                    dr_colorBtn.setColor(Color.WHITE);
                    recent_colors_mask.addView(colorBtn);
                }

            }
        });

        // set hash color===================================================
        hashColor.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(changeHash == 2) return;
                changeHash = 1;

                final String hash = s.toString();
                final String hash1 = "#";

                if (hash.equals("")) {
                }
                // validate is it a hash color
                else{

                    int length = hashColor.getText().length();
                    String text = hashColor.getText().toString();
                    String last=text.substring(text.length() - 1);

                    String[] validate = {"a", "b", "c","d","e","f","A", "B", "C","D","E","F","1","2","3","4","5","6","7","8","9","0"};

                    Pattern p = Pattern.compile("^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{6})$");
                    Matcher m = p.matcher(hash);

                    if (!Arrays.asList(validate).contains(last)) {

                        //hashColor.getText().delete(length - 1, length);
                        int l = hashColor.getText().length();
                        String str = hashColor.getText().toString().substring(0, l-1);
                        hashColor.setText(str);
                        hashColor.setSelection(l-1);

                    }

                    if (m.matches()) {
                        if(!onHSVcircle) {
                            setcolors.setHashColor((hash1.concat(hash)).trim());

                        }
                        //color = hash;


                        //==========  change r g b values   ==========
                        int color = Color.parseColor((hash1.concat(hash)).trim());

                        int rVal = (color >> 16) & 0xFF;
                        int gVal = (color >> 8) & 0xFF;
                        int bVal = (color >> 0) & 0xFF;

                        r=rVal;
                        g=gVal;
                        b=bVal;


                        rValue.setText(Integer.toString(r));
                        gValue.setText(Integer.toString(g));
                        bValue.setText(Integer.toString(b));


                        //============================================

                        changeHash = 0;

                    }


                        /*else{
                            for (char c: hashColor.getText().toString().toCharArray()) {
                                Log.d("character","="+c);
                                *//*int txtlentch = hashColor.getText().length();
                                if(!Arrays.asList(validate).contains(c)){
                                    String st = hashColor.getText().toString().replace(hashColor.getText().toString(), hashCode);
                                    hashCode=st;
                                    hashColor.setText(hashCode);
                                    hashColor.setSelection(txtlentch);
                                }
                                else{
                                    hashCode=hashColor.getText().toString();
                                    hashColor.setText(hashCode);
                                    hashColor.setSelection(txtlentch);
                                }*//*

                            }
                        }*/





                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


// set r value===================================================
        rValue.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(changeHash == 1) return;
                changeHash = 2;
                final String rString = s.toString();
                if(rString.equals("")){}

                else {

                    int rVal = Integer.parseInt(rString);

                    if (rVal >= 0 && rVal <= 255) {
                        r=rVal;
                        String rgbToHex = String.format("#%02X%02X%02X", r, g, b);
                        if(!onHSVcircle) {
                            setcolors.setHashColor(rgbToHex);
                        }
                        String hex = rgbToHex.replace("#", " ");
                        hashColor.setText(hex.trim());
                        //color=rgbToHex;
                    }
                    else{
                        rValue.setText("255");
                    }
                }
                changeHash = 0;

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });




        // set g value===================================================
        gValue.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(changeHash == 1) return;
                changeHash = 2;
                final String gString = s.toString();
                if(gString.equals("")){System.out.println("....");}

                else {
                    int gVal = Integer.parseInt(gString);

                    if (gVal >= 0 && gVal <= 255) {
                        g=gVal;
                        String rgbToHex = String.format("#%02X%02X%02X", r, g, b);
                        if(!onHSVcircle) {
                            setcolors.setHashColor(rgbToHex);
                        }
                        String hex = rgbToHex.replace("#", " ");
                        hashColor.setText(hex.trim());
                        //color=rgbToHex;
                    }
                    else{
                        gValue.setText("255");
                    }
                }
                changeHash=0;
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        // set b value===================================================
        bValue.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(changeHash == 1) return;
                changeHash = 2;
                final String bString = s.toString();
                if (bString.equals("")) {}
                else {
                    int bVal = Integer.parseInt(bString);

                    if (bVal >= 0 && bVal <= 255) {
                        b=bVal;
                        //set R G B color to color picker===============================
                        String rgbToHex = String.format("#%02X%02X%02X", r, g, b);
                        if(!onHSVcircle) {
                            setcolors.setHashColor(rgbToHex);
                        }
                        String hex = rgbToHex.replace("#", " ");
                        hashColor.setText(hex.trim());
                        //color=rgbToHex;
                    }
                    else{
                        bValue.setText("255");
                    }
                }
                changeHash=0;
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });



//set color value to Edit text viewssss========================
 /*       setcolors.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setTextValuesOnTouch();
                return false;
            }
        });*/


//color picker======================================================

        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                tempCall.enablePicker();


            }
        });

  /*      darkness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                darkFacter = progresValue;
                float facter = darkFacter / 1000;
                facter = 1 - facter;
                setcolors.setDarkness(facter);//setting darkness

                //set color value to Edit text viewssss
                hashColor.setText(setcolors.hexColor);//set hEX COLOR

                int color = Color.parseColor(setcolors.hexColor);//SET RGB

                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color >> 0) & 0xFF;

                rValue.setText(Integer.toString(r));
                gValue.setText(Integer.toString(g));
                bValue.setText(Integer.toString(b));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
*/


    }

    private void setTextValuesOnTouch() {
        //color=setcolors.hexColor;
        //String hex = setcolors.hexColor.replace("#", " ");
        // Log.d("hexcolor",""+hex);
        //hashColor.setText(hex);

        int color = Color.parseColor(setcolors.hexColor);

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;

        rValue.setText(Integer.toString(r));
        gValue.setText(Integer.toString(g));
        bValue.setText(Integer.toString(b));


        String rgbToHex = String.format("#%02X%02X%02X", r, g, b);
        String hex1 = rgbToHex.replace("#", " ");
        hashColor.setText(hex1.trim());

        hashColor.setSelection(hashColor.getText().length());
        rValue.setSelection(rValue.getText().length());
        gValue.setSelection(gValue.getText().length());
        bValue.setSelection(bValue.getText().length());
    }

    private  void readFromFile(){
//=================  read external stroage ===============================
        String read=" ";
        File Root= Environment.getExternalStorageDirectory();
        File Dir=new File(Root,"Android/data/com.axis.drawingdesk.v3/files/colors");
        File file=new File(Dir,"colorjson.txt");

        try {
            FileInputStream inputStream=new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            read = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("read", read);

//================  get values from read json & set it to adapter ====================
        Gson gson=new Gson();
        //set json response to ColorSchemeList class.
        ColorSchemeList colorSchemeList=gson.fromJson(read,ColorSchemeList.class);

        List<ColorScheme> scheme_list=colorSchemeList.getData().getThemes();

        for (ColorScheme schemes :scheme_list) {
            //Log.d("LOG", schemes.getName());
            String name = schemes.getName();
            List<com.axis.colorpickerlib.models.Color> colorsList= schemes.getColors();
            String c[] = new String[5];//use only 5 colors for one scheme
            for (int i =0; i<colorsList.size();i++){
                c[i]=colorsList.get(i).getCode();
            }

            ColorCategory item = new ColorCategory(name,c);
            rowItems.add(item);


        }
        Collections.reverse(rowItems);// reserse the list that get from json
        adapter=new CustomBaseAdapter(mContext, rowItems);
        adapter.setCallBack(tempCall);
        listView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        String color=v.getTag().toString();
        setcolors.setrecentColor(color);
        setTextValuesOnTouch();


    }

    public void setColorCallBack(ColorCallBack call){
        this.tempCall = call;
        adapter.setCallBack(call);
        setcolors.setCallBack(call);
        adapter.setThemeCallBack(this);
    }
    public void setColorOnLongClick(String color){
        setcolors.setrecentColor(color);
        setTextValuesOnTouch();
    }

    public void showRecentColor(){
        recent_colors_mask.removeAllViews();

        for(int i=0; i<10;i++){

            colorBtn = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(colorSquare, colorSquare);
            params.setMargins(0,0,colormargin,0);
            colorBtn.setLayoutParams(params);
            colorBtn.setImageResource(R.drawable.recent_color_border);
            dr_colorBtn = (GradientDrawable) colorBtn.getDrawable();
            dr_colorBtn.setColor(Color.WHITE);
            recent_colors_mask.addView(colorBtn);
        }

        recent_colors.removeAllViews();
        ArrayList<String> recentColorList = RecentColorDb.getInstance().getRecentColors();
        for (String color: recentColorList) {

            colorBtn = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(colorSquare, colorSquare);
            params.setMargins(0,0,colormargin,0);
            colorBtn.setLayoutParams(params);
            colorBtn.setImageResource(R.drawable.recent_color_border);

            dr_colorBtn = (GradientDrawable) colorBtn.getDrawable();
            dr_colorBtn.setColor(Color.parseColor(color));
            recent_colors.addView(colorBtn);
            colorBtn.setOnClickListener(this);
            colorBtn.setTag(color);


        }
    }

    //set color value to Edit text viewssss========================
    @Override
    public void OnTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onHSVcircle=true;
            case MotionEvent.ACTION_MOVE:
                onHSVcircle=true;
                setTextValuesOnTouch();
            case MotionEvent.ACTION_UP:
                onHSVcircle=false;

        }
    }

    @Override
    public void setCircleColor(String selectedColor) {
        setcolors.setrecentColor(selectedColor);
        setTextValuesOnTouch();

    }
}