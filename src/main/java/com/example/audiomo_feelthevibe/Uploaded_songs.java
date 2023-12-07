package com.example.audiomo_feelthevibe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.audiomo_feelthevibe.Model.UploadSong;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class Uploaded_songs extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView textViewImage;
    ProgressBar progressBar;
    Uri audioUri;
    StorageReference mStorage;
    StorageTask mUploadTask;
    DatabaseReference referenceSongs;
    String songsCategory;
    MediaMetadataRetriever metadataRetriever;
    byte [] art;
    String title1,artist1,album_art1="",duration1;
    TextView title,artist,duration,album,dataa;
    ImageView album_art;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_songs);

        textViewImage=findViewById(R.id.textViewSongsFileSelected);
        progressBar=findViewById(R.id.progressBar);
        title=findViewById(R.id.Title);
        artist=findViewById(R.id.artist);
        duration=findViewById(R.id.duration);
        album=findViewById(R.id.album);
        dataa=findViewById(R.id.dataa);
        album_art=findViewById(R.id.imageView);

        metadataRetriever=new MediaMetadataRetriever();
        referenceSongs= FirebaseDatabase.getInstance().getReference().child("songs");
        mStorage= FirebaseStorage.getInstance().getReference().child("songs");


        Spinner spinner=findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);


        List <String > catrgories=new ArrayList<>();
        catrgories.add("Love Songs");
        catrgories.add("sad Songs");
        catrgories.add("party Songs");
        catrgories.add("birthday Songs");
        catrgories.add("f Songs");

        ArrayAdapter<String> dataAdapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,catrgories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);



    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
        songsCategory=adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, "Selected : "+songsCategory, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void openAudioFiles(View v){
        Intent i=new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        startActivityForResult(i,101);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==101 && resultCode==RESULT_OK && data.getData() !=null){
            audioUri=data.getData();


            String fileName=getFileName(audioUri);
            textViewImage.setText(fileName);

            metadataRetriever.setDataSource(this,audioUri);

            art=metadataRetriever.getEmbeddedPicture();
            Bitmap bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
            album_art.setImageBitmap(bitmap);
            album.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            artist.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            dataa.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            duration.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            title.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));


            artist1=metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            title1=metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            duration1=metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);





        }

    }


    @SuppressLint("Range")
    private String getFileName(Uri uri)
    {
        String result=null;
        if(uri.getScheme().equals("content")){
            Cursor cursor=getContentResolver().query(uri,null,null,null,null);
            try {
                if(cursor!=null && cursor.moveToFirst())
                {
                    result=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }


        }
        if(result==null){result=uri.getPath();
        int cut=result.lastIndexOf('/');

        if(cut!=-1){
            result=result.substring(cut +1);
        }
        }
        return result;
    }
    public void uploadFileTofirebase(View v){
        if (textViewImage.equals("no file selected")){
            Toast.makeText(this, "please selected a image", Toast.LENGTH_SHORT).show();

        }
        else{
            if(mUploadTask!=null && mUploadTask.isInProgress()){
                Toast.makeText(this, "songs uploads in progress", Toast.LENGTH_SHORT).show();
            }
            else {
                uploadFile();
            }
        }
    }

    private void uploadFile() {
        if(audioUri!=null){
            Toast.makeText(this, "upload please wait", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference storageReference=mStorage.child(System.currentTimeMillis() +"."+getfileextension(audioUri));
            mUploadTask =storageReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UploadSong uploadSong=new UploadSong(songsCategory,title1,artist1,album_art1,duration1, audioUri.toString());
                            String uploadId=referenceSongs.push().getKey();
                            referenceSongs.child(uploadId).setValue(uploadSong);
                        }

                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                 double progress=(100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressBar.setProgress((int)progress);

                }
            });
        }
        else {
            Toast.makeText(this, "no file selected to upload", Toast.LENGTH_SHORT).show();
        }



    }
    private String getfileextension(Uri audioUri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));


        //hre i change return and derie variable

    }

    private void openAlbumuploadsActivity(View view){
        Intent in=new Intent(Uploaded_songs.this,UploadAlbumActivity2.class);
        startActivity(in);
    }

}