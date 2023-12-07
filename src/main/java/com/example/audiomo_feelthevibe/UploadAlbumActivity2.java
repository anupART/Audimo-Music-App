package com.example.audiomo_feelthevibe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.audiomo_feelthevibe.Model.Constants;
import com.example.audiomo_feelthevibe.Model.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadAlbumActivity2 extends AppCompatActivity implements View.OnClickListener {
private Button buttonchoose;
private Button buttonUpload;
private EditText edittextName;
private ImageView imageView;
String songsCategory;
private static final  int PICK_IAMGE_REQUESt=234;

private Uri fileFath;
StorageReference storageReference;
DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_album2);

        buttonchoose=findViewById(R.id.buttonchoose);
        buttonUpload=findViewById(R.id.buttonUpload);
        edittextName=findViewById(R.id.edit_text);
        imageView=findViewById(R.id.imageView);

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase= FirebaseDatabase.getInstance().getReference(Constants.DATA_PATH_UPLOADS);
        Spinner spinner=findViewById(R.id.spinner);

        buttonchoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);

        List<String > catrgories=new ArrayList<>();
        catrgories.add("Love Songs");
        catrgories.add("sad Songs");
        catrgories.add("party Songs");
        catrgories.add("birthday Songs");
        catrgories.add("f Songs");

        ArrayAdapter<String> dataAdapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,catrgories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                songsCategory=adapterView.getItemAtPosition(i).toString();
                Toast.makeText(UploadAlbumActivity2.this, "Seleted "+songsCategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    @Override
    public void onClick(View v) {
        if(v==buttonchoose){
            showFileChoose();
        }
        else if(v==buttonUpload){
            uploadFile();
        }
    }

    private void uploadFile() {

        if(fileFath!=null){
            ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("uploading......");
            progressDialog.show();
            final StorageReference sref=storageReference.child(Constants.STORAGE_PATH_UPLOADS
            +System.currentTimeMillis()+"."+getFileExtension(fileFath));
            sref.putFile(fileFath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url=uri.toString();
                             Upload upload=new Upload(edittextName.getText().toString().trim(),url,songsCategory);
                          String uploadId=mDatabase.push().getKey();
                          mDatabase.child(uploadId).setValue(upload);
                          progressDialog.dismiss();
                            Toast.makeText(UploadAlbumActivity2.this, "File uploaded", Toast.LENGTH_SHORT).show();


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadAlbumActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("uplodad "+(int)progress+"%....");
                }
            });
        }
    }

    private void showFileChoose() {
        Intent intent=new Intent();
        intent.setType("image");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select picture"),PICK_IAMGE_REQUESt);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IAMGE_REQUESt && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            fileFath=data.getData();
            Bitmap bitmap=null;
            try {
                 bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),fileFath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();


            }


        }
    }

    public  String getFileExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getMimeTypeFromExtension(cr.getType(uri));
    }
}