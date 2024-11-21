package com.example.uitest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Dialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(Dialog.this);
        View dia= LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog,null);
        dialog.setView(dia);
        dialog.show();
    }
}