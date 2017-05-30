package br.com.mitraconsignado.whattsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import br.com.mitraconsignado.whattsapp.R;
import br.com.mitraconsignado.whattsapp.config.ConfiguracaoFirebase;
import br.com.mitraconsignado.whattsapp.helper.Base64Custom;
import br.com.mitraconsignado.whattsapp.helper.Preferencias;
import br.com.mitraconsignado.whattsapp.modelo.Usuario;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button botaocadastro;
    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nome = (EditText)findViewById(R.id.edit_cadastro_nome);
        email = (EditText)findViewById(R.id.edit_cadastro_email);
        senha = (EditText)findViewById(R.id.edit_cadastro_senha);
        botaocadastro = (Button) findViewById(R.id.button_cadastrar);

        botaocadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = new Usuario();
                usuario.setNome(nome.getText().toString());
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                cadastrarUsuarios();
            }
        });
    }

    private void cadastrarUsuarios(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getBaseContext(),"Sucesso ao cadastrar usuarios",Toast.LENGTH_LONG).show();

                   String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setId(identificadorUsuario);
                    usuario.salvar();

                    //aqui vai salvar os dados no celular
                    Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);
                    String identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());
                    preferencias.salvarDados(identificadorUsuarioLogado);

                    abrirLoginUsuario();

                }else {
                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erroExcecao = "Digite uma senha mais forte, contendo mais caracteres e com letras e números";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "Digite uma e-mail valido,ou um e-mail novo";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erroExcecao = "Este e-mail já esta em uso no App! ";
                    } catch (Exception e) {
                        erroExcecao = "Erro ao efetuar o cadastro ";
                        e.printStackTrace();
                    }
                    Toast.makeText(getBaseContext(),"Erro:  "+ erroExcecao,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void abrirLoginUsuario() {
        Intent intent = new Intent(getBaseContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
