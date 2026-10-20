package com.utfpr.luisalange.controlefinanceiro.contas;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utfpr.luisalange.controlefinanceiro.R;
import com.utfpr.luisalange.controlefinanceiro.utils.UtilsAlert;

import java.util.Collections;
import java.util.List;

public class ContasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewContas;

    private TextView textViewMessageListContasEmpty;

    private RecyclerView.LayoutManager layoutManager;
    private ContaRecyclerViewAdapter contaRecyclerViewAdapter;

    private List<Conta> listaContas;

    private int posicaoSelecionada = -1;

    private ActionMode actionMode;

    private View viewSelecionada;
    private Drawable backgroundDrawable;

    private ActionMode.Callback actionCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.contas_item_selecionado, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int idMenuItem = item.getItemId();

            if (idMenuItem == R.id.menuItemEditar) {
                editarConta();
                return true;
            } else if (idMenuItem == R.id.menuItemExcluir) {
                excluirConta();
                mode.finish();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            if (viewSelecionada != null) {
                viewSelecionada.setBackground(backgroundDrawable);
            }

            actionMode = null;
            viewSelecionada = null;
            backgroundDrawable = null;

            recyclerViewContas.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas);

        setTitle(getString(R.string.title_contas));

        textViewMessageListContasEmpty = findViewById(R.id.textViewMessageListContasEmpty);

        recyclerViewContas = findViewById(R.id.recyclerViewContas);

        layoutManager = new LinearLayoutManager(this);

        recyclerViewContas.setLayoutManager(layoutManager);
        recyclerViewContas.setHasFixedSize(true);
        recyclerViewContas.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        popularListaContas();
    }

    private void popularListaContas() {
        ContasDatabase database = ContasDatabase.getInstance(this);

        listaContas = database.getContaDao().queryAllAscending();

        if (listaContas.isEmpty()) {
            textViewMessageListContasEmpty.setVisibility(View.VISIBLE);
        } else {
            textViewMessageListContasEmpty.setVisibility(View.GONE);
        }

        contaRecyclerViewAdapter = new ContaRecyclerViewAdapter(this, listaContas);

        contaRecyclerViewAdapter.setOnItemClickListener(new ContaRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                posicaoSelecionada = position;
                editarConta();
            }
        });

        contaRecyclerViewAdapter.setOnItemLongClickListener(new ContaRecyclerViewAdapter.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(View view, int position) {

                if (actionMode != null) {
                    return false;
                }

                posicaoSelecionada = position;

                viewSelecionada = view;
                backgroundDrawable = view.getBackground();

                view.setBackgroundColor(getResources().getColor(R.color.detachColor));

                recyclerViewContas.setEnabled(false);

                actionMode = startSupportActionMode(actionCallback);

                return true;
            }
        });

        recyclerViewContas.setAdapter(contaRecyclerViewAdapter);
    }

    ActivityResultLauncher<Intent> launcherNovaConta = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == ContasActivity.RESULT_OK) {

                        Intent intent = result.getData();

                        Bundle bundle = intent.getExtras();

                        if (bundle != null) {

                            long id = bundle.getLong(ContaActivity.KEY_ID);

                            ContasDatabase database = ContasDatabase.getInstance(ContasActivity.this);

                            Conta conta = database.getContaDao().queryForId(id);

                            listaContas.add(conta);

                            ordenarLista();
                        }
                    }
                }
            });

    public void abrirNovaConta() {

        Intent intentAbertura = new Intent(this, ContaActivity.class);

        intentAbertura.putExtra(ContaActivity.KEY_MODO, ContaActivity.MODO_NOVO);

        launcherNovaConta.launch(intentAbertura);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contas_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemAdicionar) {
            abrirNovaConta();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void excluirConta() {
        final Conta conta = listaContas.get(posicaoSelecionada);

        String mensagem = getString(R.string.message_deseja_apagar, conta.getBanco());

        DialogInterface.OnClickListener listenerSim = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                ContasDatabase database = ContasDatabase.getInstance(ContasActivity.this);

                int quantidadeAlterada = database.getContaDao().delete(conta);

                if (quantidadeAlterada != 1) {
                    UtilsAlert.mostrarAviso(ContasActivity.this, R.string.message_erro_ao_tentar_excluir);
                    return;
                }

                listaContas.remove(posicaoSelecionada);
                contaRecyclerViewAdapter.notifyItemRemoved(posicaoSelecionada);
                if (listaContas.isEmpty()) {
                    textViewMessageListContasEmpty.setVisibility(View.VISIBLE);
                } else {
                    textViewMessageListContasEmpty.setVisibility(View.GONE);
                }
                if (actionMode != null){
                    actionMode.finish();
                }
            }
        };

        UtilsAlert.confirmarAcao(this, mensagem, listenerSim, null);
    }

    ActivityResultLauncher<Intent> launcherEditarConta = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == ContasActivity.RESULT_OK) {

                        Intent intent = result.getData();

                        Bundle bundle = intent.getExtras();

                        if (bundle != null) {
                            long id = bundle.getLong(ContaActivity.KEY_ID);

                            final ContasDatabase database = ContasDatabase.getInstance(ContasActivity.this);

                            final Conta contaEditada = database.getContaDao().queryForId(id);

                            listaContas.set(posicaoSelecionada, contaEditada);

                            ordenarLista();
                        }
                    }

                    posicaoSelecionada = -1;

                    if (actionMode != null) {
                        actionMode.finish();
                    }
                }
            });

    private void editarConta() {

        Conta conta = listaContas.get(posicaoSelecionada);

        Intent intentAbertura = new Intent(this, ContaActivity.class);

        intentAbertura.putExtra(ContaActivity.KEY_MODO, ContaActivity.MODO_EDITAR);

        intentAbertura.putExtra(ContaActivity.KEY_ID, conta.getId());

        launcherEditarConta.launch(intentAbertura);
    }

    private void ordenarLista() {
        Collections.sort(listaContas, Conta.ordenacaoCrescente);

        if (listaContas.isEmpty()) {
            textViewMessageListContasEmpty.setVisibility(View.VISIBLE);
        } else {
            textViewMessageListContasEmpty.setVisibility(View.GONE);
        }

        contaRecyclerViewAdapter.notifyDataSetChanged();
    }
}