package com.utfpr.luisalange.controlefinanceiro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SobreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        setTitle(getString(R.string.title_sobre));
    }

    private void abrirSite(String endereco){

        Intent intentAbertura = new Intent(Intent.ACTION_VIEW);

        intentAbertura.setData(Uri.parse(endereco));

        if (intentAbertura.resolveActivity(getPackageManager()) != null){
            startActivity(intentAbertura);
        }else{
            Toast.makeText(this,
                    R.string.message_nao_ha_aplicativos_abrir_site,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void abrirSiteUTFPR(View view) {
        abrirSite("www.utfpr.edu.br");
    }

    public void enviarEmailAutor(View view){
        enviarEmail(new String[]{getString(R.string.author_email)}, getString(R.string.email_assunto));
    }

    private void enviarEmail(String[] enderecos, String assunto){

        Intent intentAbertura = new Intent(Intent.ACTION_SENDTO);

        intentAbertura.setData(Uri.parse("mailto:"));
        intentAbertura.putExtra(Intent.EXTRA_EMAIL, enderecos);
        intentAbertura.putExtra(Intent.EXTRA_SUBJECT, assunto);

        if (intentAbertura.resolveActivity(getPackageManager()) != null){
            startActivity(intentAbertura);
        }else{
            Toast.makeText(this,
                    R.string.message_nao_ha_aplicativos_envio_email,
                    Toast.LENGTH_LONG).show();
        }
    }
}