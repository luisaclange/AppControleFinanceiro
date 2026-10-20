package com.utfpr.luisalange.controlefinanceiro.movimentacoes;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

@Entity
public class Movimentacao implements Cloneable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(index = true)
    private String descricao;

    private Date data;
    private float valor;
    private boolean fixo;
    private Tipo tipo;
    private int categoria;
    private long conta;

    public Movimentacao(String descricao, Date data, float valor, boolean fixo, Tipo tipo, int categoria, long conta) {
        this.descricao = descricao;
        this.data = data;
        this.valor = valor;
        this.fixo = fixo;
        this.tipo = tipo;
        this.categoria = categoria;
        this.conta = conta;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public long getConta() {
        return conta;
    }

    public void setConta(long conta) {
        this.conta = conta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public boolean isFixo() {
        return fixo;
    }

    public void setFixo(boolean fixo) {
        this.fixo = fixo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
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

        Movimentacao movimentacao = (Movimentacao) o;

        return descricao.equals(movimentacao.descricao) &&
                tipo == movimentacao.tipo &&
                fixo == movimentacao.fixo &&
                valor == movimentacao.valor &&
                data.equals(movimentacao.data) &&
                conta == movimentacao.conta &&
                categoria == movimentacao.categoria;
    }

    @Override
    public int hashCode() {
        return Objects.hash(descricao, data, valor, fixo, tipo, categoria, conta);
    }

    public static Comparator<Movimentacao> ordenacaoDecrescente = new Comparator<Movimentacao>() {

        @Override
        public int compare(Movimentacao item_a, Movimentacao item_b) {
            return -1 * item_a.getData().compareTo(item_b.getData());
        }
    };

    public static Comparator<Movimentacao> ordenacaoCrescente = new Comparator<Movimentacao>() {

        @Override
        public int compare(Movimentacao item_a, Movimentacao item_b) {
            return item_a.getData().compareTo(item_b.getData());
        }
    };
}
