package com.utfpr.luisalange.controlefinanceiro.contas;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.utfpr.luisalange.controlefinanceiro.R;
import com.utfpr.luisalange.controlefinanceiro.utils.UtilsAlert;

public class ContaActivity extends AppCompatActivity {

    public static final String KEY_MODO = "MODO";
    public static final String KEY_ID = "ID";

    public static final int MODO_NOVO = 0;
    public static final int MODO_EDITAR = 1;

    private EditText editTextBanco, editTextValorMaximo, editTextLimiteCredito;
    private Spinner spinnerTipoConta;

    private int modo;
    private Conta contaOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        editTextBanco = findViewById(R.id.editTextBanco);
        editTextValorMaximo = findViewById(R.id.editTextValorMaximo);
        spinnerTipoConta = findViewById(R.id.spinnerTipoConta);
        editTextLimiteCredito = findViewById(R.id.editTextLimiteCredito);

        Intent intentAbertura = getIntent();

        Bundle bundle = intentAbertura.getExtras();

        if (bundle != null) {

            modo = bundle.getInt(KEY_MODO);

            if (modo == MODO_NOVO) {
                setTitle(R.string.title_nova_conta);
            } else {

                setTitle(getString(R.string.title_editar_conta));

                long id = bundle.getLong(KEY_ID);

                ContasDatabase database = ContasDatabase.getInstance(this);

                contaOriginal = database.getContaDao().queryForId(id);

                editTextBanco.setText(contaOriginal.getBanco());
                if (contaOriginal.getLimiteCredito() != null) {
                    editTextLimiteCredito.setText(String.valueOf(contaOriginal.getLimiteCredito()));
                }
                if (contaOriginal.getValorMaximo() != null) {
                    editTextValorMaximo.setText(String.valueOf(contaOriginal.getValorMaximo()));
                }

                spinnerTipoConta.setSelection(contaOriginal.getTipoConta());

            }
        }
    }

    public void limparCamposConta() {

        editTextBanco.setText(null);
        editTextValorMaximo.setText(null);
        editTextLimiteCredito.setText(null);
        spinnerTipoConta.setSelection(0);

        editTextBanco.requestFocus();

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

    public void salvarValoresConta() {
        if (!validateIsNotEmpty(editTextBanco.getText().toString())) {
            Toast.makeText(this,
                    R.string.message_voce_deve_informar_o_nome_do_banco,
                    Toast.LENGTH_LONG).show();

            editTextBanco.requestFocus();
            return;
        }
        String banco = editTextBanco.getText().toString().trim();

        if (!validateIsNotEmpty((String) spinnerTipoConta.getSelectedItem())) {
            Toast.makeText(this,
                    R.string.message_o_spinner_conta_nao_possui_valores,
                    Toast.LENGTH_LONG).show();
            return;
        }
        int tipoConta = spinnerTipoConta.getSelectedItemPosition();

        String valorMaximoString = editTextValorMaximo.getText().toString().trim();

        if (!valorMaximoString.isEmpty() && !validateIsFloat(valorMaximoString)) {
            Toast.makeText(this,
                    R.string.message_valor_maximo_deve_ser_um_numero_positivo_valido,
                    Toast.LENGTH_LONG).show();
            editTextValorMaximo.requestFocus();
            editTextValorMaximo.setSelection(0, editTextValorMaximo.getText().toString().length());
            return;
        }
        Float valorMaximo = null;
        if (!valorMaximoString.isEmpty()) valorMaximo = Float.parseFloat(valorMaximoString);

        String limiteCreditoString = editTextLimiteCredito.getText().toString().trim();

        if (!limiteCreditoString.isEmpty() && !validateIsFloat(limiteCreditoString)) {
            Toast.makeText(this,
                    R.string.message_limite_de_credito_deve_ser_um_numero_positivo_valido,
                    Toast.LENGTH_LONG).show();
            editTextLimiteCredito.requestFocus();
            editTextLimiteCredito.setSelection(0, editTextLimiteCredito.getText().toString().length());
            return;
        }
        Float limiteCredito = null;
        if (!limiteCreditoString.isEmpty()) limiteCredito = Float.parseFloat(limiteCreditoString);

        Conta conta = new Conta(banco, tipoConta, valorMaximo, limiteCredito);

        if (conta.equals(contaOriginal)) {
            setResult(ContaActivity.RESULT_CANCELED);
            finish();
            return;
        }

        Intent intentResposta = new Intent();

        ContasDatabase database = ContasDatabase.getInstance(this);

        if (modo == MODO_NOVO) {

            long novoId = database.getContaDao().insert(conta);

            if (novoId <= 0) {
                UtilsAlert.mostrarAviso(this, R.string.message_erro_ao_inserir_conta);
                return;
            }

            conta.setId(novoId);

        } else {

            conta.setId(contaOriginal.getId());

            int quantidadeAlterada = database.getContaDao().update(conta);

            if (quantidadeAlterada != 1) {
                UtilsAlert.mostrarAviso(this, R.string.message_erro_ao_tentar_alterar_conta);
                return;
            }
        }

        intentResposta.putExtra(KEY_ID, conta.getId());

        setResult(ContaActivity.RESULT_OK, intentResposta);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conta_opcoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemSalvar) {
            salvarValoresConta();
            return true;
        } else if (idMenuItem == R.id.menuItemLimpar) {
            limparCamposConta();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}