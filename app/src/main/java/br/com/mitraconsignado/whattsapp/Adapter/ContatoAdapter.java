package br.com.mitraconsignado.whattsapp.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.mitraconsignado.whattsapp.R;
import br.com.mitraconsignado.whattsapp.modelo.Contato;

/**
 * Created by Eduardo on 30/05/2017.
 */

public class ContatoAdapter extends ArrayAdapter<Contato>{

    private ArrayList<Contato> contatos;
    private Context context;

    public ContatoAdapter(Context c, ArrayList<Contato> objects) {
        super(c, 0, objects);
        this.contatos = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //verificar se tem contatos
        if (contatos != null){
            //inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //montar a view apartir do xml
            view = inflater.inflate(R.layout.lista_contatos,parent,false);

            //recuperar elemento para exibição
            TextView nomeContato =(TextView) view.findViewById(R.id.tv_nome);
            TextView emailContato =(TextView) view.findViewById(R.id.tv_email);

            Contato contato = contatos.get(position);
            nomeContato.setText(contato.getNome());
            emailContato.setText(contato.getEmail());
        }

        return view;
    }
}
