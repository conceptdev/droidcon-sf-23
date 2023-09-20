package com.example.compose.jetchat.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log


class HistoryContract {
    object EmbeddingEntry : BaseColumns {
        const val TABLE_NAME = "embedding"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_MESSAGE = "message"
        const val COLUMN_NAME_VECTOR = "vector"
    }
}

//-- Embeddings
private const val SQL_CREATE_EMBEDDING_ENTRIES =
    "CREATE TABLE ${HistoryContract.EmbeddingEntry.TABLE_NAME} (" +
            "${HistoryContract.EmbeddingEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY," +
            "${HistoryContract.EmbeddingEntry.COLUMN_NAME_MESSAGE} TEXT," +
            "${HistoryContract.EmbeddingEntry.COLUMN_NAME_VECTOR} TEXT)"

private const val SQL_DELETE_EMBEDDING_ENTRIES = "DROP TABLE IF EXISTS ${HistoryContract.EmbeddingEntry.TABLE_NAME}"


class HistoryDbHelper (var context: Context?) : SQLiteOpenHelper(context,
    HistoryDbHelper.DATABASE_NAME, null,
    HistoryDbHelper.DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_EMBEDDING_ENTRIES)
        Log.v("LLM-EH", "## History database created")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_EMBEDDING_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 4
        const val DATABASE_NAME = "History.db"
    }
}
