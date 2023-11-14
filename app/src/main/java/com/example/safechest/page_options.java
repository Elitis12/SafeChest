package com.example.safechest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class page_options extends AppCompatActivity {


    Button exportButton , importButton;
    bddManager BDDManager;
    ListView listBackup;
    EditText backupExportName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_options);


        backupExportName = (EditText)findViewById(R.id.editTextBackupName);
        exportButton = (Button)findViewById(R.id.buttonExport);
//        importButton = (Button)findViewById(R.id.buttonImport);

        BDDManager = new bddManager(this);

// ===================================== RÉCUPÉRATION SESSION UTILISATEUR ==========================================================================================================

        Intent intent = getIntent();
        String idtransmis =intent.getStringExtra("id"); // Récupération de la valeur "Id" de l'utilisateur.

// ===================================== FIN RÉCUPÉRATION SESSION UTILISATEUR ==========================================================================================================

// ===================================== CRÉATION LISTE DES SAUVEGARDES ==========================================================================================================

        List<File> backupList = new ArrayList<>();
        listBackup =(ListView)findViewById(R.id.ListBackup);
        File [] sd = Environment.getExternalStorageDirectory().listFiles();
        for (File itemFile : sd) {
            if ((itemFile.getName()).endsWith(".db")){

                backupList.add(itemFile);
            }
        }
        listBackup.setAdapter(new Backupadapter(this,backupList));

// ===================================== FIN CRÉATION LISTE DES SAUVEGARDES ==========================================================================================================



// ===================================== DÉBUT EXPORTATION ==========================================================================================================

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(page_options.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(page_options.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},0);
                }
                else{
                    if(TextUtils.isEmpty(backupExportName.getText())){
                        Toast.makeText(page_options.this, "Le nom de la sauvegarde doit être renseigné", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        BDDManager.exportDatabase(backupExportName.getText().toString());
                        File sd = Environment.getExternalStorageDirectory(); // Accès à la carte SD
                        String backupDBPath = backupExportName.getText().toString();
                        File currentDB = new File(sd, backupDBPath);

                        backupList.add(currentDB);
                        listBackup.setAdapter(new Backupadapter(page_options.this,backupList));

                        Toast.makeText(page_options.this, "Votre sauvegarde est enregistrée dans : "+Environment.getExternalStorageState(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        }
        }
// ===================================== FIN EXPORTATION ==========================================================================================================

