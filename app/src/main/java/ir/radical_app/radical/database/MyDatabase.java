package ir.radical_app.radical.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.models.MessageModel;

public class MyDatabase extends SQLiteOpenHelper {

    private static final String dbName="RadicalDB";
    private static final int dbVersion=1;
    private static final String messageDB = "messages";
    private static final String CategoryDB = "categories";

    private static final String messageDBQuery = "CREATE TABLE messages (id INTEGER primary key autoincrement, sender text, receiver text,message text, title text, date text)";
    private static final String categoriesDBQuery = "CREATE TABLE categories (id INTEGER primary key autoincrement, category_id INTEGER,title text)";

    public MyDatabase(@Nullable Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(messageDBQuery);
        db.execSQL(categoriesDBQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public void deleteMessageTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM messages");
    }
    public void deleteCategoriesTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM categories");
    }

    public void saveMessage(MessageModel messageModel){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sender",messageModel.getSender());
        cv.put("receiver",messageModel.getReceiver());
        cv.put("message",messageModel.getMessage());
        cv.put("title",messageModel.getTitle());
        cv.put("date",messageModel.getDate());
        db.insert(messageDB,null,cv);
    }
    public void saveCategories(ArrayList<JsonResponse> list){
        SQLiteDatabase db = getWritableDatabase();
        for(int i=0;i<list.size();i++){
            ContentValues cv = new ContentValues();
            JsonResponse jsonResponse = list.get(i);
            cv.put("title",jsonResponse.getName());
            cv.put("category_id",jsonResponse.getCategoryId());
            db.insert(CategoryDB,null,cv);
        }


    }

    public ArrayList<MessageModel> getMessages(){
        ArrayList<MessageModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM messages where sender=='مدیر' order by id DESC",null,null);
        if(cursor.moveToFirst()){
            do{
                MessageModel model = new MessageModel();
                model.setMessage(cursor.getString(cursor.getColumnIndex("message")));
                model.setSender(cursor.getString(cursor.getColumnIndex("sender")));
                model.setReceiver(cursor.getString(cursor.getColumnIndex("receiver")));
                model.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                model.setDate(cursor.getString(cursor.getColumnIndex("date")));
                list.add(model);
            }while (cursor.moveToNext());
        }
        return list;
    }
    public ArrayList<MessageModel> getChatMessages(){
        ArrayList<MessageModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM messages where sender!='مدیر' order by id ASC",null,null);
        if(cursor.moveToFirst()){
            do{
                MessageModel model = new MessageModel();
                model.setMessage(cursor.getString(cursor.getColumnIndex("message")));
                model.setSender(cursor.getString(cursor.getColumnIndex("sender")));
                model.setReceiver(cursor.getString(cursor.getColumnIndex("receiver")));
                model.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                model.setDate(cursor.getString(cursor.getColumnIndex("date")));
                list.add(model);
            }while (cursor.moveToNext());
        }
        return list;
    }
    public ArrayList<JsonResponse> getCategories(){
        ArrayList<JsonResponse> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categories",null,null);
        if(cursor.moveToFirst()){
            do{
                JsonResponse model = new JsonResponse();
                model.setName(cursor.getString(cursor.getColumnIndex("title")));
                model.setCategoryId(cursor.getString(cursor.getColumnIndex("category_id")));
                list.add(model);
            }while (cursor.moveToNext());
        }
        return list;
    }
}
