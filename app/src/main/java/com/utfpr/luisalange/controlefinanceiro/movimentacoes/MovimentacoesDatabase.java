package com.utfpr.luisalange.controlefinanceiro.movimentacoes;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.utfpr.luisalange.controlefinanceiro.utils.DateConverter;

@Database(entities = {Movimentacao.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class MovimentacoesDatabase extends RoomDatabase {
    public abstract MovimentacaoDao getMovimentacaoDao();

    private static  MovimentacoesDatabase INSTANCE;

    public static MovimentacoesDatabase getInstance(final Context context){

        if (INSTANCE == null){

            synchronized (MovimentacoesDatabase.class){

                if (INSTANCE == null){

                    INSTANCE = Room.databaseBuilder(context,
                            MovimentacoesDatabase.class,
                            "movimentacoes.db").allowMainThreadQueries().build();
                }
            }
        }

        return INSTANCE;
    }
}