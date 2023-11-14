package com.example.safechest;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class page_forgottenmdp extends AppCompatActivity {

    EditText number , login;
    Button modif , retour;
    bddManager bddManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_forgottenmdp);

        login = (EditText) findViewById(R.id.editTextLoginRecup);
        modif = (Button) findViewById(R.id.buttonModifMdpMaitre);
        retour = (Button) findViewById(R.id.buttonModifMdpRetour);
        bddManager = new bddManager(this);


        modif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(login.getText())) {
                    // On vérifie que l'ensemble des champs soient complétés
                    Toast.makeText(page_forgottenmdp.this, "Tout les champs doivent être complétés", Toast.LENGTH_SHORT).show();
                } else {
                    // On vérifie que le login enregistré dans la BDD correspond bien avec le login inscrit
                    Boolean checkuser = bddManager.checkLogin(login.getText().toString());
                    if (checkuser == true) {
                        Toast.makeText(page_forgottenmdp.this,"Login correspondant trouvé !",Toast.LENGTH_SHORT).show();
                        String id = bddManager.GetIDByLogin(login.getText().toString());
                        Intent intentversAuthentification = new Intent(getApplicationContext(),page_verifauthentification.class);
                        intentversAuthentification.putExtra("id",id);
                        intentversAuthentification.putExtra("login",login.getText().toString());
                        startActivity(intentversAuthentification);
                    }
                    else{
                        Toast.makeText(page_forgottenmdp.this,"Ca marche po",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });




        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent versLogin= new Intent(getApplicationContext(),page_login.class);
                startActivity(versLogin);
                finish();
            }
        });

    }
    @Override
    public void onBackPressed()
    {
        // Pour bloquer le bouton retour arrière , on ne marque aucune commande à effectuer
    }
}