package com.example.safechest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Debug;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast; // Pour les messages d'erreurs.

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static java.lang.System.*;
import static java.lang.System.out;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class bddManager extends SQLiteOpenHelper {

    private static final String nomBDD = "safechestbdd.db";
    public bddManager(Context context){

        super(context,"safechestbdd.db" ,null ,1);

    }

// ===================================== DÉBUT CRÉATION DE LA TABLE ==========================================================================================================


    @Override
    public void onCreate(SQLiteDatabase db) {

        String query1= "CREATE TABLE IF NOT EXISTS utilisateur ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, login TEXT NOT NULL, mdpmaitre TEXT NOT NULL, repQuestion TEXT NOT NULL , numeroRecup TEXT NOT NULL );";
        String query2="CREATE TABLE IF NOT EXISTS motdepasse ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , nom TEXT NOT NULL , motdepasse TEXT NOT NULL , idutilisateur INTEGER NOT NULL , FOREIGN KEY (idutilisateur) REFERENCES utilisateur (id));";

        db.execSQL(query1);
        db.execSQL(query2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS utilisateur");
        db.execSQL("DROP TABLE IF EXISTS motdepasse");
        db.execSQL("DROP TABLE IF EXISTS securite");
        onCreate(db);
    }
// ===================================== FIN CRÉATION DE LA BDD ==========================================================================================================



// ===================================== DÉBUT MÉTHODES HASH DES MOTS DE PASSE ==========================================================================================================

    // Méthode pour hash le mot de passe principal
    public static final String sha256(final String s) {
        try {
            // ===== Création du hash en SHA-256 =====*

            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256"); // On fait appel à la méthode de Hash.
            digest.update(s.getBytes()); // On crée une variable qui récupère le contenu qu'on lui donne (String).
            byte messageDigest[] = digest.digest(); // Cette variable est transformé en une séquence d'octet et mis dans un tableau d'octet.

            // ====== Création du mot de passe qui sera rentrée ,transformé avec la méthode de Hash =====

            StringBuilder hexString = new StringBuilder(); // Variable qui sera utilisé pour restocké le mot de passe hash, en un String, dans la BDD.
            for (byte aMessageDigest : messageDigest) { // Pour chaque Octet, trouvé dans le tableau ...
                String h = Integer.toHexString(0xFF & aMessageDigest); // ... Cet octet est transformé en un String aléatoire,
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h); // ,On rajoute l'octet transformé en String, dans le mot de passe qui sera renvoyé
            }
            return hexString.toString(); // On renvoie le mot de passe.
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ""; // Si la fonction n'a pas marché, le mot de passe renvoyé est vide.
    }
    String AES = "AES";
    public String encrypt(String Password , String Key ) throws Exception {
        SecretKeySpec keypassword = generateKey(Key);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, keypassword);
        byte [] encVal = c.doFinal(Password.getBytes());
        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        return encryptedValue;
    }

    public SecretKeySpec generateKey (String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte [] bytes = password.getBytes("UTF-8");
        digest.update(bytes , 0 , bytes.length);
        byte [] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    public String decrypt (String Password , String Key) throws Exception {
        SecretKeySpec keypass = generateKey(Key);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,keypass);
        byte [] decodedValue = Base64.decode(Password, Base64.DEFAULT);
        byte [] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return  decryptedValue;

    }


// ===================================== FIN MÉTHODES HASH DES MOTS DE PASSE ==========================================================================================================




// ===================================== DÉBUT MÉTHODES CRUD ==========================================================================================================

    // Cette méthode permet la création d'un nouveau compte dans la BDD
    public Boolean addUtilisateur(String login , String mdpmaitre , String repQuestion , String numeroRecup) {
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String hashMdpMaitre = sha256(mdpmaitre);
        String hashRepQuestion = sha256(repQuestion);
        String hashNumeroRecup = sha256(numeroRecup);

        // On s'assure que le programme crée bien la base de donnée lors d'une première inscription.
        onCreate(db);

        // On enregistre les valeurs
        cv.put("login",login);
        cv.put("mdpmaitre",hashMdpMaitre);
        cv.put("repQuestion",hashRepQuestion);
        cv.put("numeroRecup",hashNumeroRecup);

        long res =db.insert("utilisateur",null,cv);
        if (res==-1){
            return true;
        }
        else {
            return false;
        }
    }


    // Cette méthode permet la création d'un nouveau mot de passe dans la BDD de l'utilisateur
    public Boolean addMdp (String nom , String Mdp , String idUtilisateur){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("nom",nom);
        cv.put("motdepasse",Mdp);
        cv.put("idutilisateur",idUtilisateur);

        long res =db.insert("motdepasse",null,cv);
        if (res==-1){
            return true;
        }
        else {
            return false;
        }

    }

    public  Boolean updateMdp(String nameMdp , String mdp , String idUtilisateur){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nom",nameMdp);
        contentValues.put("motdepasse",mdp);
        long result = db.update("motdepasse",contentValues,"id=?", new String[]{idUtilisateur});
        if (result==-1){
            return false;
        }
        else{
            return true;
        }
    }

    public  Boolean updateMdpMaitre(String mdp , String idUtilisateur){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String Hashmdp = sha256(mdp);
        contentValues.put("mdpmaitre",Hashmdp);
        long result = db.update("utilisateur",contentValues,"id=?", new String[]{idUtilisateur});
        if (result==-1){
            return false;
        }
        else{
            return true;
        }
    }

    public Boolean deleteMdp(String idutilisateur){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long result = db.delete("motdepasse","id=?",new String []{idutilisateur});

        if (result ==-1){
            return false;
        }
        else{
            return true;
        }
    }

// ===================================== FIN MÉTHODES CRUD ==========================================================================================================

// ===================================== DÉBUT REQUÊTE SELECT ==========================================================================================================

    // Cette méthode permet de récupérer l'ID de l'utilisateur
    // Elle servira à stocker dans une variable, l'ID de l'utilisateur connecté pour Ajouter/Supprimer/Afficher ses mots de passe dans sa page de mot de passe
    public int GetidUtilisateur (String login, String mdp){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM utilisateur WHERE login=? AND mdpmaitre=?",new String[] {login,mdp});
        cursor.moveToFirst();
        int ID = cursor.getInt(0);
        return ID;

    }

    public String GetIDByLogin (String Login){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM utilisateur WHERE login=?",new String[] {Login});
        cursor.moveToFirst();
        String id = cursor.getString(0);
        return id;

    }

    public String GetLoginByNumero (String numeroRecup){
        SQLiteDatabase db = this.getWritableDatabase();
        String HashNumero = sha256(numeroRecup);
        Cursor cursor = db.rawQuery("SELECT login FROM utilisateur WHERE numeroRecup=?",new String[] {HashNumero});
        cursor.moveToFirst();
        String login = cursor.getString(0);
        return login;

    }

    public String GetIdByNumero (String numeroRecup){
        SQLiteDatabase db = this.getWritableDatabase();
        String HashNumero = sha256(numeroRecup);
        Cursor cursor = db.rawQuery("SELECT id FROM utilisateur WHERE numeroRecup=?",new String[] {HashNumero});
        cursor.moveToFirst();
        String id = cursor.getString(0);
        return id;

    }

    // Cette méthode permet de vérifier, lors d'une création d'un compte, si l'utilisateur n'existe pas déjà dans la BDD.
    public Boolean checkLogin(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM utilisateur WHERE login=?", new String[] {username});
        if (cursor.getCount()>0){
            return true;
        }
        else{
            return false;
        }
    }
    // Cette méthode permet de s'authentifier, lors du login, et de vérifier si un compte existe bien avec les identifiants donnés.
    public Boolean checkUserNamePassword (String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        String HashPassword = sha256(password);
        Cursor cursor = db.rawQuery("SELECT * FROM utilisateur WHERE login=? AND mdpmaitre=?", new String[] {username,HashPassword});
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            return true;
        }
        else{
            return false;
        }
    }

    public Boolean checkByQuestion (String login, String repQuestion){
        SQLiteDatabase db = this.getWritableDatabase();
        String HashQuestion = sha256(repQuestion);
        Cursor cursor = db.rawQuery("SELECT * FROM utilisateur WHERE login=? AND repQuestion=?", new String[] {login,HashQuestion});
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            return true;
        }
        else{
            return false;
        }
    }

    public Boolean checkByNumber (String Login,String numberRecup){
        SQLiteDatabase db = this.getWritableDatabase();
        String HashNumero = sha256(numberRecup);
        Cursor cursor = db.rawQuery("SELECT * FROM utilisateur WHERE numeroRecup=? AND login=?", new String[] {HashNumero,Login});
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            return true;
        }
        else{
            return false;
        }
    }


    // Cette méthode permet l'affichage de l'ensemble des mots de passe de l'utilisateur.
    public Cursor getMdpList(String idutilisateur){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT nom,motdepasse,id FROM motdepasse WHERE idutilisateur=?", new String[] {idutilisateur});
        return data;
    }

// ===================================== FIN REQUÊTE SELECT ==========================================================================================================


// ===================================== DÉBUT IMPORT / EXPORT DATABASE ==========================================================================================================

    public void exportDatabase(String backupName){
        try
        {
            File sd = Environment.getExternalStorageDirectory(); // Accès à la Carte SD
            File data = Environment.getDataDirectory(); // Accès au dossier de la BDD du Tel

            String currentDBPath = "data/com.example.safechest/databases/safechestbdd.db";
            String backupDBPath = backupName+".db";

            File currentDB = new File(data, currentDBPath); // Chemin complet de la BDD à exporter
            File backupDB = new File(sd, backupDBPath); // Chemin complet de l'endroit où exporter

            FileChannel src = new FileInputStream(currentDB).getChannel(); // On prends le fichier dans le dossier de l'application
            FileChannel dst = new FileOutputStream(backupDB).getChannel(); // On l'envoie sur la carte SD

            dst.transferFrom(src, 0, src.size()); // Il transfère l'intégralité de la BDD

            src.close();
            dst.close();
        }
        catch (Exception e) {
            Log.d("Main", e.toString());
        }
    }

    public void importDatabase(String backupName){
        try
        {
            File sd = Environment.getExternalStorageDirectory(); // Accès à la carte SD
            File data = Environment.getDataDirectory(); // Accès à la BDD

            String currentDBPath = "data/com.example.safechest/databases/safechestbdd.db";
            String backupDBPath = backupName;

            File backupDB = new File(data, currentDBPath); // Chemin de l'endroit où l'on veux l'emporter
            File currentDB = new File(sd, backupDBPath); // Chemin de la BDD que l'on veux importer

            FileChannel src = new FileInputStream(currentDB).getChannel(); // On importe la BDD de la carte SD
            FileChannel dst = new FileOutputStream(backupDB).getChannel(); // On importe la BDD dans le dossier de l'application

            dst.transferFrom(src, 0, src.size()); // Il transfère l'intégralité de la BDD

            src.close();
            dst.close();
        }
        catch (Exception e) {
            Log.d("Main", "Fichier à importé non-trouvé : "+e);
        }
    }

}

// ===================================== FIN IMPORT / EXPORT DATABASE ==========================================================================================================
