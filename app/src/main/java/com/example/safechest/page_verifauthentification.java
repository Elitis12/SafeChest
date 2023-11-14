package com.example.safechest;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class page_verifauthentification extends AppCompatActivity {

    EditText repQuestion, numero;
    Button authentificationQuestion , authentificationNumero , retour;
    bddManager bddManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_verifauthentification);

        Spinner spinner = findViewById(R.id.spinnerQuestion);
        bddManager = new bddManager(this);
        repQuestion = (EditText) findViewById(R.id.editTextRepQuestion);
        numero = (EditText) findViewById(R.id.editTextNumber);
        authentificationNumero = (Button) findViewById(R.id.buttonAuthentificationNumero);
        authentificationQuestion = (Button) findViewById(R.id.buttonAuthentificationQuestion);
        retour = (Button) findViewById(R.id.buttonAuthentificationRetour);


        // Récupération du login
        Intent intent = getIntent();
        String idtransmis = intent.getStringExtra("id"); // Récupération de la valeur "id" de l'utilisateur.
        String logintransmis =intent.getStringExtra("login"); // Récupération de la valeur "Login" de l'utilisateur.

        // Création de la liste des questions
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.question, R.layout.spinner_color);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Authentification par question
        authentificationQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(repQuestion.getText())){
                    Toast.makeText(page_verifauthentification.this, "Vous devez répondre à la question", Toast.LENGTH_SHORT).show();
                }
                else{
                    Boolean checkAuthentification = bddManager.checkByQuestion(logintransmis,repQuestion.getText().toString());
                    if (checkAuthentification == true){
                        Toast.makeText(page_verifauthentification.this,"Vous avez été authentifié",Toast.LENGTH_SHORT).show();
                        Intent versModifMdpMaitre = new Intent(getApplicationContext(),page_modifmdpmaitre.class);
                        versModifMdpMaitre.putExtra("id",idtransmis);
                        versModifMdpMaitre.putExtra("login",logintransmis);
                        startActivity(versModifMdpMaitre);
                        finish();
                    }
                    else {
                        Toast.makeText(page_verifauthentification.this,"Authentification incorrecte",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Authentification par numéro
        authentificationNumero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(numero.getText())){
                    Toast.makeText(page_verifauthentification.this, "Vous devez inscrire votre numéro", Toast.LENGTH_SHORT).show();
                }
                else{
                    Boolean checkAuthentification = bddManager.checkByNumber(logintransmis,numero.getText().toString());
                    if (checkAuthentification == true){
                        Toast.makeText(page_verifauthentification.this,"Vous avez été authentifié",Toast.LENGTH_SHORT).show();
                        Intent versModifMdpMaitre = new Intent(getApplicationContext(),page_modifmdpmaitre.class);
                        versModifMdpMaitre.putExtra("id",idtransmis);
                        versModifMdpMaitre.putExtra("login",logintransmis);
                        startActivity(versModifMdpMaitre);
                        finish();
                    }
                    else {
                        Toast.makeText(page_verifauthentification.this,"Authentification incorrecte",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent versForgottenMdp = new Intent(getApplicationContext(),page_forgottenmdp.class);
                startActivity(versForgottenMdp);
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