package com.utfpr.luisalange.controlefinanceiro.contas;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.utfpr.luisalange.controlefinanceiro.utils.DateConverter;

@Database(entities = {Conta.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class ContasDatabase extends RoomDatabase {
    public abstract ContaDao getContaDao();

    private static  ContasDatabase INSTANCE;

    public static ContasDatabase getInstance(final Context context){

        if (INSTANCE == null){

            synchronized (ContasDatabase.class){

                if (INSTANCE == null){

                    INSTANCE = Room.databaseBuilder(context,
                            ContasDatabase.class,
                            "contas.db").allowMainThreadQueries().build();
                }
            }
        }

        return INSTANCE;
    }
}