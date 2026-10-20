package com.utfpr.luisalange.controlefinanceiro.movimentacoes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.utfpr.luisalange.controlefinanceiro.SobreActivity;
import com.utfpr.luisalange.controlefinanceiro.contas.Conta;
import com.utfpr.luisalange.controlefinanceiro.contas.ContasActivity;
import com.utfpr.luisalange.controlefinanceiro.contas.ContasDatabase;
import com.utfpr.luisalange.controlefinanceiro.utils.UtilsAlert;

import java.util.Collections;
import java.util.List;

public class MovimentacoesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMovimentacoes;
    private RecyclerView.LayoutManager layoutManager;
    private MovimentacaoRecyclerViewAdapter movimentacaoRecyclerViewAdapter;

    private TextView textViewMessageListEmpty;

    private List<Movimentacao> listaMovimentacoes;

    private int posicaoSelecionada = -1;

    private ActionMode actionMode;

    private View viewSelecionada;
    private Drawable backgroundDrawable;

    public static final String ARQUIVO_PREFERENCIAS = "br.edu.utfpr.luisalange.controlefinanceiro.PREFERENCES";

    public static final String KEY_ORDENACAO_DESCENDENTE = "ORDENACAO_DESCENDENTE";

    public static final boolean PADRAO_INICIAL_ORDENACAO_DESCENDENTE = true;

    private boolean ordenacaoDescendente = PADRAO_INICIAL_ORDENACAO_DESCENDENTE;
    private MenuItem menuItemOrdenacao;

    private ActionMode.Callback actionCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.movimentacoes_item_selecionado, menu);
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
                editarMovimentacao();
                return true;
            } else if (idMenuItem == R.id.menuItemExcluir) {
                excluirMovimentacao();
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

            recyclerViewMovimentacoes.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimentacoes);

        textViewMessageListEmpty = findViewById(R.id.textViewMessageListEmpty);

        setTitle(getString(R.string.title_movimentacoes));

        recyclerViewMovimentacoes = findViewById(R.id.recyclerViewMovimentacoes);

        layoutManager = new LinearLayoutManager(this);

        recyclerViewMovimentacoes.setLayoutManager(layoutManager);
        recyclerViewMovimentacoes.setHasFixedSize(true);
        recyclerViewMovimentacoes.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        lerPreferencias();

        popularListaMovimentacoes();
    }

    private void popularListaMovimentacoes() {
        MovimentacoesDatabase database = MovimentacoesDatabase.getInstance(this);

        if (ordenacaoDescendente) {
            listaMovimentacoes = database.getMovimentacaoDao().queryAllDownward();
        } else {
            listaMovimentacoes = database.getMovimentacaoDao().queryAllAscending();
        }

        if (listaMovimentacoes.isEmpty()) {
            textViewMessageListEmpty.setVisibility(View.VISIBLE);
        } else {
            textViewMessageListEmpty.setVisibility(View.GONE);
        }

        ContasDatabase databaseContas = ContasDatabase.getInstance(this);
        List<Conta> listaContas = databaseContas.getContaDao().queryAllAscending();

        movimentacaoRecyclerViewAdapter = new MovimentacaoRecyclerViewAdapter(this, listaMovimentacoes, listaContas);

        movimentacaoRecyclerViewAdapter.setOnItemClickListener(new MovimentacaoRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                posicaoSelecionada = position;
                editarMovimentacao();
            }
        });

        movimentacaoRecyclerViewAdapter.setOnItemLongClickListener(new MovimentacaoRecyclerViewAdapter.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(View view, int position) {

                if (actionMode != null) {
                    return false;
                }

                posicaoSelecionada = position;

                viewSelecionada = view;
                backgroundDrawable = view.getBackground();

                view.setBackgroundColor(getResources().getColor(R.color.detachColor));

                recyclerViewMovimentacoes.setEnabled(false);

                actionMode = startSupportActionMode(actionCallback);

                return true;
            }
        });

        recyclerViewMovimentacoes.setAdapter(movimentacaoRecyclerViewAdapter);
    }

    ActivityResultLauncher<Intent> launcherNovaMovimentacao = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == MovimentacoesActivity.RESULT_OK) {

                        Intent intent = result.getData();

                        Bundle bundle = intent.getExtras();

                        if (bundle != null) {

                            long id = bundle.getLong(MovimentacaoActivity.KEY_ID);

                            MovimentacoesDatabase database = MovimentacoesDatabase.getInstance(MovimentacoesActivity.this);

                            Movimentacao movimentacao = database.getMovimentacaoDao().queryForId(id);

                            listaMovimentacoes.add(movimentacao);

                            ordenarLista();
                        }
                    }
                }
            });

    public void abrirSobre() {

        Intent intentAbertura = new Intent(this, SobreActivity.class);

        startActivity(intentAbertura);
    }

    public void abrirContas() {

        Intent intentAbertura = new Intent(this, ContasActivity.class);

        startActivity(intentAbertura);
    }

    public void abrirNovaMovimentacao() {
        ContasDatabase database = ContasDatabase.getInstance(this);
        List<Conta> listaContas = database.getContaDao().queryAllAscending();

        if (listaContas.isEmpty()) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    abrirContas();
                }
            };
            UtilsAlert.mostrarAviso(MovimentacoesActivity.this, R.string.message_voce_precisa_criar_uma_conta, listener);
            return;
        }

        Intent intentAbertura = new Intent(this, MovimentacaoActivity.class);

        intentAbertura.putExtra(MovimentacaoActivity.KEY_MODO, MovimentacaoActivity.MODO_NOVO);

        launcherNovaMovimentacao.launch(intentAbertura);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        atualizarIconeOrdenacao();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movimentacoes_opcoes, menu);
        menuItemOrdenacao = menu.findItem(R.id.menuItemOrdenacao);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemAdicionar) {
            abrirNovaMovimentacao();
            return true;
        } else if (idMenuItem == R.id.menuItemSobre) {
            abrirSobre();
            return true;
        }
        if (idMenuItem == R.id.menuItemOrdenacao) {
            salvarPreferenciaOrdenacaoDescendente(!ordenacaoDescendente);
            atualizarIconeOrdenacao();
            ordenarLista();
            return true;
        } else if (idMenuItem == R.id.menuItemRestaurar) {
            restaurarPadroes();
            atualizarIconeOrdenacao();
            ordenarLista();

            Toast.makeText(this,
                    R.string.message_as_configuracoes_voltaram_para_o_padrao_de_instalação,
                    Toast.LENGTH_LONG).show();
            return true;
        } else if (idMenuItem == R.id.menuItemContas) {
            abrirContas();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void excluirMovimentacao() {
        final Movimentacao movimentacao = listaMovimentacoes.get(posicaoSelecionada);

        String mensagem = getString(R.string.message_deseja_apagar, movimentacao.getDescricao());

        DialogInterface.OnClickListener listenerSim = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                MovimentacoesDatabase database = MovimentacoesDatabase.getInstance(MovimentacoesActivity.this);

                int quantidadeAlterada = database.getMovimentacaoDao().delete(movimentacao);

                if (quantidadeAlterada != 1) {
                    UtilsAlert.mostrarAviso(MovimentacoesActivity.this, R.string.message_erro_ao_tentar_excluir);
                    return;
                }

                listaMovimentacoes.remove(posicaoSelecionada);
                movimentacaoRecyclerViewAdapter.notifyItemRemoved(posicaoSelecionada);
                if (listaMovimentacoes.isEmpty()) {
                    textViewMessageListEmpty.setVisibility(View.VISIBLE);
                } else {
                    textViewMessageListEmpty.setVisibility(View.GONE);
                }
                if (actionMode != null) {
                    actionMode.finish();
                }
            }
        };

        UtilsAlert.confirmarAcao(this, mensagem, listenerSim, null);
    }

    ActivityResultLauncher<Intent> launcherEditarMovimentacao = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == MovimentacoesActivity.RESULT_OK) {

                        Intent intent = result.getData();

                        Bundle bundle = intent.getExtras();

                        if (bundle != null) {
                            long id = bundle.getLong(MovimentacaoActivity.KEY_ID);

                            final MovimentacoesDatabase database = MovimentacoesDatabase.getInstance(MovimentacoesActivity.this);

                            final Movimentacao movimentacaoEditada = database.getMovimentacaoDao().queryForId(id);

                            listaMovimentacoes.set(posicaoSelecionada, movimentacaoEditada);

                            ordenarLista();
                        }
                    }

                    posicaoSelecionada = -1;

                    if (actionMode != null) {
                        actionMode.finish();
                    }
                }
            });

    private void editarMovimentacao() {

        Movimentacao movimentacao = listaMovimentacoes.get(posicaoSelecionada);

        Intent intentAbertura = new Intent(this, MovimentacaoActivity.class);

        intentAbertura.putExtra(MovimentacaoActivity.KEY_MODO, MovimentacaoActivity.MODO_EDITAR);

        intentAbertura.putExtra(MovimentacaoActivity.KEY_ID, movimentacao.getId());

        launcherEditarMovimentacao.launch(intentAbertura);
    }

    private void ordenarLista() {

        if (ordenacaoDescendente) {
            Collections.sort(listaMovimentacoes, Movimentacao.ordenacaoDecrescente);
        } else {
            Collections.sort(listaMovimentacoes, Movimentacao.ordenacaoCrescente);
        }

        if (listaMovimentacoes.isEmpty()) {
            textViewMessageListEmpty.setVisibility(View.VISIBLE);
        } else {
            textViewMessageListEmpty.setVisibility(View.GONE);
        }

        movimentacaoRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void atualizarIconeOrdenacao() {

        if (ordenacaoDescendente) {
            menuItemOrdenacao.setIcon(R.drawable.sort_calendar_descending);
        } else {
            menuItemOrdenacao.setIcon(R.drawable.sort_calendar_ascending);
        }
    }

    private void lerPreferencias() {

        SharedPreferences shared = getSharedPreferences(ARQUIVO_PREFERENCIAS, Context.MODE_PRIVATE);

        ordenacaoDescendente = shared.getBoolean(KEY_ORDENACAO_DESCENDENTE, ordenacaoDescendente);
    }

    private void salvarPreferenciaOrdenacaoDescendente(boolean novoValor) {

        SharedPreferences shared = getSharedPreferences(ARQUIVO_PREFERENCIAS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean(KEY_ORDENACAO_DESCENDENTE, novoValor);

        editor.commit();

        ordenacaoDescendente = novoValor;
    }

    private void restaurarPadroes() {

        SharedPreferences shared = getSharedPreferences(ARQUIVO_PREFERENCIAS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.clear();
        editor.commit();

        ordenacaoDescendente = PADRAO_INICIAL_ORDENACAO_DESCENDENTE;
    }
}