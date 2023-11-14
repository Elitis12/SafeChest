package com.example.safechest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class page_login extends AppCompatActivity {

    EditText login, mdp;
    TextView MdpOublie;
    Button connexion;
    bddManager bddManager;
    ImageView imageLock;
    Handler setDelay;
    Runnable startDelay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_login);

        setDelay = new Handler();
        login= (EditText)findViewById(R.id.editTextLogin);
        mdp = (EditText)findViewById(R.id.editTextTextPassword);
        imageLock = (ImageView)findViewById(R.id.imageIcon);
        bddManager= (bddManager) new bddManager(this);
        connexion = (Button) findViewById(R.id.buttonConnexion);
        MdpOublie = (TextView) findViewById(R.id.textViewMotDePasseOublie);

        // L'ensemble des actions lorsque l'on clique sur le bouton "Connexion"
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginTXT = login.getText().toString();
                String mdpTXT = mdp.getText().toString();

                if (TextUtils.isEmpty(loginTXT) || TextUtils.isEmpty(mdpTXT)){

                    // On vérifie que l'ensemble des champs soient complétés
                    Animation animation = AnimationUtils.loadAnimation(page_login.this,R.anim.wrong);
                    imageLock.startAnimation(animation);
                    Toast.makeText(page_login.this,"Tout les champs doivent être complétés",Toast.LENGTH_SHORT).show();
                }
                else{

                    // On vérifie que l'utilisateur existe avant de pouvoir le faire rentrer sur la page de ses mots de passe
                    Boolean checkuserpass = bddManager.checkUserNamePassword(loginTXT,mdpTXT);

                    if (checkuserpass==true){
                        imageLock.setImageResource(R.drawable.ic_padlock_open);
                        int test = bddManager.GetidUtilisateur(loginTXT, bddManager.sha256(mdpTXT));
                        String id = String.valueOf(test);
                        startDelay = new Runnable() {
                            @Override
                            public void run() {
                                // On récupère la valeur "ID" de l'utilisateur qui se connecte afin de la transmettre à la page des mots de passe
                                // Cette valeur servira a afficher / ajouter / supprimer les mots de passe
                                Intent intent = new Intent(getApplicationContext(),page_mdp.class);
                                intent.putExtra("id",id);
                                intent.putExtra("login",loginTXT);
                                startActivity(intent);

                            }

                        };
                        setDelay.postDelayed(startDelay,500);








                    }
                    else{
                        Animation animation = AnimationUtils.loadAnimation(page_login.this,R.anim.wrong);
                        imageLock.startAnimation(animation);
                        Toast.makeText(page_login.this,"La connexion à échouée",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        MdpOublie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),page_forgottenmdp.class);
                startActivity(intent);
            }
        });


    }

    // Méthode pour le bouton d'inscription, permettant à l'utilisateur d'accéder à la page d'inscription
    public void LienInscription(View view) {
        Intent versInscription = new Intent(getApplicationContext(), page_inscription.class);
        startActivity(versInscription);
        finish();
    }

    // Créations des méthodes pour la page d'option: Elle servira à Importer / Exporter notre base de donnée.




    @Override
    public void onBackPressed()
    {
        // Pour bloquer le bouton retour arrière , on ne marque aucune commande à effectuer
    }

}