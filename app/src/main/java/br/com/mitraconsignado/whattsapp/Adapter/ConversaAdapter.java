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
import br.com.mitraconsignado.whattsapp.modelo.Conversa;

/**
 * Created by Eduardo on 05/06/2017.
 */

public class ConversaAdapter extends ArrayAdapter<Conversa> {

    private ArrayList<Conversa> conversas;
    private Context context;

    public ConversaAdapter(Context c,ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.conversas = objects;
        this.context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        //verificar se tem contatos
        if (conversas != null){
            //inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //montar a view apartir do xml
            view = inflater.inflate(R.layout.lista_mensagem,parent,false);

            //recuperar elemento para exibição
            TextView nomeContato =(TextView) view.findViewById(R.id.tv_titulo);
            TextView mensagem =(TextView) view.findViewById(R.id.tv_subtitulo);

            Conversa conversa = conversas.get(position);
            nomeContato.setText(conversa.getNome());
            mensagem.setText(conversa.getMensagem());

    }
        return view;
    }
}
