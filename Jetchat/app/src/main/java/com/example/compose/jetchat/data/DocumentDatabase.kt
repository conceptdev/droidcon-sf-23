package com.example.compose.jetchat.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log

/* Following this guide
 * https://developer.android.com/training/data-storage/sqlite
 */

/** Database schema for documents */
object DocumentContract {
    // Table contents are grouped together in an anonymous object.

    object EmbeddingEntry : BaseColumns {
        const val TABLE_NAME = "embedding"
        const val COLUMN_NAME_CHUNKID = "chunk_id"
        const val COLUMN_NAME_CONTENT = "content"
        const val COLUMN_NAME_VECTOR = "vector"
    }
}

//-- Embeddings
private const val SQL_CREATE_EMBEDDING_ENTRIES =
    "CREATE TABLE ${DocumentContract.EmbeddingEntry.TABLE_NAME} (" +
            "${DocumentContract.EmbeddingEntry.COLUMN_NAME_CHUNKID} TEXT PRIMARY KEY," +
            "${DocumentContract.EmbeddingEntry.COLUMN_NAME_CONTENT} TEXT," +
            "${DocumentContract.EmbeddingEntry.COLUMN_NAME_VECTOR} TEXT)"

private const val SQL_DELETE_EMBEDDING_ENTRIES = "DROP TABLE IF EXISTS ${DroidconContract.EmbeddingEntry.TABLE_NAME}"



class DocumentDbHelper(var context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_EMBEDDING_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_SESSION_ENTRIES)
        db.execSQL(SQL_DELETE_EMBEDDING_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "DocumentChat.db"
    }

    /** Generates the database schema for the `AskDatabaseFunction` */
    fun generateSimpleSchema(): String {
        val db = readableDatabase
        var out = ""
        // get_table_names
        val tableCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        with(tableCursor) {
            while (moveToNext()) {
                val tableName = getString(0)
                out += "Table: $tableName\nColumns: "
                // get_column_names
                val columnCursor = db.rawQuery("PRAGMA table_info('$tableName');", null)
                var needComma = false
                with(columnCursor) {
                    while (moveToNext()) {
                        val columnName = getString(1)
                        if (needComma) out += ", " else needComma = true
                        out += "$columnName"
                    }
                }
                columnCursor.close()
                out += "\n\n"
            }
        }
        tableCursor.close()
        return out
    }
}