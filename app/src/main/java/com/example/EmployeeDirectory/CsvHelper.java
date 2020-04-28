package com.example.EmployeeDirectory;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
public class CsvHelper {
    public Context context;
    public Activity activity;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    DbHelper myDb;
    public CsvHelper(Context context){
        this.context=context;
        myDb = new DbHelper(context);
    }

    ///////////////////////////////////     WRITING THE CSV FILE    ////////////////////////////////////
    public String WriteCSV() throws IOException {


        File path= new File(Environment.getExternalStorageDirectory(), "");
        if (!path.exists()) {
            path.mkdirs();
        }

            File filePath =new File(path,"employee.csv");
            CSVWriter writer = new CSVWriter(new FileWriter(filePath));

        List<String[]> ret =new ArrayList<String[]>();
        Cursor cur =  myDb.getAllData();
        while(cur.moveToNext())
        {
            String id =Integer.toString(cur.getInt(0));
            String name = cur.getString(1);
            String gender = cur.getString(2);
            String age =Integer.toString(cur.getInt(3));
            String imagePath =cur.getString(4);
            File f=new File(imagePath, "image"+id+".jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            String image = BitMapToString(bitmap);
            ret.add(new String[]{id,name,gender,age,image});
        }

            writer.writeAll(ret);
            writer.close();
            return filePath.getAbsolutePath().toString();

    }

    ///////////////////////////////////     READING THE CSV FILE    ////////////////////////////////////
    public int readCSV(String path) throws IOException {
        File file= new File(Environment.getExternalStorageDirectory(), path);
        CSVReader csvReader = new CSVReader(new FileReader(file));
        List content = csvReader.readAll();
        String [] row = null;
        int total = 0;
        for (Object object : content) {
            row = (String[]) object;
            String imagePath = saveToInternalStorage(StringToBitMap(row[4]),myDb.getSize());
            if(myDb.insertData(row[1],Integer.parseInt(row[3]),row[2],imagePath))
                total++;
        }

        csvReader.close();
        return  total;
    }
    ////////////////    BITMAP TO STRING  ////////////////////////////////////
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    /////////////////    STRING TO BITMAP ///////////////////////
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
     /////////////////////////  SAVE THE IMAGE TO INTERNAL STORAGE   ////////////////////
    private String saveToInternalStorage(Bitmap bitmapImage,int id){
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"image"+id+".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

}
