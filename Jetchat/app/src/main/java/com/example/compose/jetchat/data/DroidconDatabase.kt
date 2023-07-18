package com.example.compose.jetchat.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

/* Following this guide
 * https://developer.android.com/training/data-storage/sqlite
 */

/** Database schema for droidcon sessions */
object DroidconContract {
    // Table contents are grouped together in an anonymous object.
    object SessionEntry : BaseColumns {
        const val TABLE_NAME = "sessions"
        const val COLUMN_NAME_SUBJECT = "subject"
        const val COLUMN_NAME_DESCRIPTION = "description"
    }
    object FavoriteEntry : BaseColumns {
        const val TABLE_NAME = "favorites"
        const val COLUMN_NAME_ISFAVORITE = "is_favorite"
    }
}

private const val SQL_CREATE_SESSION_ENTRIES =
    "CREATE TABLE ${DroidconContract.SessionEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} TEXT PRIMARY KEY," +
            "${DroidconContract.SessionEntry.COLUMN_NAME_SUBJECT} TEXT," +
            "${DroidconContract.SessionEntry.COLUMN_NAME_DESCRIPTION} TEXT)"

private const val SQL_DELETE_SESSION_ENTRIES = "DROP TABLE IF EXISTS ${DroidconContract.SessionEntry.TABLE_NAME}"

private const val SQL_CREATE_FAVORITE_ENTRIES =
    "CREATE TABLE ${DroidconContract.FavoriteEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} TEXT PRIMARY KEY," +
            "${DroidconContract.FavoriteEntry.COLUMN_NAME_ISFAVORITE} INT)"

private const val SQL_DELETE_FAVORITE_ENTRIES = "DROP TABLE IF EXISTS ${DroidconContract.FavoriteEntry.TABLE_NAME}"


class DroidconDbHelper(var context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_SESSION_ENTRIES)
        db.execSQL(SQL_CREATE_FAVORITE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_SESSION_ENTRIES)
        db.execSQL(SQL_DELETE_FAVORITE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Droidcon.db"
    }
}