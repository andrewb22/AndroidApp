package net.FitnessDuo.combination2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static DatabaseHelper mInstance = null;

    private static final String TABLE_NAME = "photos_dates_weights_table";
    private static final String COL0 = "ID";
    private static final String COL1 = "filenames";
    private static final String COL2 = "dates";
    private static final String COL3 = "weights";

    public static DatabaseHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private DatabaseHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL1 + " TEXT, " + COL2 + " CLOB, " + COL3 + " TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String item1, Long item2, String item3){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, item1);
        contentValues.put(COL2, item2);
        contentValues.put(COL3, item3);

        Log.d(TAG, "addData: Adding " + item1 + ", " + item2 + ", " + item3 + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //If data is inserted incorrectly it will return -1
        if(result==-1){
            return false;
        } else {
            return true;
        }
    }

    //Returns all the data from the database
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void deleteEntry(int id, String imagePath, Long date, String weight){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL0 + " = '" + id + "'" +
                " AND " + COL1 + " = '" + imagePath + "'" +
                " AND " + COL2 + " = '" + date + "'" +
                " AND " + COL3 + " = '" + weight + "'";
        Log.d(TAG, "deleteEntry: query: " + query);
        Log.d(TAG, "deleteEntry: Deleting " + imagePath + ", " + date + ", " + weight + " from database.");
        db.execSQL(query);
    }

    public Cursor getItemID(String filePathText){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL0 + " FROM " + TABLE_NAME +
                " WHERE " + COL1 + " = '" + filePathText + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
