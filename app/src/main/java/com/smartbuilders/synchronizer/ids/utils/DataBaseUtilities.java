package com.smartbuilders.synchronizer.ids.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.text.TextUtils;
import android.util.SparseArray;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;

import net.iharder.Base64;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by stein on 21/7/2016.
 */
public class DataBaseUtilities {

    /**
     *
     * @param user
     * @param sql
     * @return
     */
    public static Object getJsonBase64CompressedQueryResult(Context context, User user, String sql){
        ArrayList<String> preview;
        ArrayList<String> result;
        Cursor cursor = null;
        int batchMaxLength = 9000;
        try {
            if (user==null) {
                cursor = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI,
                        null, sql, null, null);
            } else {
                cursor = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                        null, sql, null, null);
            }
            result = new ArrayList<>();
            preview = new ArrayList<>();
            if(cursor != null){
                int columnCount = cursor.getColumnCount();

                JSONObject json = new JSONObject();
                for(int i = 0; i < columnCount; i++){
                    try {
                        json.put(String.valueOf(i), cursor.getColumnName(i));
                    } catch(NullPointerException e){
                        json.remove(String.valueOf(i));
                    } catch (JSONException e) {	}
                }
                preview.add(json.toString());

                //for(int i = 0; i < columnCount; i++){
                //    try {
                //        json.put(String.valueOf(i), rs.getMetaData().getColumnType(i));
                //    } catch(NullPointerException e){
                //        json.remove(String.valueOf(i));
                //    } catch (JSONException e) {	}
                //}
                //preview.add(json.toString());
                while (cursor.moveToNext()){
                    for(int i = 0; i < columnCount; i++){
                        try {
                            if(cursor.getString(i)==null){
                                json.remove(String.valueOf(i));
                            }else{
                                json.put(String.valueOf(i), cursor.getString(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    preview.add(json.toString());
                    //Si el tamano del archivo es mayor a batchMaxLength Bytes
                    if(preview.toString().getBytes("UTF-8").length>batchMaxLength){
                        if(result.isEmpty()){
                            result.add((new StringBuilder("{\"")).append(result.size()).append("\":\"")
                                    .append(Base64.encodeBytes(gzip(preview.toString()), Base64.GZIP)).append("\"").toString());
                        }else{
                            result.add((new StringBuilder("\"")).append(result.size()).append("\":\"")
                                    .append(Base64.encodeBytes(gzip(preview.toString()), Base64.GZIP)).append("\"").toString());
                        }
                        preview.clear();
                    }
                }
            }
            if(result.isEmpty()){
                result.add((new StringBuilder("{\"")).append(result.size()).append("\":\"")
                        .append(Base64.encodeBytes(gzip(preview.toString()), Base64.GZIP)).append("\"}").toString());
            }else{
                result.add((new StringBuilder("\"")).append(result.size()).append("\":\"")
                        .append(Base64.encodeBytes(gzip(preview.toString()), Base64.GZIP)).append("\"}").toString());
            }
            return Base64.encodeBytes(gzip(result.toString()), Base64.GZIP);
        } catch(Exception e) {
            e.printStackTrace();
            return e;
        } finally {
            try {
                if(cursor != null){
                    cursor.close();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * comprime un String y devuelve un byte[]
     * @param s
     * @return
     * @throws Exception
     */
    public static byte[] gzip(String s) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        OutputStreamWriter osw = new OutputStreamWriter(gzip, "UTF-8");
        osw.write(s);
        osw.close();
        return bos.toByteArray();
    }

    /**
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String unGzip(byte[] bytes) throws Exception{
        InputStreamReader isr = null;
        GZIPInputStream gzipInputStream = null;
        try {
            gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
            isr = new InputStreamReader(gzipInputStream, "UTF-8");
            StringWriter sw = new StringWriter();
            char[] chars = new char[1024];
            for (int len; (len = isr.read(chars)) > 0; ) {
                sw.write(chars, 0, len);
            }
            return sw.toString();
        } finally {
            if (isr!=null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
            if (gzipInputStream!=null) {
                try {
                    gzipInputStream.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }
    }

    /**
     * devuelve la primera palabra en un String,
     * obviando espacios en blanco a la izquierda y derecha
     * @param string
     * @return
     * @throws NullPointerException
     */
    private static String firstWord(String string) throws NullPointerException{
        return string.trim().split("\\s+")[0]; //add " " to string to be sure there is something to split
    }

    ///**
    // *
    // * @param sqLiteDatabase
    // * @param data
    // * @param validSequenceIds
    // * @param tableName
    // * @throws Exception
    // */
    //public static void insertDataFromWSResultData(SQLiteDatabase sqLiteDatabase, String data, String validSequenceIds,
    //                                              String tableName) throws Exception {
    //    int counterEntireCompressedData = 0;
    //    int counter;
    //    JSONArray jsonArray = new JSONArray(unGzip(Base64.decode(data, Base64.GZIP)));
    //    Iterator<?> keys = jsonArray.getJSONObject(counterEntireCompressedData).keys();
    //    if(keys.hasNext()){
    //        int columnCount = 0;
    //        JSONArray jsonArray2 = new JSONArray(unGzip(Base64.decode(jsonArray
    //                .getJSONObject(counterEntireCompressedData).getString((String)keys.next()), Base64.GZIP)));
    //        StringBuilder insertSentence = new StringBuilder("INSERT OR REPLACE INTO ").append(tableName).append(" (");
    //        try{
    //            counter = 0;
    //            Iterator<?> keysTemp = jsonArray2.getJSONObject(counter).keys();
    //            while(keysTemp.hasNext()){
    //                if(columnCount==0){
    //                    insertSentence.append(jsonArray2.getJSONObject(counter).getString((String) keysTemp.next()));
    //                } else {
    //                    insertSentence.append(", ").append(jsonArray2.getJSONObject(counter).getString((String) keysTemp.next()));
    //                }
    //                columnCount++;
    //            }
    //            insertSentence.append(") VALUES (");
    //            for (int i = 0; i<columnCount; i++) {
    //                insertSentence.append((i==0) ? "?" : ", ?");
    //            }
    //            insertSentence.append(")");
    //        } catch (Exception e){
    //            e.printStackTrace();
    //        }
    //
    //        SQLiteStatement statement = null;
    //        try {
    //            statement = sqLiteDatabase.compileStatement(insertSentence.toString());
    //            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
    //                sqLiteDatabase.beginTransactionNonExclusive();
    //            } else {
    //                sqLiteDatabase.beginTransaction();
    //            }
    //            counter = 1;
    //            Iterator<?> keysTemp;
    //            String key;
    //            //Se itera a traves de la data
    //            while (counter <= jsonArray2.length()) {
    //                if (++counter >= jsonArray2.length()) {
    //                    if (keys.hasNext()) {
    //                        counter = 0;
    //                        jsonArray2 = new JSONArray(unGzip(Base64.decode(jsonArray
    //                                .getJSONObject(counterEntireCompressedData).getString((String) keys.next()), Base64.GZIP)));
    //                        if (jsonArray2.length() < 1) {
    //                            break;
    //                        }
    //                    } else {
    //                        if (++counterEntireCompressedData >= jsonArray.length()) {
    //                            break;
    //                        } else {
    //                            counter = 0;
    //                            keys = jsonArray.getJSONObject(counterEntireCompressedData).keys();
    //                            jsonArray2 = new JSONArray(unGzip(Base64.decode(jsonArray
    //                                    .getJSONObject(counterEntireCompressedData).getString((String) keys.next()), Base64.GZIP)));
    //                            if (jsonArray2.length() < 1) {
    //                                break;
    //                            }
    //                        }
    //                    }
    //                }
    //                //Se prepara la data que se insertara
    //                statement.clearBindings();
    //                keysTemp = jsonArray2.getJSONObject(counter).keys();
    //                while(keysTemp.hasNext()){
    //                    key = (String) keysTemp.next();
    //                    statement.bindString(Integer.valueOf(key), jsonArray2.getJSONObject(counter).getString(key));
    //                }
    //                statement.executeInsert();
    //                //Fin de preparacion de la data que se insertara
    //            }
    //            //la condicion de SEQUENCE_ID <> 0 es para que no elimine los registro que no se han sincronizado
    //            sqLiteDatabase.delete(tableName, TextUtils.isEmpty(validSequenceIds) ? null
    //                    : ("SEQUENCE_ID <> 0 AND SEQUENCE_ID NOT IN (" +
    //                            unGzip(Base64.decode(validSequenceIds, Base64.GZIP)) + ")"),
    //                    null);
    //            sqLiteDatabase.setTransactionSuccessful();
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            throw e;
    //        } finally {
    //            if(statement!=null) {
    //                try {
    //                    statement.close();
    //                } catch (Exception e) {
    //                    e.printStackTrace();
    //                }
    //            }
    //            if(sqLiteDatabase!=null) {
    //                try {
    //                    sqLiteDatabase.endTransaction();
    //                } catch (Exception e) {
    //                    e.printStackTrace();
    //                }
    //            }
    //        }
    //    }
    //}

    /**
     *
     * @return
     * @throws Exception
     * @throws IOException
     * @throws JSONException
     */
    public static Cursor parseJsonCursorToCursor(String data) throws Exception{
        MatrixCursor cursor = null;
        int counterEntireCompressedData = 0;
        int counter = 0;
        JSONArray jsonArray = new JSONArray(unGzip(Base64.decode(data, Base64.GZIP)));
        Iterator<?> keys = jsonArray.getJSONObject(counterEntireCompressedData).keys();
        if(keys.hasNext()){
            int columnCount = 0;
            Object columnValues[];
            JSONArray jsonArray2 = new JSONArray(unGzip(Base64.decode(jsonArray
                    .getJSONObject(counterEntireCompressedData).getString((String)keys.next()), Base64.GZIP)));
            HashMap<String, String> colsIndex;
            SparseArray<String> colsType;

            //MetadaData
            try{
                ArrayList<String> columnNames = new ArrayList<>();
                //Se carga la metadata de los indices de las columnas consultadas
                Iterator<?> keysTemp = jsonArray2.getJSONObject(counter).keys();
                colsIndex = new HashMap<>();
                while(keysTemp.hasNext()){
                    columnCount++;
                    String key = (String) keysTemp.next();
                    colsIndex.put(jsonArray2.getJSONObject(counter).getString(key), key);
                    columnNames.add(jsonArray2.getJSONObject(counter).getString(key));
                }
                cursor = new MatrixCursor(columnNames.toArray(new String[0]));
                columnValues = new Object[columnCount];

                /************************************************************************/
                counter = 1;
                //Se carga la metadata de los tipos de columnas consultadas
                keysTemp = jsonArray2.getJSONObject(counter).keys();
                colsType = new SparseArray<>();
                while(keysTemp.hasNext()){
                    String key = (String) keysTemp.next();
                    colsType.put(Integer.valueOf(key), jsonArray2.getJSONObject(counter).getString(key));
                }
                /************************************************************************/

                //Query Result
                int columnIndex, rowIndex;
                while(counter<=jsonArray2.length()){
                    if(++counter>=jsonArray2.length()){
                        if(keys.hasNext()){
                            counter = 0;
                            jsonArray2 = new JSONArray(unGzip(Base64.decode(jsonArray
                                    .getJSONObject(counterEntireCompressedData).getString((String)keys.next()), Base64.GZIP)));
                            if(jsonArray2.length()<1){
                                break;
                            }
                        }else{
                            if(++counterEntireCompressedData>=jsonArray.length()){
                                break;
                            }else{
                                counter = 0;
                                keys = jsonArray.getJSONObject(counterEntireCompressedData).keys();
                                jsonArray2 = new JSONArray(unGzip(Base64.decode(jsonArray
                                        .getJSONObject(counterEntireCompressedData).getString((String)keys.next()), Base64.GZIP)));
                                if(jsonArray2.length()<1){
                                    break;
                                }
                            }
                        }
                    }

                    for(columnIndex=1,rowIndex=0; columnIndex<=columnCount; columnIndex++,rowIndex++){
                        try{
                            //TODO: castear al tipo de dato correspondiente
                            columnValues[rowIndex] = jsonArray2.getJSONObject(counter).getString(String.valueOf(columnIndex));
                        }catch(Exception e){
                            columnValues[rowIndex] = null;
                        }
                    }
                    cursor.addRow(columnValues);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return cursor;
    }
}
