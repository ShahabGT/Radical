package ir.radical_app.radical.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ir.radical_app.radical.models.JsonResponse
import ir.radical_app.radical.models.MessageModel

class MyDatabase constructor(context: Context): SQLiteOpenHelper(context,"RadicalDB",null,1){
    private val messageDB = "messages"
    private val supportDB = "support"
    private val categoryDB = "categories"

    private val messageDBQuery = "CREATE TABLE messages (id INTEGER primary key autoincrement, sender text, receiver text,message text, title text, date text, read INTEGER)"
    private val supportDBQuery = "CREATE TABLE support (id INTEGER primary key autoincrement, sender text, receiver text,message text, title text, date text,read INTEGER)"
    private val categoriesDBQuery = "CREATE TABLE categories (id INTEGER primary key autoincrement, category_id INTEGER,title text)"


    override fun onCreate(db: SQLiteDatabase?) {
      db?.execSQL(messageDBQuery)
      db?.execSQL(supportDBQuery)
      db?.execSQL(categoriesDBQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun deleteSupportTable(){
        val db = writableDatabase
        db.execSQL("DELETE FROM $supportDB")
    }
    fun updateSupportDB(){
        val db = writableDatabase
        db.execSQL("update $supportDB set read=1 where read=0")
    }

    fun deleteMessageTable(){
        val db = writableDatabase
        db.execSQL("DELETE FROM $messageDB")
    }
    fun readMessage(id:String){
        val db=writableDatabase
        db.execSQL("update $messageDB set read=1 where id=$id")
    }

    fun deleteCategoriesTable(){
        val db = writableDatabase
        db.execSQL("DELETE FROM $categoryDB")
    }

    fun saveMessage(messageModel:MessageModel){
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("sender",messageModel.sender);
        cv.put("receiver",messageModel.receiver);
        cv.put("message",messageModel.message);
        cv.put("title",messageModel.title);
        cv.put("date",messageModel.date);
        cv.put("read",0);
        db.insert(messageDB,null,cv)
    }

    fun saveSupport(messageModel:MessageModel){
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("sender",messageModel.sender);
        cv.put("receiver",messageModel.receiver);
        cv.put("message",messageModel.message);
        cv.put("title",messageModel.title);
        cv.put("date",messageModel.date);
        cv.put("read",messageModel.read);
        db.insert(supportDB,null,cv)
    }

    fun saveCategories(list : ArrayList<JsonResponse>){
        val db = writableDatabase
        for(i in 0 until list.size){
            val cv = ContentValues()
            val jsonResponse = list[i]
            cv.put("title",jsonResponse.name);
            cv.put("category_id",jsonResponse.categoryId);
            db.insert(categoryDB,null,cv)
        }
    }

    fun getMessages():ArrayList<MessageModel>{
        val list = ArrayList<MessageModel>()
        val db = readableDatabase
        val cursor:Cursor=db.rawQuery("SELECT * FROM $messageDB order by id DESC",null,null)
        if(cursor.moveToFirst()){
            do{
                val model =MessageModel()
                model.id=cursor.getInt(cursor.getColumnIndex("id")).toString()
                model.read=cursor.getInt(cursor.getColumnIndex("read")).toString()
                model.message=cursor.getString(cursor.getColumnIndex("message"))
                model.sender=cursor.getString(cursor.getColumnIndex("sender"))
                model.receiver=cursor.getString(cursor.getColumnIndex("receiver"))
                model.title=cursor.getString(cursor.getColumnIndex("title"))
                model.date=cursor.getString(cursor.getColumnIndex("date"))
                list.add(model)
            }while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getReadMessages():Int{
        val db = readableDatabase
        val cursor:Cursor=db.rawQuery("SELECT COUNT(*) AS count FROM $messageDB where read=0;",null,null)
        return if(cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndex("count")) else 0
    }

    fun getChatMessages():ArrayList<MessageModel>{
        val list = ArrayList<MessageModel>()
        val db = readableDatabase
        val cursor:Cursor=db.rawQuery("SELECT * FROM $supportDB order by id ASC",null,null)
        if(cursor.moveToFirst()){
            do{
                val model=MessageModel()
                model.message=cursor.getString(cursor.getColumnIndex("message"))
                model.sender=cursor.getString(cursor.getColumnIndex("sender"))
                model.receiver=cursor.getString(cursor.getColumnIndex("receiver"))
                model.title=cursor.getString(cursor.getColumnIndex("title"))
                model.date=cursor.getString(cursor.getColumnIndex("date"))
                list.add(model)
            }while (cursor.moveToNext())
        }
        cursor.close()
        updateSupportDB()
        return list
    }

    fun getReadSupport():Int{
        val db = readableDatabase
        val cursor:Cursor=db.rawQuery("SELECT COUNT(*) AS count FROM $supportDB where read=0;",null,null)
        return if(cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndex("count")) else 0
    }

    fun getCategories():ArrayList<JsonResponse>{
        val list = ArrayList<JsonResponse>()
        val db = readableDatabase
        val cursor:Cursor=db.rawQuery("SELECT * FROM $categoryDB",null,null)
        if(cursor.moveToFirst()){
            do{
                val model= JsonResponse()
                model.name = cursor.getString(cursor.getColumnIndex("title"))
                model.categoryId = cursor.getString(cursor.getColumnIndex("category_id"))
                list.add(model)

            }while (cursor.moveToNext())

        }
        cursor.close()
        return list
    }

}