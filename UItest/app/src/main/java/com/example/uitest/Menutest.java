package com.example.uitest;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Menutest extends AppCompatActivity {

    TextView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menutest);
        view = findViewById(R.id.menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_small:
                view.setTextSize(10);
                break;
            case R.id.menu_middle:
                view.setTextSize(16);
                break;
            case R.id.menu_big:
                view.setTextSize(20);
                break;
            case R.id.menu_normal:
                Toast.makeText(this, "普通菜单项", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_red:
                view.setTextColor(Color.RED);
                break;
            case R.id.menu_black:
                view.setTextColor(Color.BLACK);
                break;
            default:
                break;
        }
        return true;
    }
}