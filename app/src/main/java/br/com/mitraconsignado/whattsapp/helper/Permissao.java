package br.com.mitraconsignado.whattsapp.helper;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import br.com.mitraconsignado.whattsapp.activity.LoginActivity;

/**
 * Created by Eduardo on 22/05/2017.
 */

public class Permissao {

    public static boolean validaPermisao(LoginActivity activity, String[] permissoes, int requestCode) {

        if (Build.VERSION.SDK_INT >= 23) {
            List<String> listaPermisoes = new ArrayList<String>();

            // percorre as permissoes
            for (String permisao : permissoes) {
                Boolean validaPermissao = ContextCompat.checkSelfPermission(activity, permisao) == PackageManager.PERMISSION_GRANTED;
                if (!validaPermissao) listaPermisoes.add(permisao);
            }
            //caso a lista vazia, não é necessario solicitar permissao
            if (listaPermisoes.isEmpty()) return true;

            String[] novasPermissoes = new String[listaPermisoes.size()];
            listaPermisoes.toArray(novasPermissoes);

            //solicita permissao
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);
        }
        return true;
    }
}
