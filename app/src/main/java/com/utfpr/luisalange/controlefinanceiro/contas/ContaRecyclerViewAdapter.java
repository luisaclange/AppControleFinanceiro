package com.utfpr.luisalange.controlefinanceiro.contas;

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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ContaRecyclerViewAdapter extends RecyclerView.Adapter<ContaRecyclerViewAdapter.ContaHolder> {
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private OnCreateContextMenu onCreateContextMenu;
    private OnContextMenuClickListener onContextMenuClickListener;

    private Context context;

    private List<Conta> listaContas;

    private String[] tiposConta;

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

    public class ContaHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener,
            View.OnCreateContextMenuListener {

        public TextView textViewValueBanco;
        public TextView textViewValueValorMaximo;
        public TextView textViewValueLimiteCredito;
        public TextView textViewValueTipoConta;

        public ContaHolder(@NonNull View itemView) {
            super(itemView);

            textViewValueBanco = itemView.findViewById(R.id.textViewValueBanco);
            textViewValueValorMaximo = itemView.findViewById(R.id.textViewValueValorMaximo);
            textViewValueLimiteCredito = itemView.findViewById(R.id.textViewValueLimiteCredito);
            textViewValueTipoConta = itemView.findViewById(R.id.textViewValueTipoConta);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            if (onCreateContextMenu != null) {
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

                if (onContextMenuClickListener != null) {
                    onContextMenuClickListener.onContextMenuItemClick(item, getAdapterPosition());
                    return true;
                }
                return false;
            }
        };
    }

    public ContaRecyclerViewAdapter(Context context, List<Conta> listaContas) {
        this.context = context;
        this.listaContas = listaContas;

        tiposConta = context.getResources().getStringArray(R.array.tipoContas);
    }

    @NonNull
    @Override
    public ContaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.line_lista_conta, parent, false);

        return new ContaHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContaHolder holder, int position) {

        Conta conta = listaContas.get(position);

        holder.textViewValueBanco.setText(conta.getBanco());

        DecimalFormat df = new DecimalFormat("0.00");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));

        if (conta.getValorMaximo() == null) {
            holder.textViewValueValorMaximo.setText(R.string.label_nao_informado);
        } else {
            holder.textViewValueValorMaximo.setText("R$ " + df.format(conta.getValorMaximo()));
        }

        if (conta.getLimiteCredito() == null) {
            holder.textViewValueLimiteCredito.setText(R.string.label_nao_informado);
        } else {
            holder.textViewValueLimiteCredito.setText("R$ " + df.format(conta.getLimiteCredito()));
        }

        holder.textViewValueTipoConta.setText(tiposConta[conta.getTipoConta()]);
    }

    @Override
    public int getItemCount() {
        return listaContas.size();
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
