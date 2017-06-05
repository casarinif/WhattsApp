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
import br.com.mitraconsignado.whattsapp.helper.Preferencias;
import br.com.mitraconsignado.whattsapp.modelo.Contato;
import br.com.mitraconsignado.whattsapp.modelo.Mensagem;

/**
 * Created by Eduardo on 31/05/2017.
 */

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private ArrayList<Mensagem> mensagens;
    private Context context;

    public MensagemAdapter(Context c, ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.mensagens = objects;
        this.context = c;
    }

    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {
        View view = null;
        if (mensagens != null){
            //recuperar dados do usuario preenchido
            Preferencias preferencias = new Preferencias(context);
            String idUsuarioRemetente = preferencias.getIdentificador();

            //inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            //recuperar as mensagem
            Mensagem mensagem = mensagens.get(position);
            //montar a view apartir do xml
            if (idUsuarioRemetente.equals(mensagem.getIdUsuario())){
                view = inflater.inflate(R.layout.item_mensagem_direita,parent,false);
            }else {
                view = inflater.inflate(R.layout.item_mensagem_esquerda,parent,false);
            }

            //recuperar elemento para exibição
            TextView textoMensagem =(TextView) view.findViewById(R.id.tv_mensagem);
            textoMensagem.setText(mensagem.getMensagem());
        }

        return view;
    }
}
