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

import br.com.mitraconsignado.whattsapp.Adapter.MensagemAdapter;
import br.com.mitraconsignado.whattsapp.R;
import br.com.mitraconsignado.whattsapp.config.ConfiguracaoFirebase;
import br.com.mitraconsignado.whattsapp.helper.Base64Custom;
import br.com.mitraconsignado.whattsapp.helper.Preferencias;
import br.com.mitraconsignado.whattsapp.modelo.Conversa;
import br.com.mitraconsignado.whattsapp.modelo.Mensagem;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton botao;
    private DatabaseReference firebase;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenerMensagem;

    //dados do destinatario
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //dados do remetente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;

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
        nomeUsuarioRemetente = preferencias.getINOME();

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
        adapter = new MensagemAdapter(ConversaActivity.this,mensagens);
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
                    mensagens.add(mensagem);
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

                    //vamos salvar as mensagens do remetente
                   Boolean retornoMensagemRemetente = salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario, mensagem);
                    if (!retornoMensagemRemetente){
                        Toast.makeText(ConversaActivity.this,"Problema ao salvar mensagem, tente novamente",
                                Toast.LENGTH_LONG).show();
                    }else {
                        //vamos salvar as mensagens do destinatario
                        Boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestinatario,idUsuarioRemetente, mensagem);
                        if (!retornoMensagemDestinatario){
                            Toast.makeText(ConversaActivity.this,"Problema ao enviar mensagem para o Destinátario, tente novamente",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    //vamos salvar as conversa do remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(textoMensagem);

                    Boolean retornoConversaRemetente = salvarConversas(idUsuarioRemetente,idUsuarioDestinatario,conversa );
                    if (!retornoConversaRemetente){
                        Toast.makeText(ConversaActivity.this,"Problema ao salvar conversa, tente novamente",
                                Toast.LENGTH_LONG).show();
                    }else {
                        //vamos salvar as mensagens do destinatario
                        conversa = new Conversa();
                        conversa.setIdUsuario(idUsuarioRemetente);
                        conversa.setNome(nomeUsuarioRemetente);
                        conversa.setMensagem(textoMensagem);

                        Boolean retornoConversaDestinatario = salvarConversas(idUsuarioDestinatario, idUsuarioRemetente, conversa);
                        if (!retornoConversaDestinatario) {
                            Toast.makeText(ConversaActivity.this, "Problema ao enviar conversa para o Destinátario, tente novamente",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    editMensagem.setText("");
                }
            }
        });

    }

    private boolean salvarConversas(String idRemetente, String idDestinatario, Conversa conversa){
        try{
            firebase = ConfiguracaoFirebase.getFirebase().child("conversas");
            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .setValue(conversa);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
      }
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
