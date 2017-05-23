package br.com.mitraconsignado.whattsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

import br.com.mitraconsignado.whattsapp.R;
import br.com.mitraconsignado.whattsapp.helper.Permissao;
import br.com.mitraconsignado.whattsapp.helper.Preferencias;

public class LoginActivity extends AppCompatActivity {

    private EditText nome;
    private EditText fone;
    private EditText codpais;
    private EditText codarea;
    private Button cadastrar;
    private String[] permisaoNecesarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Permissao.validaPermisao(this, permisaoNecesarias, 1);

        nome = (EditText) findViewById(R.id.edit_Nome);
        fone = (EditText) findViewById(R.id.edit_telefone);
        codpais = (EditText) findViewById(R.id.edit_cod_pais);
        codarea = (EditText) findViewById(R.id.edit_cod_regiao);
        cadastrar = (Button) findViewById(R.id.button_cadastrar);

        SimpleMaskFormatter simpleMaskCodPais = new SimpleMaskFormatter("+NN");
        SimpleMaskFormatter simpleMaskCodRegiao = new SimpleMaskFormatter("NN");
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNN-NNNNN");

        MaskTextWatcher maskCodPais = new MaskTextWatcher(codpais, simpleMaskCodPais);
        MaskTextWatcher maskCodRegiao = new MaskTextWatcher(codarea, simpleMaskCodRegiao);
        MaskTextWatcher masktelefomne = new MaskTextWatcher(fone, simpleMaskTelefone);

        codpais.addTextChangedListener(maskCodPais);
        codarea.addTextChangedListener(maskCodRegiao);
        fone.addTextChangedListener(masktelefomne);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeUsuario = nome.getText().toString();
                String telefoneCompleto =
                        codpais.getText().toString() +
                                codarea.getText().toString() +
                                fone.getText().toString();

                String telefoneSemFormatacao = telefoneCompleto.replace("+", "");
                telefoneSemFormatacao = telefoneSemFormatacao.replace("-", "");
//                Log.i("TELEFONE","T = "+telefoneSemFormatacao);

                //gerar o token não é recomendado
                Random randomico = new Random();
                int numerorandomico = randomico.nextInt(9999 - 1000) + 1000;
                String token = String.valueOf(numerorandomico);
                String mensagemEnvio = "Whatsapp Código de Confirmação:" + token;

                //salvar os dados para validar
                Preferencias preferencias = new Preferencias(LoginActivity.this);
                preferencias.salvarUsuarioPreferencias(nomeUsuario, telefoneSemFormatacao, token);

                //Envio do SMS
                boolean enviadoSMS = enviaSMS("+" + telefoneSemFormatacao, mensagemEnvio);

                if (enviadoSMS ){
                    Intent intent = new Intent(getBaseContext(),ValidadorActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getBaseContext(),"Problema ao enviar o SMS, tente de novamente!!",Toast.LENGTH_LONG).show();
                }

//                HashMap<String, String> usuarios = preferencias.getDadosUsuario();
//                Log.i("Token","T = "+usuarios.get("nome")+" token: "+ usuarios.get("token")+ " tel: "+ usuarios.get("telefone"));


            }

        });

    }

    private boolean enviaSMS(String telefone, String mensagem) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone, null, mensagem, null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int resultado : grantResults){
            if (resultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissão negada");
        builder.setMessage("Para utilizar o app, é preciso aceitar as permissões");
        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
