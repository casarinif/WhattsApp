package br.com.mitraconsignado.whattsapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.mitraconsignado.whattsapp.Adapter.ConversaAdapter;
import br.com.mitraconsignado.whattsapp.R;
import br.com.mitraconsignado.whattsapp.activity.ConversaActivity;
import br.com.mitraconsignado.whattsapp.config.ConfiguracaoFirebase;
import br.com.mitraconsignado.whattsapp.helper.Base64Custom;
import br.com.mitraconsignado.whattsapp.helper.Preferencias;
import br.com.mitraconsignado.whattsapp.modelo.Contato;
import br.com.mitraconsignado.whattsapp.modelo.Conversa;

public class ConversasFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<Conversa> adapter;
    private ArrayList<Conversa> conversas;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerConversas;

    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        conversas = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.lv_conversas);
        adapter = new ConversaAdapter(getActivity(), conversas);
        listView.setAdapter(adapter);

        // recuperar os contatos do firebase
        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();

        firebase = ConfiguracaoFirebase.getFirebase()
                .child("conversas")
                .child(identificadorUsuarioLogado);

        //listener para recuperar contatos
        valueEventListenerConversas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conversas.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Conversa conversa = dados.getValue(Conversa.class);
                    conversas.add(conversa);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ConversaActivity.class);
                //recuperar dados a serem passados
                Conversa conversa = conversas.get(position);

                //enviando dados para conversa activity
                intent.putExtra("nome", conversa.getNome());
                String email = Base64Custom.decodificarBase64(conversa.getIdUsuario());
                intent.putExtra("email", email);

                startActivity(intent);
            }

        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerConversas);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerConversas);
    }
}
