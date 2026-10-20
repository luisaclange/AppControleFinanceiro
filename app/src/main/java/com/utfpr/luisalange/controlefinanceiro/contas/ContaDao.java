package com.utfpr.luisalange.controlefinanceiro.contas;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.utfpr.luisalange.controlefinanceiro.contas.Conta;

import java.util.List;

@Dao
public interface ContaDao {
    @Insert
    long insert(Conta conta);

    @Delete
    int delete(Conta conta);

    @Update
    int update(Conta conta);

    @Query("SELECT * FROM conta WHERE id=:id")
    Conta queryForId(long id);

    @Query("SELECT * FROM conta ORDER BY banco ASC")
    List<Conta> queryAllAscending();
}
