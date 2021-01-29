package com.sarthak.my_object_identifier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder window;
    private final String[] Options = {"Float Model", "Quantized Model"};
    private Button btn_choose_fq;
    private Button btn_proceed;
    private boolean isQuant = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        window = new AlertDialog.Builder(this);
        window.setTitle("Please choose your option");
        window.setItems(Options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int chosen) {
                switch (chosen) {
                    case 0:
                        Toast.makeText(MainActivity.this, "Float Selected", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "Quantized Selected", Toast.LENGTH_SHORT).show();
                        isQuant = true;
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Choose again!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // map btn_choose_fq button
        btn_choose_fq = findViewById(R.id.btn_choose_fq);
        // button to show user options
        btn_choose_fq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.show();
            }
        });

        // map btn_choose_fq button
        btn_proceed = findViewById(R.id.btn_proceed);
        // button to show user options
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(MainActivity.this, ClassificationActivity.class);
                    i.putExtra("isQuant", isQuant);
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
