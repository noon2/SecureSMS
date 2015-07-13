package com.securesms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.securesms.items.MessageItem;
import com.securesms.items.ReceiverItem;

import java.util.ArrayList;


public class DbAdapter {
    public static final String DATABASE_NAME = "data.db";

    public static final String SQLLITE_TABLE_RECEIVERS = "Receivers";
    public static final String REC_ID = "_id";
    public static final String REC_NAME = "rec_name";
    public static final String REC_NUMBER = "rec_number";
    public static final String REC_CODE = "rec_code";

    public static final String SQLLITE_TABLE_MESSAGES = "Messages";
    public static final String MES_ID = "_id";
    public static final String MES_REC_ID = "mes_rec_id";
    public static final String MES_DATE = "mes_date";
    public static final String MES_TEXT = "mes_text";
    public static final String MES_REC = "mes_rec";
    public static final String MES_READ = "mes_read";

    public static boolean is_open = false;
    public static int DATABASE_VERSION = 6;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private final Context dbContex;

    private static final String DATABASE_RECEIVERS_CREATE = "CREATE TABLE if not exists "
            + SQLLITE_TABLE_RECEIVERS
            + " ("
            + REC_ID
            + " INTEGER PRIMARY KEY autoincrement,"
            + REC_NAME
            + " TEXT,"
            + REC_NUMBER
            + " STRING,"
            + REC_CODE
            + " TEXT);";
    private static final String DATABASE_MESSAGES_CREATE = "CREATE TABLE if not exists "
            + SQLLITE_TABLE_MESSAGES
            + " ("
            + MES_ID
            + " INTEGER PRIMARY KEY autoincrement,"
            + MES_REC_ID
            + " INTEGER,"
            + MES_DATE
            + " TEXT,"
            + MES_TEXT
            + " TEXT,"
            + MES_REC
            + " INTEGER,"
            + MES_READ
            + " INTEGER);";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_RECEIVERS_CREATE);
            db.execSQL(DATABASE_MESSAGES_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + SQLLITE_TABLE_RECEIVERS);
            db.execSQL("DROP TABLE IF EXISTS " + SQLLITE_TABLE_MESSAGES);
            onCreate(db);
        }
    }

    public DbAdapter(Context ctx) {
        this.dbContex = ctx;
    }

    public DbAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(dbContex);
        db = dbHelper.getWritableDatabase();
        is_open = true;
        return this;
    }

    public void close() {
        if (dbHelper != null) {
            is_open = false;
            dbHelper.close();
        }
    }


    public void upgrade(int version) {
        dbHelper.onUpgrade(db, DATABASE_VERSION, version);
    }

    // zadania do tabeli Receiver
    public long createRowReceiver(ReceiverItem t) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(REC_NAME, t.name);
        initialValues.put(REC_NUMBER, t.number);
        initialValues.put(REC_CODE, t.code);
        return db.insert(SQLLITE_TABLE_RECEIVERS, null, initialValues);
    }

    public long deleteRowReceiver(ReceiverItem t) {
        return db.delete(SQLLITE_TABLE_RECEIVERS, REC_ID + " =?",
                new String[]{t.id + ""});
    }

    public long updateRowReceive(ReceiverItem t) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(REC_NAME, t.name);
        initialValues.put(REC_NUMBER, t.number);
        initialValues.put(REC_CODE, t.code);
        return db.update(SQLLITE_TABLE_RECEIVERS, initialValues, REC_ID + " =?",
                new String[]{t.id + ""});
    }

    public ReceiverItem searchRowReceiverId(String id) {
        ReceiverItem wynik = null;
        String[] allColumns = new String[]{REC_ID, REC_NAME,
                REC_NUMBER, REC_CODE};
        Cursor c = db.query(SQLLITE_TABLE_RECEIVERS, allColumns, REC_ID + "= ?",
                new String[]{id}, null, null, null);
        if (c != null && c.moveToFirst()) {
            wynik = new ReceiverItem(c.getInt(0), c.getString(1), c.getString(2),
                    c.getString(3));
            c.close();
        }

        return wynik;
    }

    public ReceiverItem searchRowReceiverNumber(String number) {
        ReceiverItem wynik = null;
        String[] allColumns = new String[]{REC_ID, REC_NAME,
                REC_NUMBER, REC_CODE};
        Cursor c = db.query(SQLLITE_TABLE_RECEIVERS, allColumns, REC_NUMBER + "= ?",
                new String[]{number}, null, null, null);
        if (c != null && c.moveToFirst()) {
            wynik = new ReceiverItem(c.getInt(0), c.getString(1), c.getString(2),
                    c.getString(3));
            c.close();
        }

        return wynik;
    }

    public boolean isReadMessageReceiver(int id) {
        boolean wynik = true;
        String[] allColumns = new String[]{MES_REC_ID, MES_READ};
        Cursor c = db.query(SQLLITE_TABLE_MESSAGES, allColumns, MES_REC_ID + " = ? AND " + MES_READ + " = ?",
                new String[]{id + "", 1 + ""}, null, null, null);
        if (c != null && c.moveToFirst()) {
            if (c.getCount() != 0) {
                wynik = false;
            }
            c.close();
        }
        return wynik;
    }

    // zadania do tabeli Receiver
    public long createRowMessage(MessageItem t) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(MES_REC_ID, t.id_receivers);
        initialValues.put(MES_DATE, t.date);
        initialValues.put(MES_TEXT, t.text);
        initialValues.put(MES_REC, t.rec);
        initialValues.put(MES_READ, t.read);
        return db.insert(SQLLITE_TABLE_MESSAGES, null, initialValues);
    }

    // zadania do tabeli Receiver
    public long setReadMessages(long id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(MES_READ, 0);
        return db.update(SQLLITE_TABLE_MESSAGES, initialValues, MES_REC_ID + " =? AND " + MES_READ + " =?",
                new String[]{id + "", 1 + ""});
    }

    public long deleteRowMessage(MessageItem t) {
        return db.delete(SQLLITE_TABLE_MESSAGES, REC_ID + " =?",
                new String[]{t.id + ""});
    }

    public long updateRowMessage(MessageItem t) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(MES_REC_ID, t.id_receivers);
        initialValues.put(MES_DATE, t.date);
        initialValues.put(MES_TEXT, t.text);
        initialValues.put(MES_REC, t.rec);
        initialValues.put(MES_READ, t.read);
        return db.update(SQLLITE_TABLE_MESSAGES, initialValues, MES_ID + " =?",
                new String[]{t.id + ""});
    }

    public MessageItem searchRowMessageId(int id) {
        MessageItem wynik = null;
        String[] allColumns = new String[]{MES_ID, MES_REC_ID, MES_DATE,
                MES_TEXT, MES_REC, MES_READ};
        Cursor c = db.query(SQLLITE_TABLE_MESSAGES, allColumns, MES_ID + "= ?",
                new String[]{id + ""}, null, null, null);
        if (c != null && c.moveToFirst()) {
            wynik = new MessageItem(c.getInt(0), c.getInt(1), c.getString(2),
                    c.getString(3), c.getInt(4), c.getInt(5));
            c.close();
        }

        return wynik;
    }

    public Cursor searchRowMessageRec(long id) {
        String[] allColumns = new String[]{MES_ID, MES_TEXT, MES_DATE, MES_REC};
        Cursor c = db.query(SQLLITE_TABLE_MESSAGES, allColumns, MES_REC_ID + "= ?", new String[]{id + ""}, null,
                null, MES_DATE);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    public ArrayList<MessageItem> getRowsMessagesRec(long id) {
        ArrayList<MessageItem> list = new ArrayList<MessageItem>();
        String[] allColumns = new String[]{MES_TEXT, MES_DATE, MES_REC};
        Cursor c = db.query(SQLLITE_TABLE_MESSAGES, allColumns, MES_REC_ID + "= ?", new String[]{id + ""}, null,
                null, MES_DATE);
        if (c != null && c.moveToFirst()) {
            MessageItem item = new MessageItem(c.getString(0),c.getString(1),c.getInt(2));
            list.add(item);
            while(c.moveToNext())
            {
                item = new MessageItem(c.getString(0),c.getString(1),c.getInt(2));
                list.add(item);
            }
        }
        return list;
    }

    public Cursor readListMainMessages() {
        String query = "SELECT * FROM Messages m INNER JOIN Receivers r ON r._id=m.mes_rec_id GROUP BY m.mes_rec_id";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor readListReceivers() {
        String[] allColumns = new String[]{REC_ID, REC_NAME, REC_NUMBER, REC_CODE};
        Cursor c = db.query(SQLLITE_TABLE_RECEIVERS, allColumns, null, null, null,
                null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor readListSelectMessages() {
        String[] allColumns = new String[]{MES_REC_ID, MES_DATE, MES_TEXT};
        Cursor c = db.query(SQLLITE_TABLE_MESSAGES, allColumns, null, null, MES_REC_ID,
                null, MES_DATE);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }


}
