package br.com.mitraconsignado.whattsapp.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.HashMap;

import br.com.mitraconsignado.whattsapp.R;
import br.com.mitraconsignado.whattsapp.helper.Preferencias;

public class ValidadorActivity extends AppCompatActivity {

    private EditText codigoValidacao;
    private Button validar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);

        codigoValidacao = (EditText)findViewById(R.id.edit_cod_validacao);
        validar         = (Button)findViewById(R.id.bt_validar);

        SimpleMaskFormatter simpleMaskCodValidacao = new SimpleMaskFormatter("NNNN");
        MaskTextWatcher maskValidacao = new MaskTextWatcher(codigoValidacao, simpleMaskCodValidacao);

        codigoValidacao.addTextChangedListener(maskValidacao);
        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recuperar dados das preferencias do usuarios
                Preferencias preferencias = new Preferencias(getApplication());
                HashMap<String, String> usuario = preferencias.getDadosUsuario();

                String tokenGerado = usuario.get("token");
                String tokenDigitado = codigoValidacao.getText().toString();

                if (tokenDigitado.equals(tokenGerado)){
                    Toast.makeText(getBaseContext(),"Token Validado",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getBaseContext(),"Token não Validado",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
