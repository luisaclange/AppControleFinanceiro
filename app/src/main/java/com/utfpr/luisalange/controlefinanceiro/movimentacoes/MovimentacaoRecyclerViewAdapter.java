package com.utfpr.luisalange.controlefinanceiro.movimentacoes;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.utfpr.luisalange.controlefinanceiro.R;
import com.utfpr.luisalange.controlefinanceiro.contas.Conta;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MovimentacaoRecyclerViewAdapter extends RecyclerView.Adapter<MovimentacaoRecyclerViewAdapter.MovimentacaoHolder> {
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener    onItemLongClickListener;

    private OnCreateContextMenu        onCreateContextMenu;
    private OnContextMenuClickListener onContextMenuClickListener;

    private Context context;

    private List<Movimentacao> listaMovimentacoes;

    private String[] categorias;
    private String[] tipoContas;
    private List<Conta> contas;

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }

    interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

    interface OnCreateContextMenu {
        void onCreateContextMenu(ContextMenu menu,
                                 View v,
                                 ContextMenu.ContextMenuInfo menuInfo,
                                 int position,
                                 MenuItem.OnMenuItemClickListener menuItemClickListener);
    }

    interface OnContextMenuClickListener {
        boolean onContextMenuItemClick(MenuItem menuItem, int position);
    }

    public class MovimentacaoHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                                                                                View.OnLongClickListener,
                                                                                View.OnCreateContextMenuListener {

        public TextView textViewValueDescricao;
        public TextView textViewValueData;
        public TextView textViewValueValor;
        public TextView textViewValueFixo;
        public TextView textViewValueTipo;
        public TextView textViewValueCategoria;
        public TextView textViewValueConta;

        public MovimentacaoHolder(@NonNull View itemView) {
            super(itemView);

            textViewValueDescricao  = itemView.findViewById(R.id.textViewValueBanco);
            textViewValueData       = itemView.findViewById(R.id.textViewValueData);
            textViewValueValor      = itemView.findViewById(R.id.textViewValueValor);
            textViewValueFixo       = itemView.findViewById(R.id.textViewValueFixo);
            textViewValueTipo       = itemView.findViewById(R.id.textViewValueTipo);
            textViewValueCategoria  = itemView.findViewById(R.id.textViewValueValorMaximo);
            textViewValueConta      = itemView.findViewById(R.id.textViewValueLimiteCredito);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {

            if (onItemClickListener != null){
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if (onItemLongClickListener != null){
                onItemLongClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            if (onCreateContextMenu != null){
                onCreateContextMenu.onCreateContextMenu(menu,
                        v,
                        menuInfo,
                        getAdapterPosition(),
                        onMenuItemClickListener);
            }
        }

        MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {

                if (onContextMenuClickListener != null){
                    onContextMenuClickListener.onContextMenuItemClick(item, getAdapterPosition());
                    return true;
                }
                return false;
            }
        };
    }

    public MovimentacaoRecyclerViewAdapter(Context context, List<Movimentacao> listaMovimentacoes, List<Conta> listaContas) {
        this.context             = context;
        this.listaMovimentacoes  = listaMovimentacoes;

        categorias = context.getResources().getStringArray(R.array.categorias);
        tipoContas = context.getResources().getStringArray(R.array.tipoContas);
        contas = listaContas;
    }

    @NonNull
    @Override
    public MovimentacaoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.line_lista_movimentacao, parent, false);

        return new MovimentacaoHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovimentacaoHolder holder, int position) {

        Movimentacao movimentacao = listaMovimentacoes.get(position);

        holder.textViewValueDescricao.setText(movimentacao.getDescricao());

        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.date_format), Locale.getDefault());
        holder.textViewValueData.setText(sdf.format(movimentacao.getData()));

        DecimalFormat df = new DecimalFormat("0.00");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));
        holder.textViewValueValor.setText(df.format(movimentacao.getValor()));

        if (movimentacao.isFixo()){
            holder.textViewValueFixo.setText(R.string.label_movimentacao_fixa);
        }else{
            holder.textViewValueFixo.setText(R.string.label_movimentacao_nao_fixa);
        }

        holder.textViewValueCategoria.setText(categorias[movimentacao.getCategoria()]);

        int posicao = -1;

        for (int i = 0; i < contas.size(); i++) {
            if (contas.get(i).getId() == movimentacao.getConta()) {
                posicao = i;
                break;
            }
        }

        if (posicao >= 0) {
            Conta conta = contas.get(posicao);
            holder.textViewValueConta.setText(conta.getBanco() + " - " + tipoContas[conta.getTipoConta()]);
        }

        switch (movimentacao.getTipo()){

            case Entrada:
                holder.textViewValueTipo.setText(R.string.label_entrada);
                holder.textViewValueTipo.setTextColor(ContextCompat.getColor(context, R.color.entrada));
                break;

            case Saida:
                holder.textViewValueTipo.setText(R.string.label_saida);
                holder.textViewValueTipo.setTextColor(ContextCompat.getColor(context, R.color.saida));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listaMovimentacoes.size();
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public OnCreateContextMenu getOnCreateContextMenu() {
        return onCreateContextMenu;
    }

    public void setOnCreateContextMenu(OnCreateContextMenu onCreateContextMenu) {
        this.onCreateContextMenu = onCreateContextMenu;
    }

    public OnContextMenuClickListener getOnContextMenuClickListener() {
        return onContextMenuClickListener;
    }

    public void setOnContextMenuClickListener(OnContextMenuClickListener onContextMenuClickListener) {
        this.onContextMenuClickListener = onContextMenuClickListener;
    }
}
