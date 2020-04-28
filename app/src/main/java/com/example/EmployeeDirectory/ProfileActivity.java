package com.example.EmployeeDirectory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ProfileActivity extends AppCompatActivity {
    DbHelper myDB;
    public String Name;
    public int Age;
    public  int id;
    public String Image;
    public String Gender;
    public TextView name_view;
    public TextView age_view;
    public  TextView gender_view;
    public TextView id_view;
    public ImageView image_view;
    Bitmap bitmap;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                id= -2;
            } else {
                id = extras.getInt("id");
            }
        } else {
            id= (int) savedInstanceState.getSerializable("id");
        }
        myDB=new DbHelper(this);
        Cursor cur = myDB.getByID(id);
        if(!cur.moveToFirst())
        {
            Toast.makeText(ProfileActivity.this,"Data not found",Toast.LENGTH_LONG).show();
            Intent intent =new Intent(ProfileActivity.this,ShowEmployeeList.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_profile);


        if(cur!=null)
        {
            if(cur.moveToFirst()){
                Name = cur.getString(0);
                Age=cur.getInt(1);
                Gender=cur.getString(2);
                Image = cur.getString(3);
            }
        }


        age_view = (TextView) findViewById(R.id.profileage);
        name_view=(TextView) findViewById(R.id.profilename);
        gender_view=(TextView) findViewById(R.id.profilegender);
        id_view=(TextView) findViewById(R.id.profileid);
        image_view=(ImageView) findViewById(R.id.ivProfile) ;


        age_view.setText(Integer.toString(Age));
        name_view.setText(Name);
        gender_view.setText(Gender);
        id_view.setText("Employee ID  :  "+id);
        loadImageFromStorage(Image);


    }
    public void goToMain(View v)
    {
        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
        startActivity(intent);
    }
    public void deleteData(View v)
    {
        Cursor tem = myDB.getByID(id);
        if(tem .moveToFirst())
        {
            int deletedRows = myDB.deleteData(Integer.toString(id));
            if(deletedRows > 0)
            {
                Intent intent =new Intent(ProfileActivity.this,ShowEmployeeList.class);
                startActivity(intent);
            }
            else
                Toast.makeText(ProfileActivity.this,"Data not Deleted",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(ProfileActivity.this,"Data not found",Toast.LENGTH_LONG).show();
            Intent intent =new Intent(ProfileActivity.this,ShowEmployeeList.class);
            startActivity(intent);
        }


    }

    public  void updateData(View v)
    {
        Cursor tem = myDB.getByID(id);
        if(tem.moveToFirst())
        {
            Intent intent =new Intent(ProfileActivity.this,UpdateProfileActivity.class);
            intent.putExtra("id",id);
            startActivity(intent);
        }

        else
        {
            Toast.makeText(ProfileActivity.this,"Data not found",Toast.LENGTH_LONG).show();
            Intent intent =new Intent(ProfileActivity.this,ShowEmployeeList.class);
            startActivity(intent);
        }


    }

        public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "image"+id+".jpg");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            mDrawable.setCircular(true);
            image_view.setImageDrawable(mDrawable);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
