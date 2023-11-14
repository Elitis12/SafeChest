package com.example.safechest;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class page_mdp extends AppCompatActivity {


    Button ajout , deconnexion;
    ImageView burgerMenu;
    bddManager bddManager;
    ListView ListMdp;
    DrawerLayout drawerLayout;
    private EditText nomEditText, mdpEditText;
    List<MotDePasse> mdpList = new ArrayList<>();
    String AES = "AES";
    private int lastInteractionTime = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_mdp);
        DetectInactivite();

        ajout = (Button)findViewById(R.id.buttonAddMdp);
        deconnexion = (Button)findViewById(R.id.buttonDeconnexion);
        drawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        burgerMenu = (ImageView) findViewById(R.id.imageBurgerMenu);
        bddManager = new bddManager(this);
        ListMdp = (ListView) findViewById(R.id.mdpList);



// ===================================== RÉCUPÉRATION SESSION UTILISATEUR ==========================================================================================================

        Intent intent = getIntent();
        String idtransmis =intent.getStringExtra("id"); // Récupération de la valeur "Id" de l'utilisateur.
        String logintransmis = intent.getStringExtra("login");

// ===================================== FIN RÉCUPÉRATION SESSION UTILISATEUR ==========================================================================================================


// ===================================== DÉBUT POP UP AJOUT MDP ==========================================================================================================

        ajout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialisation de l'affichage
                final Dialog dialog = new Dialog(page_mdp.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.popup_addmdp);
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                // Initialisation des variables

                final EditText nameEt = dialog.findViewById(R.id.editTextName);
                final EditText passwordEt = dialog.findViewById(R.id.editTextPassword);
                final EditText passwordConfirmedEt = dialog.findViewById(R.id.editTextPasswordConfirmed);
                final String nom;
                final String password;
                Button addButton = dialog.findViewById(R.id.buttonAdd);
                Button leftButton = dialog.findViewById(R.id.buttonLeft);

                // Affichage du PopUp avec la reqûete d'insertion à la BDD
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nomTXT= nameEt.getText().toString();
                        String mdpTXT= passwordEt.getText().toString();
                        String mdpConfirmedTXT = passwordConfirmedEt.getText().toString();
                        if(TextUtils.isEmpty(nomTXT) || TextUtils.isEmpty(mdpTXT) || TextUtils.isEmpty(mdpConfirmedTXT)){
                            Toast.makeText(page_mdp.this,"Tout les champs doivent être complétés",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if (mdpTXT.equals(mdpConfirmedTXT)){
                                try {
                                    bddManager.addMdp(nomTXT,bddManager.encrypt(mdpTXT,nomTXT),idtransmis);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mdpList.add(new MotDePasse(nomTXT,mdpTXT,Integer.valueOf(idtransmis)));
                                ListView listMdp = findViewById(R.id.mdpList);
                                listMdp.setAdapter(new MotDePasseAdapter(page_mdp.this,mdpList));
                                Toast.makeText(page_mdp.this,"Votre mot de passe est enregistré",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                            else{
                                Toast.makeText(page_mdp.this,"Les mots de passe doivent être les mêmes.",Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
                leftButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout((6 * width)/7, (4 * height)/5);
            }
        });
// ===================================== FIN POP UP AJOUT MDP ==========================================================================================================

     // Méthode afin de se déconnecter de sa session lorsque l'on clique sur le bouton déconnexion
        deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deconnexion = new Intent(getApplicationContext(), page_login.class);
                startActivity(deconnexion);
                finish();
            }
        });

// ===================================== DEBUT INITIALISATION LISTE MDP ==========================================================================================================

        Cursor data = bddManager.getMdpList(idtransmis);
        if(data.getCount() == 0){
            Toast.makeText(page_mdp.this,"Il n'y a aucun mot de passe enregistré pour le moment",Toast.LENGTH_SHORT).show();
        }
        else{
            while (data.moveToNext()){
                mdpList.add(new MotDePasse((data.getString(0)),(data.getString(1)),(data.getInt(2))));
            }
            ListView listMdp = findViewById(R.id.mdpList);
            listMdp.setAdapter(new MotDePasseAdapter(this,mdpList));
        }

// ===================================== FIN INITIALISATION LISTE MDP ==========================================================================================================


// ===================================== DÉBUT BURGER MENU ==========================================================================================================

        burgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.NavigationView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.menuExportBDD:
                    Intent versOption = new Intent(getApplicationContext(),page_options.class);
                    startActivity(versOption);
                    break;
                case R.id.menuMdpMaitre:
                    Intent versModifMdpMaitre = new Intent(getApplicationContext(),page_modifmdpmaitre.class);
                    versModifMdpMaitre.putExtra("id",idtransmis);
                    versModifMdpMaitre.putExtra("login",logintransmis);
                    startActivity(versModifMdpMaitre);
                    finish();

                    Random r = new Random();
                case R.id.menuMdpGenerator:
                    final Dialog dialog = new Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.popup_generate_password);
                    dialog.show();

                    Button generate = (Button) dialog.findViewById(R.id.buttonGenerate);
                    EditText lenght = (EditText) dialog.findViewById(R.id.passwordlenght);
                    TextView generatedPassword = (TextView) dialog.findViewById(R.id.GeneratedPassword);

                    generate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Random random = new Random();
                            int lenghtpassword = Integer.valueOf(lenght.getText().toString());
                            String generatedpassword = "";
                            int i;
                            if (TextUtils.isEmpty(lenght.getText().toString())){
                                Toast.makeText(page_mdp.this,"Veuillez rentrer une taille de mot de passe", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                for(i=0 ; i < lenghtpassword ; i++){
                                    int choix = random.nextInt(4);
                                    switch (choix){
                                        case 0:
                                            String Listlettreminuscule = "abcdefghijklmnopqrstuvwxyz";
                                            String minuscule = String.valueOf(Listlettreminuscule.charAt(random.nextInt(Listlettreminuscule.length())));
                                            generatedpassword += minuscule;

                                            break;
                                        case 1:
                                            String Listlettremajuscule = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                                            String majuscule = String.valueOf(Listlettremajuscule.charAt(random.nextInt(Listlettremajuscule.length())));
                                            generatedpassword += majuscule;

                                            break;
                                        case 2:
                                            int chiffre = random.nextInt(10);
                                            String chiffreString = String.valueOf(chiffre);
                                            generatedpassword += chiffreString;

                                            break;
                                        case 3:
                                            String Listcaractere = "$?#@&!?";
                                            String caractere = String.valueOf(Listcaractere.charAt(random.nextInt(Listcaractere.length())));
                                            generatedpassword += caractere;

                                            break;
                                    }
                                }
                                generatedPassword.setText(generatedpassword);
                                ClipboardManager clipboard = (ClipboardManager) page_mdp.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = null;
                                clip = ClipData.newPlainText("Mot de passe",generatedPassword.getText().toString());
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(page_mdp.this,"Le mot de passe a été copié !",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
            }
            return false;
        } );

// ===================================== DÉBUT MÉTHODE DE VERROUILLAGE AFK ==========================================================================================================


    }
    @Override
    public void onBackPressed()
    {
        // Pour bloquer le bouton retour arrière , on ne marque aucune commande à effectuer
    }

// ===================================== DÉBUT MÉTHODE DE VERROUILLAGE AFK ==========================================================================================================


    public void DetectInactivite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {

                        // Toute les 15 secondes, le compteur d'inactivité se rajoute 15 seconde. Tant qu'aucune activité n'est réalisé il continue de monter
                        Thread.sleep(15000); // Vérifie toute les 15 secondes si l'utilisateur est inactif
                        setLastInteractionTime(15000); // Ajoute les 15 secondes à la variable

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(getLastInteractionTime()> 120000) // -- 120 000ms = 2 minutes.
                    {
                        // Si l'utilisateur n'a effectué aucune intéraction avec l'application au bout de 2min, l'application déconnecte l'utilisateur
                        Intent versLogin= new Intent(getApplicationContext(),page_login.class);
                        startActivity(versLogin);
                        finish();
                        break;

                    }
                }
            }
        }).start();
    }

    public long getLastInteractionTime() {
        return this.lastInteractionTime;
    }

    public void setLastInteractionTime(int lastInteractionTime) {
        this.lastInteractionTime = this.lastInteractionTime+lastInteractionTime;
    }
    public void setLastInteractionTimeToZero() {
        this.lastInteractionTime = 0;
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        setLastInteractionTimeToZero();
    }

// ===================================== FIN MÉTHODE DE VERROUILLAGE AFK ==========================================================================================================


}




