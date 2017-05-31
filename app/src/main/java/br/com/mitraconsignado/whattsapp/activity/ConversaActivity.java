package br.com.mitraconsignado.whattsapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.mitraconsignado.whattsapp.R;
import br.com.mitraconsignado.whattsapp.config.ConfiguracaoFirebase;
import br.com.mitraconsignado.whattsapp.helper.Base64Custom;
import br.com.mitraconsignado.whattsapp.helper.Preferencias;
import br.com.mitraconsignado.whattsapp.modelo.Mensagem;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton botao;
    private DatabaseReference firebase;
    private ListView listView;
    private ArrayList<String> mensagens;
    private ArrayAdapter adapter;
    private ValueEventListener valueEventListenerMensagem;

    //dados do destinatario
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //dados do remetente
    private String idUsuarioRemetente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar)findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        botao = (ImageButton) findViewById(R.id.bt_enviar);
        listView = (ListView) findViewById(R.id.lv_conversas);

        //recuperar os dados do usuario logado
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();

        //Bundle serve para recuperar dados de outra activity
        Bundle extra = getIntent().getExtras();
        if (extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);
        }

        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //montar listview e adapter
        mensagens = new ArrayList<>();
        adapter = new ArrayAdapter(
                ConversaActivity.this,
                android.R.layout.simple_list_item_1,
                mensagens
        );
        listView.setAdapter(adapter);

        //recuperar mensagens do firebase
        firebase = ConfiguracaoFirebase.getFirebase()
                   .child("mensagens")
                   .child(idUsuarioRemetente)
                   .child(idUsuarioDestinatario);
        //criar listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // limpar mensagens
                mensagens.clear();
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem.getMensagem());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerMensagem);


        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMensagem = editMensagem.getText().toString();

                if (textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this,"Digite uma mensagem para enviar",Toast.LENGTH_LONG).show();
                }else {
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                    salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario, mensagem);
                    editMensagem.setText("");
                }
            }
        });

    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem) {
        try{
            firebase = ConfiguracaoFirebase.getFirebase().child("mensagens");
            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}
