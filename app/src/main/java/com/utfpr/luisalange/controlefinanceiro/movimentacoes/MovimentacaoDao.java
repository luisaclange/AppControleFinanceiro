package com.utfpr.luisalange.controlefinanceiro.movimentacoes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovimentacaoDao {
    @Insert
    long insert(Movimentacao movimentacao);

    @Delete
    int delete(Movimentacao movimentacao);

    @Update
    int update(Movimentacao movimentacao);

    @Query("SELECT * FROM movimentacao WHERE id=:id")
    Movimentacao queryForId(long id);

    @Query("SELECT * FROM movimentacao ORDER BY data ASC")
    List<Movimentacao> queryAllAscending();

    @Query("SELECT * FROM movimentacao ORDER BY data DESC")
    List<Movimentacao> queryAllDownward();
}
