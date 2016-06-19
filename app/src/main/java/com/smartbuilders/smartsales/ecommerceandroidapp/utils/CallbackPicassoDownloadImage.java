package com.smartbuilders.smartsales.ecommerceandroidapp.utils;

import android.content.Context;
import android.util.Log;

import com.jasgcorp.ids.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by stein on 18/6/2016.
 */
public class CallbackPicassoDownloadImage implements com.squareup.picasso.Callback {

    private String mFileName;
    private Context mContext;
    private User mUser;
    private boolean mIsThumb;

    public CallbackPicassoDownloadImage(String fileName, boolean isThumb, User user, Context context) {
        mFileName = fileName;
        mContext = context;
        mUser = user;
        mIsThumb = isThumb;
    }

    @Override
    public void onSuccess() {
        //Si el archivo no existe entonces se descarga
        //GetFileFromServlet getFileFromServlet =
        //        new GetFileFromServlet(mFileName, mIsThumb, mUser, mContext);
        //try {
        //    if(mIsThumb) {
        //        Utils.createFileInThumbDir(mFileName,
        //                getFileFromServlet.execute().get(),
        //                mUser, mContext);
        //    }else{
        //        Utils.createFileInOriginalDir(mFileName,
        //                getFileFromServlet.execute().get(),
        //                mUser, mContext);
        //    }
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //} catch (ExecutionException e) {
        //    e.printStackTrace();
        //}
        new DownloadAndCreateImage(/*mFileName, mIsThumb, mUser, mContext*/).start();
    }

    @Override
    public void onError() {

    }

    class DownloadAndCreateImage extends Thread {
        //private String mFileName;
        //private Context mContext;
        //private User mUser;
        //private boolean mIsThumb;
        //
        //public DownloadAndCreateImage(String fileName, boolean isThumb, User user, Context context) {
        //    mFileName = fileName;
        //    mContext = context;
        //    mUser = user;
        //    mIsThumb = isThumb;
        //}

        public void run() {
            downloadImage(mFileName, mContext, mUser, mIsThumb);
        }

        // Creates Bitmap from InputStream and returns it
        private void downloadImage(String fileName, Context context, User user, boolean isThumb) {
            try {
                String url = user.getServerAddress() + "/IntelligentDataSynchronizer/"
                        + (isThumb ? "GetThumbImage" : "GetOriginalImage")
                        + "?fileName=" + fileName;
                OutputStream outputStream = null;
                InputStream inputStream = null;
                try {
                    inputStream = getHttpConnection(url);
                    // write the inputStream to a FileOutputStream
                    outputStream =
                            new FileOutputStream(new File(new StringBuffer(context.getExternalFilesDir(null).toString())
                                    .append(File.separator).append(user.getUserGroup()).append(File.separator)
                                    .append(user.getUserName()).append(isThumb ? "/Data_In/thumb/" : "/Data_In/original/")
                                    .append(fileName).toString()));
                    int read = 0;
                    byte[] bytes = new byte[1024];
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            // outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (SocketTimeoutException e) {
                Log.w("DownloadAndCreateImage", "SocketTimeoutException, " + e.getMessage());
            } catch (SocketException e) {
                Log.w("DownloadAndCreateImage", "SocketException, " + e.getMessage());
            } catch(MalformedURLException e){
                Log.e("DownloadAndCreateImage", "MalformedURLException, " + e.getMessage());
            } catch (IOException e) {
                Log.e("DownloadAndCreateImage", "IOException, " + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString) throws Exception {
            try {
                HttpURLConnection httpConnection = (HttpURLConnection) (new URL(urlString)).openConnection();
                httpConnection.setConnectTimeout(1200);
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();
                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return httpConnection.getInputStream();
                } else {
                    Log.w("DownloadAndCreateImage", "httpConnection.getResponseCode(): " + httpConnection.getResponseCode());
                    throw new Exception("httpConnection.getResponseCode(): " + httpConnection.getResponseCode());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
    }
}