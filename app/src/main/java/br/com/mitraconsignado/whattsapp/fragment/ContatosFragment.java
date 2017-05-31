package br.com.mitraconsignado.whattsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.mitraconsignado.whattsapp.Adapter.ContatoAdapter;
import br.com.mitraconsignado.whattsapp.R;
import br.com.mitraconsignado.whattsapp.activity.ConversaActivity;
import br.com.mitraconsignado.whattsapp.config.ConfiguracaoFirebase;
import br.com.mitraconsignado.whattsapp.helper.Preferencias;
import br.com.mitraconsignado.whattsapp.modelo.Contato;


public class ContatosFragment extends Fragment {


    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contato> contatos;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContatos;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerContatos);
        Log.i("ValueEvent","onStart" );
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerContatos);
        Log.i("ValueEvent","onStop" );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // instaciar o array list
        contatos = new ArrayList<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        listView = (ListView) view.findViewById(R.id.lv_contatos);
//        adapter = new ArrayAdapter(
//                getActivity(),
//                R.layout.lista_contatos,
//                contatos
//        );

        adapter = new ContatoAdapter(getActivity(),contatos);

        listView.setAdapter(adapter);


        // recuperar os contatos do firebase
        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();

        firebase = ConfiguracaoFirebase.getFirebase()
                .child("contatos")
                .child(identificadorUsuarioLogado);
        //listener para recuperar contatos
        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contatos.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Contato contato = dados.getValue(Contato.class);
                    contatos.add(contato);
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
                Contato contato = contatos.get(position);

                //enviando dados para conversa activity
                intent.putExtra("nome",contato.getNome());
                intent.putExtra("email",contato.getEmail());

                startActivity(intent);
            }

        });

        return view;
    }

}
