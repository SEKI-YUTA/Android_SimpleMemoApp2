package com.example.simplememoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private final int SAVE_REQUEST_CODE = 101;
    private final int PERMISSION_REQUEST = 1001;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private EditText edit_memo;
    private Button btn_save, btn_read;
    private TextView tv_rowCount, tv_charCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_memo = findViewById(R.id.edit_memo);
        btn_save = findViewById(R.id.btn_save);
        btn_read = findViewById(R.id.btn_read);
        tv_rowCount = findViewById(R.id.tv_rowCount);
        tv_charCount = findViewById(R.id.tv_charCount);

//        edit_memo.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                EditText editText = (EditText) view;
//                Log.d("MyLog", String.valueOf(editText.getText().toString().length()));
//                return true;
//            }
//        });

       edit_memo.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               String inputString = charSequence.toString();
               int returnCount = inputString.split("\n").length;
               tv_charCount.setText(String.valueOf(inputString.length()) + "文字");
               tv_rowCount.setText(String.valueOf(returnCount) + "行");
                Log.d("MyLog", charSequence.toString());
                Log.d("MyLog", String.valueOf(i));
                Log.d("MyLog", String.valueOf(i1));
                Log.d("MyLog", String.valueOf(i2));
           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });


       if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
       != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
       } else {
           appSetUP();
       }
    }


    private void appSetUP() {
        Log.d("MyLog", "appSetUP");
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date now = new Date();
                String filename = dateFormat.format(now) + ".txt";
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_TITLE, filename);
                startActivityForResult(intent, SAVE_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isWriteStorageGranted = false;
        if(requestCode == PERMISSION_REQUEST && grantResults.length > 0) {
           for(String item: permissions) {
               if(item.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                   // 許可された場合
                   isWriteStorageGranted = true;
               } else {
//                   許可されなかった場合
                   Toast.makeText(this, "write storage permission is required", Toast.LENGTH_SHORT).show();
               }
           }
        } else {
            Toast.makeText(MainActivity.this, "permission denied", Toast.LENGTH_SHORT).show();
        }

        if(isWriteStorageGranted) {
            appSetUP();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SAVE_REQUEST_CODE && resultCode == RESULT_OK) {
            if(data != null) {
                Uri uri = data.getData();
                byte[] bytes = edit_memo.getText().toString().getBytes(StandardCharsets.UTF_8);
                Log.d("MyLgo", bytes.toString());
                try {
                    File file = new File(uri.getPath());
                    OutputStream os = getContentResolver().openOutputStream(uri);
//                    FileOutputStream fos = new FileOutputStream(os);
//                    fos.write(bytes);
//                    fos.flush();
//                    fos.close();
                    if(os != null) {
                        os.write(bytes);
                        os.flush();
                        os.close();
                        Toast.makeText(this, "保存場所" + uri.getPath(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}