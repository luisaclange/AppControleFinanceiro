package com.utfpr.luisalange.controlefinanceiro.movimentacoes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.utfpr.luisalange.controlefinanceiro.R;
import com.utfpr.luisalange.controlefinanceiro.contas.Conta;
import com.utfpr.luisalange.controlefinanceiro.contas.ContasDatabase;
import com.utfpr.luisalange.controlefinanceiro.utils.UtilsAlert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovimentacaoActivity extends AppCompatActivity {
    public static final String KEY_MODO = "MODO";
    public static final String KEY_ID = "ID";

    public static final int MODO_NOVO = 0;
    public static final int MODO_EDITAR = 1;

    private EditText editTextDescricao, editTextData, editTextValor;
    private CheckBox checkBoxFixo;
    private RadioGroup radioGroupTipo;
    private RadioButton radioButtonSaida, radioButtonEntrada;
    private Spinner spinnerCategoria, spinnerConta;

    private int modo;
    private Movimentacao movimentacaoOriginal;

    private List<Conta> listaContas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimentacao);

        editTextDescricao = findViewById(R.id.editTextDescricao);
        editTextData = findViewById(R.id.editTextData);
        editTextValor = findViewById(R.id.editTextValor);
        checkBoxFixo = findViewById(R.id.checkBoxFixo);
        radioGroupTipo = findViewById(R.id.radioGroupTipo);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerConta = findViewById(R.id.spinnerConta);
        radioButtonEntrada = findViewById(R.id.radioButtonEntrada);
        radioButtonSaida = findViewById(R.id.radioButtonSaida);

        popularSpinnerConta();

        Intent intentAbertura = getIntent();

        Bundle bundle = intentAbertura.getExtras();

        if (bundle != null) {

            modo = bundle.getInt(KEY_MODO);

            if (modo == MODO_NOVO) {
                setTitle(R.string.title_nova_movimentacao);
                SimpleDateFormat formatoSaida =
                        new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
                editTextData.setText(formatoSaida.format(new Date()));
            } else {

                setTitle(getString(R.string.title_editar_movimentacao));

                long id = bundle.getLong(KEY_ID);

                MovimentacoesDatabase database = MovimentacoesDatabase.getInstance(this);

                movimentacaoOriginal = database.getMovimentacaoDao().queryForId(id);

                Date data = movimentacaoOriginal.getData();
                SimpleDateFormat formatoSaida =
                        new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());

                String dataFormatada = formatoSaida.format(data);

                editTextDescricao.setText(movimentacaoOriginal.getDescricao());
                editTextData.setText(dataFormatada);
                editTextValor.setText(String.valueOf(movimentacaoOriginal.getValor()));
                checkBoxFixo.setChecked(movimentacaoOriginal.isFixo());
                spinnerCategoria.setSelection(movimentacaoOriginal.getCategoria());

                int posicao = -1;

                for (int i = 0; i < listaContas.size(); i++) {
                    if (listaContas.get(i).getId() == movimentacaoOriginal.getConta()) {
                        posicao = i;
                        break;
                    }
                }

                if (posicao >= 0) {
                    spinnerConta.setSelection(posicao);
                }

                if (movimentacaoOriginal.getTipo() == Tipo.Saida) {
                    radioButtonSaida.setChecked(true);
                } else if (movimentacaoOriginal.getTipo() == Tipo.Entrada) {
                    radioButtonEntrada.setChecked(true);
                }

            }
        }
    }

    private void popularSpinnerConta() {
        ContasDatabase database = ContasDatabase.getInstance(this);
        listaContas = database.getContaDao().queryAllAscending();

        List<String> nomesContas = new ArrayList<>();
        String[] tipoContas = getResources().getStringArray(R.array.tipoContas);

        for (Conta conta : listaContas) {
            nomesContas.add(
                    conta.getBanco() + " - " + tipoContas[conta.getTipoConta()]
            );
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nomesContas
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerConta.setAdapter(adapter);
    }

    public void limparCampos() {

        editTextDescricao.setText(null);
        editTextData.setText(null);
        editTextValor.setText(null);
        checkBoxFixo.setChecked(false);
        radioGroupTipo.clearCheck();
        spinnerCategoria.setSelection(0);
        spinnerConta.setSelection(0);

        editTextDescricao.requestFocus();

        Toast.makeText(this,
                R.string.message_as_entradas_foram_apagadas,
                Toast.LENGTH_LONG).show();
    }

    private boolean validateIsNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean validateIsFloat(String value) {
        try {
            float valueParsed = Float.parseFloat(value);
            return !(valueParsed <= 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validateIsDate(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault());
        sdf.setLenient(false);

        try {
            sdf.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public void salvarValores() {
        if (!validateIsNotEmpty(editTextDescricao.getText().toString())) {
            Toast.makeText(this,
                    R.string.message_voce_deve_informar_a_descricao,
                    Toast.LENGTH_LONG).show();

            editTextDescricao.requestFocus();
            return;
        }
        String descricao = editTextDescricao.getText().toString().trim();

        if (!validateIsNotEmpty(editTextData.getText().toString())) {
            Toast.makeText(this,
                    R.string.message_voce_deve_informar_a_data,
                    Toast.LENGTH_LONG).show();

            editTextData.requestFocus();
            return;
        }
        if (!validateIsDate(editTextData.getText().toString())) {
            Toast.makeText(this,
                    R.string.message_a_data_deve_ser_valida,
                    Toast.LENGTH_LONG).show();

            editTextData.requestFocus();
            return;
        }
        String data = editTextData.getText().toString().trim();

        if (!validateIsNotEmpty(editTextValor.getText().toString())) {
            Toast.makeText(this,
                    R.string.message_voce_deve_informar_o_valor,
                    Toast.LENGTH_LONG).show();

            editTextValor.requestFocus();
            return;
        }
        String valorString = editTextValor.getText().toString().trim();

        if (!validateIsFloat(valorString)) {
            Toast.makeText(this,
                    R.string.message_valor_deve_ser_um_numero_valido,
                    Toast.LENGTH_LONG).show();
            editTextValor.requestFocus();
            editTextValor.setSelection(0, editTextValor.getText().toString().length());
            return;
        }
        float valor = Float.parseFloat(valorString);

        Tipo tipo;
        int radioButtonId = radioGroupTipo.getCheckedRadioButtonId();
        if (radioButtonId == R.id.radioButtonEntrada) {
            tipo = Tipo.Entrada;
        } else if (radioButtonId == R.id.radioButtonSaida) {
            tipo = Tipo.Saida;
        } else {
            Toast.makeText(this,
                    R.string.message_selecione_o_tipo,
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!validateIsNotEmpty((String) spinnerConta.getSelectedItem())) {
            Toast.makeText(this,
                    R.string.message_o_spinner_conta_nao_possui_valores,
                    Toast.LENGTH_LONG).show();
            return;
        }
        int conta = spinnerConta.getSelectedItemPosition();

        if (!validateIsNotEmpty((String) spinnerCategoria.getSelectedItem())) {
            Toast.makeText(this,
                    R.string.message_o_spinner_categoria_nao_possui_valores,
                    Toast.LENGTH_LONG).show();
            return;
        }
        int categoria = spinnerCategoria.getSelectedItemPosition();

        boolean fixo = checkBoxFixo.isChecked();

        SimpleDateFormat formato = new SimpleDateFormat(getString(R.string.date_format));

        Date dataFormatada = new Date();

        try {
            dataFormatada = formato.parse(data);
        } catch (ParseException e) {
            //
        }

        Movimentacao movimentacao = new Movimentacao(descricao, dataFormatada, valor, fixo, tipo, categoria, listaContas.get(conta).getId());

        if (movimentacao.equals(movimentacaoOriginal)) {
            setResult(MovimentacaoActivity.RESULT_CANCELED);
            finish();
            return;
        }

        Intent intentResposta = new Intent();

        MovimentacoesDatabase database = MovimentacoesDatabase.getInstance(this);

        if (modo == MODO_NOVO) {

            long novoId = database.getMovimentacaoDao().insert(movimentacao);

            if (novoId <= 0) {
                UtilsAlert.mostrarAviso(this, R.string.message_erro_ao_inserir_movimentacao);
                return;
            }

            movimentacao.setId(novoId);

        } else {

            movimentacao.setId(movimentacaoOriginal.getId());

            int quantidadeAlterada = database.getMovimentacaoDao().update(movimentacao);

            if (quantidadeAlterada != 1) {
                UtilsAlert.mostrarAviso(this, R.string.message_erro_ao_tentar_alterar);
                return;
            }
        }

        intentResposta.putExtra(KEY_ID, movimentacao.getId());

        setResult(MovimentacaoActivity.RESULT_OK, intentResposta);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movimentacao_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemSalvar) {
            salvarValores();
            return true;
        } else if (idMenuItem == R.id.menuItemLimpar) {
            limparCampos();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}