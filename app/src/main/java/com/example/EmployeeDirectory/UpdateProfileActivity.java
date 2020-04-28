package com.example.EmployeeDirectory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UpdateProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public int id;
    DbHelper myDB;
    EditText name_txt, age_txt;
    Spinner gender_txt;
    public String Name;
    public int Age;
    public String Gender;
    public Bitmap bitmap;
    public String imagePath;
    private static final int SELECT_PHOTO = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

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
        if(cur!=null)
        {
            if(cur.moveToFirst()){
                Name = cur.getString(0);
                Age=cur.getInt(1);
                Gender=cur.getString(2);
                imagePath = cur.getString(3);

            }
        }

        name_txt = (EditText) findViewById(R.id.name);
        age_txt = (EditText) findViewById(R.id.age);
        gender_txt = (Spinner) findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_txt.setAdapter(adapter);
        gender_txt.setOnItemSelectedListener(this);



        loadImageFromStorage(imagePath);
        name_txt.setText(Name);
        age_txt.setText(Integer.toString(Age));



    }



    public void updateData(View v)
    {
        Cursor cur = myDB.getByID(id);
        if(!cur.moveToFirst())
        {
            Toast.makeText(UpdateProfileActivity.this, "Name is required", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UpdateProfileActivity.this,ShowEmployeeList.class);
            startActivity(intent);
        }
        if(name_txt.getText().toString().isEmpty())
        {
            Toast.makeText(UpdateProfileActivity.this, "Name is required", Toast.LENGTH_LONG).show();
        }
        else if(age_txt.getText().toString().isEmpty())
        {
            Toast.makeText(UpdateProfileActivity.this, "Age is required", Toast.LENGTH_LONG).show();
        }
        else if( bitmap==null)
        {
            Toast.makeText(UpdateProfileActivity.this, "Select an image", Toast.LENGTH_LONG).show();
        }
        else
        {
            saveToInternalStorage(bitmap,id);
            boolean updated = myDB.updateData(Integer.toString(id),name_txt.getText().toString(),Integer.parseInt(age_txt.getText().toString()),Gender,imagePath);
            if(updated == true)
            {
                Intent intent = new Intent(UpdateProfileActivity.this,ProfileActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(UpdateProfileActivity.this, "ERROR!!!! NOT UPDATED",Toast.LENGTH_LONG).show();
            }
        }


    }



    ////////////////////     select image button ON CLICK      //////////////////////////////////////////
    public void selectImage(View v) {
//        private static final int REGUEST_CODE = 100;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

    }

    ////////////////////////////////////////////////         Activity to upload a image      ///////////////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            switch (requestCode) {
                case SELECT_PHOTO:
                    if (resultCode == RESULT_OK) {
                        Uri IMAGE_URI = imageReturnedIntent.getData();
                        bitmap =null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), IMAGE_URI);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(bitmap==null) Toast.makeText(UpdateProfileActivity.this, "bitmap is null",Toast.LENGTH_LONG).show();

                    }
            }
        }

    }
    ////////////////////////////////// store image in internal  DB ///////////////////////////////////
     /*
             IMAGE IS SAVED IN INTERNAL STORAGE. THE PATH IS SAVED INTO THE DATABASE
     */

    private String saveToInternalStorage(Bitmap bitmapImage,int id){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
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


    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "image"+id+".jpg");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }




    /////////////////////////////////////           API PERMISSSION CODE        ///////////////////////////////////////////////////////////



    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    //////////////////////////   OVERRIDDEN METHOD FOR SNIPPER  ///////////////

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Gender = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
