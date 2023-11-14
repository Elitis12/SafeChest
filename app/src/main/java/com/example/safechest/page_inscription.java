package com.example.safechest;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class page_inscription extends AppCompatActivity {

    EditText login, motdepasse , motdepasse2, numerorecup;
    Button ajout;
    bddManager bddManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_inscription);

        login=(EditText) findViewById(R.id.editTextLogin);
        motdepasse=(EditText) findViewById(R.id.LayoutPassword);
        motdepasse2= (EditText) findViewById(R.id.editTextTextPasswordConfirmed);
        ajout= (Button) findViewById(R.id.buttonContinuez);

        bddManager = new bddManager(this);
        ajout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginTXT= login.getText().toString();
                String mdpTXT = motdepasse.getText().toString();
                String mdp2TXT = motdepasse2.getText().toString();

                if(TextUtils.isEmpty(loginTXT) || TextUtils.isEmpty(mdpTXT) || TextUtils.isEmpty(mdp2TXT)){
                    Toast.makeText(page_inscription.this,"Tout les champs doivent être complétés",Toast.LENGTH_SHORT).show();
                }
                else{
                    if (mdpTXT.equals(mdp2TXT)){
                        Boolean checkuser = bddManager.checkLogin(loginTXT);
                        if(checkuser==false){
                                Intent versSecurite= new Intent(getApplicationContext(),page_inscription_securite.class);
                                versSecurite.putExtra("login",loginTXT);
                                versSecurite.putExtra("mdp",mdpTXT);
                                startActivity(versSecurite);
                                finish();
                        }
                        else{
                            Toast.makeText(page_inscription.this,"Ce compte existe déjà",Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        Toast.makeText(page_inscription.this,"Les mots de passe entrées ne sont pas les mêmes",Toast.LENGTH_SHORT).show();
                    }
                }


                }
            });
    }
    @Override
    public void onBackPressed()
    {
        // Pour bloquer le bouton retour arrière , on ne marque aucune commande à effectuer
    }

    public void LienRetour (View view){
        Intent versLogin= new Intent(getApplicationContext(),page_login.class);
        startActivity(versLogin);
        finish();
    }
}
