package com.example.safechest;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class page_modifmdpmaitre extends AppCompatActivity {

    TextView login;
    EditText password , passwordconfirmed;
    Button modif , retour;
    bddManager bddManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_modifmdpmaitre);

        login = (TextView) findViewById(R.id.textViewLoginMdpMaitre);
        password = (EditText) findViewById(R.id.editTextPasswordMaster);
        passwordconfirmed = (EditText) findViewById(R.id.editTextPasswordMasterConfirmed);
        modif = (Button) findViewById(R.id.buttonModifMdpMaitre);
        retour = (Button) findViewById(R.id.buttonModifMdpRetour);
        bddManager = new bddManager(this);


        Intent intent = getIntent();
        String nomtransmis =intent.getStringExtra("login"); // Récupération de la valeur "Login" de l'utilisateur.
        String idtransmis = intent.getStringExtra("id");

        login.setText(nomtransmis);

        modif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(login.getText()) || TextUtils.isEmpty(password.getText()) || TextUtils.isEmpty(passwordconfirmed.getText())) {
                    Toast.makeText(page_modifmdpmaitre.this, "Tout les champs doivent être complétés", Toast.LENGTH_SHORT).show();
                } else {
                    if ((password.getText().toString()).equals(passwordconfirmed.getText().toString())) {
                        bddManager.updateMdpMaitre((password.getText().toString()),idtransmis);
                        Toast.makeText(page_modifmdpmaitre.this,"Votre mot de passe a bien été modifié",Toast.LENGTH_SHORT).show();
                        Intent versLogin= new Intent(getApplicationContext(),page_login.class);
                        startActivity(versLogin);
                        finish();
                    }
                    else{
                        Toast.makeText(page_modifmdpmaitre.this,"Les mots de passe doivent correspondre",Toast.LENGTH_SHORT).show();
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