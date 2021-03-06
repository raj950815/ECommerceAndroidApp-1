package com.smartbuilders.synchronizer.ids.receivers;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.services.LoadProductsOriginalImage;
import com.smartbuilders.synchronizer.ids.syncadapter.model.AccountGeneral;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerce.services.LoadProductsThumbImage;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

/**
 * Recibe una señal del sistema cuando cambia la conectividad del equipo
 * @author Jesus Sarco
 *
 */
public class NetworkAndBootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if(networkInfo != null){
            try {
                AccountManager accountManager = AccountManager.get(context);
                Account[] accounts = accountManager.getAccountsByType(BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE);
                for(Account account : accounts){
                    //si no se esta realizando la sincronizacion en ese momento entonces se revisa si se hace
                    //la sincronizacion completa o solo de los datos que se sincronizan en tiempo real
                    if(!ApplicationUtilities.isSyncActive(account)){
                        try {
                            //si no requiere la sincronizacion de carga inicial entonces dependiendo del
                            //tipo de red a la que se encuentre conectado se decide si se sincroniza completo
                            //o solo los datos que se sincronizan en tiempo real
                            if(!ApplicationUtilities.appRequireInitialLoad(context, account)){
                                //si esta conectado a una red wifi entonces se revisa si se necesita sincronizacion
                                //completa o solo se envian los datos que se sincronizan en tiempo real
                                //if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                                    // si requiere sincronizacion completa entonces se realiza la sincronizacion completa
                                    if(ApplicationUtilities.appRequireFullSync(context, account)) {
                                        ApplicationUtilities.initSyncByAccount(account);
                                    }else{
                                        //sino se realiza solo la sincronizacion de los datos que requieren
                                        //sincronizacion en tiempo real
                                        ApplicationUtilities.initSyncDataRealTimeWithServerService(context,
                                                accountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));
                                    }
                                //}else{
                                //    //si no esta conectado a una red wifi entonces solo se sincronizan los datos
                                //    //que requieren sincronizacion en tiempo real
                                //    ApplicationUtilities.initSyncDataRealTimeWithServerService(context,
                                //            accountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));
                                //}
                            }else{
                                ApplicationUtilities.initSyncByAccount(account);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
            try {
                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("save_images_in_device", false)
                        && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sync_thumb_images", false)
                        && !Utils.isServiceRunning(context, LoadProductsThumbImage.class)) {
                    context.startService(new Intent(context, LoadProductsThumbImage.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("save_original_images_in_device", false)
                        && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sync_original_images", false)
                        && !Utils.isServiceRunning(context, LoadProductsOriginalImage.class)) {
                    context.startService(new Intent(context, LoadProductsOriginalImage.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //}
        }
    }

}
