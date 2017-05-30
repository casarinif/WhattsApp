package br.com.mitraconsignado.whattsapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.mitraconsignado.whattsapp.Adapter.TabAdapter;
import br.com.mitraconsignado.whattsapp.R;
import br.com.mitraconsignado.whattsapp.config.ConfiguracaoFirebase;
import br.com.mitraconsignado.whattsapp.helper.Base64Custom;
import br.com.mitraconsignado.whattsapp.helper.Preferencias;
import br.com.mitraconsignado.whattsapp.helper.SlidingTabLayout;
import br.com.mitraconsignado.whattsapp.modelo.Contato;
import br.com.mitraconsignado.whattsapp.modelo.Usuario;

public class MainActivity extends AppCompatActivity {

    private Button botaoSair;
    private FirebaseAuth firebase;
    private Toolbar toolbar;

    private DatabaseReference firebaseDataBase;
    private String identificadorContato;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebase = ConfiguracaoFirebase.getFirebaseAutenticacao();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        //distriduição das lista no toolbar
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this,R.color.colorAccent));

        //Configurar adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        slidingTabLayout.setViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sair:
                deslogarUsuarios();
                return true;
            case R.id.item_configuracao:
                return true;
            case R.id.item_adicionar:
                abrirCadastroContato();
                return true;
            default:

            return super.onOptionsItemSelected(item);
        }
    }

    private void abrirCadastroContato() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        //configurar do dialog
        alertDialog.setTitle("Novo contato");
        alertDialog.setMessage("E-mail do usuário");
        alertDialog.setCancelable(false);

        //edit text
        final EditText edittext = new EditText(MainActivity.this);
        alertDialog.setView(edittext);

        //botoes
        alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String emaiContato = edittext.getText().toString();

                //validar se o e-mail foi digitado
                if (emaiContato.isEmpty()){
                    Toast.makeText(MainActivity.this,"Preencha o e-mail",Toast.LENGTH_LONG).show();
                }else {
                    //verificar se usuario ja está cadastrado no app
                    identificadorContato = Base64Custom.codificarBase64(emaiContato);

                    //recuperar instancia firebase
                    firebaseDataBase = ConfiguracaoFirebase.getFirebase().child("usuarios").child(identificadorContato);
                    firebaseDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null){

                                //recuperar os dados a ser adicionado
                                Usuario usuarioContato = dataSnapshot.getValue(Usuario.class);

                                //aqui vai salvar os dados no celular
                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String identificadorUsuarioLogado = preferencias.getIdentificador();

                                firebaseDataBase = ConfiguracaoFirebase.getFirebase();
                                firebaseDataBase = firebaseDataBase.child("contatos")
                                                                   .child(identificadorUsuarioLogado)
                                                                   .child(identificadorContato);

                                Contato contato = new Contato();
                                contato.setIdentificadorUsuario(identificadorContato);
                                contato.setEmail(usuarioContato.getEmail());
                                contato.setNome(usuarioContato.getNome());

                                firebaseDataBase.setValue(contato);

                            }else {
                                Toast.makeText(MainActivity.this,"Usuario não cadastrado.",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.create();
        alertDialog.show();

    }

    private void deslogarUsuarios() {
        firebase.signOut();
        Intent intent = new Intent(getBaseContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
