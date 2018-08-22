package com.hayk.hp.internalexternalwriter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 1001;
    private EditText fileName;
    private EditText fileContent;
    private String path;
    private String stringData = "";
    private RadioButton radioInternal;
    private RadioButton radioExternal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button buttonWrite = findViewById(R.id.button_write_id);
        final Button buttonRead = findViewById(R.id.button_read_id);
        fileName = findViewById(R.id.edit_file_name_id);
        fileContent = findViewById(R.id.edit_file_content_id);
        radioInternal = findViewById(R.id.internal_radio_id);
        radioExternal = findViewById(R.id.external_radio_id);
        requestPermissions();
        writeOnClick(buttonWrite);
        readeOnClick(buttonRead);
    }

    private void writeOnClick(Button bWrite) {
        bWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    writeFile();
                } catch (IOException e) {
                    Log.d("Write","WriteMessage");
                }
            }
        });
    }

    private void readeOnClick(Button bRead) {
        bRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    readFile();
                } catch (IOException e) {
                    Log.d("Read","ReadMessage");
                }
            }
        });
    }

    private void getPathByStorage() {
        String rootExternal = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        final String rootInternal = getFilesDir().getAbsolutePath();
        if (radioExternal.isChecked()) {
            path = String.format("%s/%s", rootExternal, fileName.getText().toString());
        } else if (radioInternal.isChecked()) {
            path = String.format("%s/%s", rootInternal, fileName.getText().toString());
        }
    }

    private void writeFile() throws IOException {
        getPathByStorage();
        File file;
        if (path != null && !fileName.getText().toString().isEmpty()) {
            file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(fileContent.getText().toString().getBytes());
            fos.close();
            Toast.makeText(this, R.string.create_file, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.not_fileName_or_path, Toast.LENGTH_SHORT).show();
        }
    }

    private void readFile() throws IOException {
        getPathByStorage();
        File fileR;
        if (!fileName.getText().toString().isEmpty() && path != null) {
            fileR = new File(path);
            if (fileR.isFile()) {
                FileInputStream fis = new FileInputStream(fileR);
                DataInputStream dis = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(dis));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    stringData = strLine;
                }
                fileContent.setText(stringData);
                dis.close();
                br.close();
            } else {
                Toast.makeText(this, R.string.no_such_file, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.no_such_file, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                }
        }
    }
}
