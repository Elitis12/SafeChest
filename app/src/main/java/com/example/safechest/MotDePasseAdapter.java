package com.example.safechest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MotDePasseAdapter extends BaseAdapter {
    private Context context;
    private List<MotDePasse> MotDePasseList;
    private LayoutInflater inflater;
    bddManager bddManager;

    public MotDePasseAdapter(Context context , List<MotDePasse> MotdePasseList){
        this.context=context;
        this.MotDePasseList=MotdePasseList;
        this.inflater = LayoutInflater.from(context);
        bddManager = new bddManager(context);
    }

    @Override
    public int getCount() {
        return MotDePasseList.size();
    }

    @Override
    public MotDePasse getItem(int position) {
        return MotDePasseList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.mdp_item,null);

        MotDePasse currentItem = getItem(position);
        String motdepasseName = currentItem.getNom();
        String motdepasseMdp= (currentItem.getMotdepasse());


        TextView itemNameView = view.findViewById(R.id.backupListname);
        itemNameView.setText(motdepasseName);
        TextView itemMdpView = view.findViewById(R.id.mdpList_mdp);
        itemMdpView.setText(motdepasseMdp); // Affiche un faux mot de passe, pour voir le vrai il faut cliquer sur l'oeil

        ImageButton buttonView = view.findViewById(R.id.imageButtonView);
        ImageButton buttonUpdate = view.findViewById(R.id.imageButtonImport);
        ImageButton buttonDelete = view.findViewById(R.id.imageButtonDelete);



//==================================================== DÉBUT MÉTHODE LONG PRESS ITEM ====================================================================================
        // Méthode pour copier le mot de passe quand on clique sur l'item.
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = null;
                try {
                    clip = ClipData.newPlainText("MotdePasse",bddManager.decrypt(currentItem.getMotdepasse().toString(),currentItem.getNom().toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context,"Le mot de passe de "+currentItem.getNom()+ " a été copié !",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

//==================================================== FIN MÉTHODE LONG PRESS ITEM ====================================================================================

//====================================================  DÉBUT MÉTHODE BOUTON VIEW ====================================================================================


        buttonView.setOnClickListener(new View.OnClickListener() {
            Boolean state = false;
            @Override
            public void onClick(View v) {

                if (state == false) {
                    itemMdpView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    buttonView.setImageResource(R.drawable.ic_hidden);
                    try {
                        itemMdpView.setText(bddManager.decrypt(currentItem.getMotdepasse().toString(),currentItem.getNom().toString())); // Dévoile le véritable mot de passe
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    state = true;
                }
                else{
                    itemMdpView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    buttonView.setImageResource(R.drawable.ic_eye);
                    itemMdpView.setText(motdepasseMdp); // Affiche un faux mot de passe, pour voir le vrai il faut cliquer sur l'oeil
                    state = false;
                }


            }
        });
//==================================================== FIN MÉTHODE BOUTON VIEW ====================================================================================

//==================================================== DÉBUT MÉTHODE BOUTON UPDATE ====================================================================================

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Affichage du PopUp

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.popup_updatemdp);
                dialog.show();

                // Initialisation des variables

                final EditText editTextNameUpdateTXT = dialog.findViewById(R.id.editTextNameUpdate);
                final EditText editTextPasswordUpdateTXT = dialog.findViewById(R.id.editTextPasswordUpdate);
                final EditText editTextPasswordConfirmedUpdateTXT = dialog.findViewById(R.id.editTextPasswordConfirmedUpdate);
                final Button buttonRegisterUpdateTXT = dialog.findViewById(R.id.buttonRegisterUpdate);
                final Button buttonRetourUpdateTXT = dialog.findViewById(R.id.buttonLeftUpdate);

                try {
                    String currentItemMdp = bddManager.decrypt(currentItem.getMotdepasse(),currentItem.getNom());
                    editTextNameUpdateTXT.setText(currentItem.getNom());
                    editTextPasswordUpdateTXT.setText(currentItemMdp);
                    editTextPasswordConfirmedUpdateTXT.setText(currentItemMdp);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                buttonRegisterUpdateTXT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nomUpdate = editTextNameUpdateTXT.getText().toString();
                        String mdpUpdate = editTextPasswordUpdateTXT.getText().toString();
                        String mdpConfirmedUpdate = editTextPasswordConfirmedUpdateTXT.getText().toString();
                        String idUtilisateur = String.valueOf(currentItem.getIdmotdepasse());

                        if(TextUtils.isEmpty(nomUpdate) || TextUtils.isEmpty(mdpUpdate) || TextUtils.isEmpty(mdpConfirmedUpdate)){
                            Toast.makeText(context,"Tout les champs doivent être complétés",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if (mdpUpdate.equals(mdpConfirmedUpdate)){
                                try {
                                    bddManager.updateMdp(nomUpdate,bddManager.encrypt(mdpUpdate,nomUpdate),idUtilisateur);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                currentItem.setNom(nomUpdate);
                                try {
                                    currentItem.setMotdepasse(bddManager.encrypt(mdpUpdate,nomUpdate));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                itemMdpView.setText(currentItem.getMotdepasse());
                                itemNameView.setText(currentItem.getNom());
                                itemMdpView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                Toast.makeText(context,"Votre mot de passe est modifé",Toast.LENGTH_SHORT).show();

                                dialog.dismiss();
                            }

                            else{
                                Toast.makeText(context,"Les mots de passe doivent être les mêmes.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                buttonRetourUpdateTXT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
//==================================================== FIN MÉTHODE BOUTON UPDATE ====================================================================================

//==================================================== DÉBUT MÉTHODE BOUTON DELETE ====================================================================================

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Affichage du PopUp

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.popup_deletemdp);
                dialog.show();

                // Initialisation des variables

                final TextView nameDeleteTXT = dialog.findViewById(R.id.textViewNameDelete);
                final Button buttonOuiDeleteTXT = dialog.findViewById(R.id.buttonDeleteYes);
                final Button buttonNonDeleteTXT = dialog.findViewById(R.id.buttonDeleteNo);
                String idMotdePasse = String.valueOf(currentItem.getIdmotdepasse());

                nameDeleteTXT.setText(currentItem.getNom());

                buttonOuiDeleteTXT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bddManager.deleteMdp(idMotdePasse);
                        MotDePasseList.remove(getItem(position)); // Supprime l'élément de la liste, pas de la base de donnée
                        Toast.makeText(context,"Votre mot de passe, a été supprimé",Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                        dialog.dismiss();

                    }
                });

                buttonNonDeleteTXT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
//==================================================== DÉBUT MÉTHODE BOUTON DELETE ====================================================================================


        return view;
    }
}
