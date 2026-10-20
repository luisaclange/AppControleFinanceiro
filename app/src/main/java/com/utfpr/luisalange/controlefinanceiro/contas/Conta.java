package com.utfpr.luisalange.controlefinanceiro.contas;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.utfpr.luisalange.controlefinanceiro.R;
import com.utfpr.luisalange.controlefinanceiro.movimentacoes.Movimentacao;

import java.util.Comparator;
import java.util.Objects;

@Entity
public class Conta implements Cloneable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(index = true)
    private String banco;
    private Float valorMaximo;
    private Float limiteCredito;
    private int tipoConta;

    public Conta(String banco, int tipoConta, Float valorMaximo, Float limiteCredito) {
        this.banco = banco;
        this.valorMaximo = valorMaximo;
        this.limiteCredito = limiteCredito;
        this.tipoConta = tipoConta;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public Float getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(Float valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public Float getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(Float limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public int getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(int tipoConta) {
        this.tipoConta = tipoConta;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Conta conta = (Conta) o;

        return banco.equals(conta.banco) &&
                tipoConta == conta.tipoConta &&
                valorMaximo.equals(conta.valorMaximo) &&
                limiteCredito.equals(conta.limiteCredito);
    }

    @Override
    public int hashCode() {
        return Objects.hash(banco, tipoConta, valorMaximo, limiteCredito);
    }

    public static Comparator<Conta> ordenacaoCrescente = new Comparator<Conta>() {

        @Override
        public int compare(Conta item_a, Conta item_b) {
            return item_a.getBanco().compareTo(item_b.getBanco());
        }
    };
}
