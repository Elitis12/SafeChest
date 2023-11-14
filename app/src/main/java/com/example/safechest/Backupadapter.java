package com.example.safechest;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class Backupadapter extends BaseAdapter {

    private Context context;
    private List<File> backupList;
    private LayoutInflater layoutInflater;
    public bddManager bddManager;

    public Backupadapter (Context context , List<File> backupList){
        this.context = context;
        this.backupList = backupList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return backupList.size();
    }

    @Override
    public File getItem(int position) {
        return backupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = layoutInflater.inflate(R.layout.backup_item,null);
        bddManager = new bddManager(context);
        TextView nameView = (TextView) view.findViewById(R.id.backupListname);
        TextView dateView = (TextView) view.findViewById(R.id.backupListDate);
        ImageButton importButton = (ImageButton) view.findViewById(R.id.imageButtonImport);
        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.imageButtonDelete);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-hh:mm");
        File currentItem = getItem(position);
        String itemName = currentItem.getName();
        long recupDate = (currentItem.lastModified());
        String itemDate = dateFormat.format(recupDate);

        nameView.setText(itemName);
        dateView.setText(String.valueOf(itemDate));

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bddManager.importDatabase(itemName);
                Toast.makeText(context, "Base de donnée, importée !", Toast.LENGTH_SHORT).show();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.popup_deletebackup);
                dialog.show();

                // Initialisation des variables

                final TextView nameDeleteTXT = dialog.findViewById(R.id.textViewNameDelete);
                final Button buttonOuiDeleteTXT = dialog.findViewById(R.id.buttonDeleteYes);
                final Button buttonNonDeleteTXT = dialog.findViewById(R.id.buttonDeleteNo);

                nameDeleteTXT.setText(currentItem.getName());

                buttonOuiDeleteTXT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentItem.delete();
                        backupList.remove(currentItem);
                        notifyDataSetChanged();
                        Toast.makeText(context,"La base de donnée : "+currentItem.getName()+" à été supprimée.",Toast.LENGTH_SHORT).show();
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

        return view;
    }
}
