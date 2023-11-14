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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class page_inscription_securite extends AppCompatActivity {

    EditText repQuestion , numero;
    Button inscription;
    bddManager bddManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_inscription_securite);

        // Récupération des valeurs entrées précédemment.

        Intent intent = getIntent();
        String loginTransmis =intent.getStringExtra("login"); // Récupération de la valeur "Login" de l'utilisateur.
        String mdpTransmis = intent.getStringExtra("mdp"); // Récupération de la valeur "Mdp" de l'utilisateur.

        Spinner spinner = findViewById(R.id.spinnerQuestion);
        repQuestion = (EditText) findViewById(R.id.editTextRepQuestion);
        numero = (EditText) findViewById(R.id.editTextNumber);
        inscription = (Button) findViewById(R.id.buttonInscription);
        bddManager = new bddManager(this);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.question, R.layout.spinner_color);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(repQuestion.getText()) || TextUtils.isEmpty(numero.getText())){
                    Toast.makeText(page_inscription_securite.this,"Les deux méthodes d'authentification doivent être complétées.",Toast.LENGTH_SHORT).show();
                }
                else{
                    bddManager.addUtilisateur(loginTransmis,mdpTransmis,repQuestion.getText().toString(),numero.getText().toString());
                    Toast.makeText(page_inscription_securite.this,"Votre compte a bien été crée",Toast.LENGTH_SHORT).show();
                    Intent versLogin = new Intent(getApplicationContext(),page_login.class);
                    startActivity(versLogin);
                    finish();
                }
            }
        });


        };

    }
