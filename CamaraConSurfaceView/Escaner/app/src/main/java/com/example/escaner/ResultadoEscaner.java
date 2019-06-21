package com.example.escaner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ResultadoEscaner extends AppCompatActivity {
    EditText txtRes;
    Button btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_escaner);

        txtRes=(EditText)findViewById(R.id.res_Edit);
        btnRegresar=(Button)findViewById(R.id.regresar);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultadoEscaner.this, MainActivity.class));
                finish();
            }
        });

        txtRes.setText(toString());
    }



    //public void regresar(View v){
        //startActivity(new Intent(ResultadoEscaner.this,MainActivity.class));
      //  finish();
    //}

   // public ResultadoEscaner() {
    //}
}
