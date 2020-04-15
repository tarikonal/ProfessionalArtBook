package com.tarikonal.professionalartbook;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URI;
import java.util.HashMap;

public class ArtContentProvider extends ContentProvider {
    //---------------Provider Operations--------------------------------
    static final String PROVIDER_NAME="com.tarikonal.professionalartbook.ArtContentProvider";
    static final String URL ="content://" + PROVIDER_NAME +"/arts";
    static final Uri CONTENT_URI = Uri.parse(URL);
    static final String NAME="name";
    static final String IMAGE="image";
    static final int ARTS=1;
    static final UriMatcher uriMatcher ;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"arts",ARTS);
    }
    private static HashMap<String,String> ART_PROJECTION_MAP;
    //---------------Provider Operations--------------------------------

    //----------------DATABASE OPERATIONS-------------------------------------------------------------
    private SQLiteDatabase sqLiteDatabase;
    static final String DATABASE_NAME="Arts";
    static final String ARTS_TABLE_NAME="arts";
    static final int DATABASE_VERSION=1;
    static final String CREATE_DATABASE_TABLE="CREATE TABLE "+
            ARTS_TABLE_NAME+"(name TEXT NOT NULL,"+
            "image BLOB NOT NULL)";

    public static class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DATABASE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+ ARTS_TABLE_NAME);
        }
    }
    @Override
    public boolean onCreate() {
        Context context = getContext();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        return sqLiteDatabase!=null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(ARTS_TABLE_NAME);
        switch (uriMatcher.match(uri)){
            case ARTS:
                sqLiteQueryBuilder.setProjectionMap(ART_PROJECTION_MAP);
                break;
            default:
                //TODO
        }
        if(sortOrder==null || sortOrder.matches("")){
                sortOrder=NAME;
        }else{
            //TODO:
        }
        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
       long rowID = sqLiteDatabase.insert(ARTS_TABLE_NAME,"",values);
       if (rowID>0){
           Uri newUri = ContentUris.withAppendedId(CONTENT_URI,rowID);
           getContext().getContentResolver().notifyChange(newUri,null);
           return newUri;
       }
        throw new SQLException("Insert Error!");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowCount=0;
        switch (uriMatcher.match(uri)){
            case ARTS:
               rowCount = sqLiteDatabase.delete(ARTS_TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Failed delete");
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowCount=0;
        switch (uriMatcher.match(uri)){
            case ARTS:
                rowCount = sqLiteDatabase.update(ARTS_TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Failed update");
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowCount;
    }

    //----------------DATABASE OPERATIONS-------------------------------------------------------------
}
