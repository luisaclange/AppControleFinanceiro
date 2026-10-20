package com.utfpr.luisalange.controlefinanceiro.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.utfpr.luisalange.controlefinanceiro.R;

public final class UtilsAlert {

    private UtilsAlert(){
        // Para evitar que esta classe seja instanciada
    }

    public static void mostrarAviso(Context context, int idMensagem){
        mostrarAviso(context, context.getString(idMensagem), null);
    }

    public static void mostrarAviso(Context context, int idMensagem,
                                    DialogInterface.OnClickListener listener){
        mostrarAviso(context, context.getString(idMensagem), listener);
    }

    public static void mostrarAviso(Context context, String mensagem,
                                    DialogInterface.OnClickListener listener){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Aviso");
        builder.setIcon(R.drawable.information_slab_circle);
        builder.setMessage(mensagem);

        builder.setNeutralButton("OK", listener);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void confirmarAcao(Context context, int idMensagem,
                                     DialogInterface.OnClickListener listenerSim,
                                     DialogInterface.OnClickListener listenerNao){

        confirmarAcao(context, context.getString(idMensagem), listenerSim, listenerNao);
    }

    public static void confirmarAcao(Context context, String mensagem,
                                     DialogInterface.OnClickListener listenerSim,
                                     DialogInterface.OnClickListener listenerNao){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Confirmação");
        builder.setIcon(R.drawable.alert);

        builder.setMessage(mensagem);

        builder.setPositiveButton("Sim", listenerSim);
        builder.setNegativeButton("Não", listenerNao);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }
}
