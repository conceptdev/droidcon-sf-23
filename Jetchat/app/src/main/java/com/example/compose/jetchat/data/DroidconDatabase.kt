package com.example.compose.jetchat.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log

/* Following this guide
 * https://developer.android.com/training/data-storage/sqlite
 */

/** Database schema for droidcon sessions */
object DroidconContract {
    // Table contents are grouped together in an anonymous object.
    object SessionEntry : BaseColumns {
        const val TABLE_NAME = "sessions"
        const val COLUMN_NAME_SESSIONID = "session_id"
        const val COLUMN_NAME_SPEAKER = "speaker"
        const val COLUMN_NAME_ROLE = "role"
        const val COLUMN_NAME_LOCATIONID = "location_id"
        const val COLUMN_NAME_DATE = "date"
        const val COLUMN_NAME_TIME = "time"
        const val COLUMN_NAME_SUBJECT = "subject"
        const val COLUMN_NAME_DESCRIPTION = "description"
    }

    object LocationEntry : BaseColumns {
        const val TABLE_NAME = "locations"
        const val COLUMN_NAME_LOCATIONID = "location_id"
        const val COLUMN_NAME_DIRECTIONS = "directions"
    }

    object FavoriteEntry : BaseColumns {
        const val TABLE_NAME = "favorites"
        const val COLUMN_NAME_SESSIONID = "session_id"
        const val COLUMN_NAME_ISFAVORITE = "is_favorite"
    }
    object EmbeddingEntry : BaseColumns {
        const val TABLE_NAME = "embedding"
        const val COLUMN_NAME_SESSIONID = "session_id"
        const val COLUMN_NAME_VECTOR = "vector"
    }
}

//-- Sessions
private const val SQL_CREATE_SESSION_ENTRIES =
    "CREATE TABLE ${DroidconContract.SessionEntry.TABLE_NAME} (" +
            "${DroidconContract.SessionEntry.COLUMN_NAME_SESSIONID} TEXT PRIMARY KEY," +
            "${DroidconContract.SessionEntry.COLUMN_NAME_SPEAKER} TEXT," +
            "${DroidconContract.SessionEntry.COLUMN_NAME_ROLE} TEXT," +
            "${DroidconContract.SessionEntry.COLUMN_NAME_LOCATIONID} TEXT," +
            "${DroidconContract.SessionEntry.COLUMN_NAME_DATE} TEXT," +
            "${DroidconContract.SessionEntry.COLUMN_NAME_TIME} TEXT," +
            "${DroidconContract.SessionEntry.COLUMN_NAME_SUBJECT} TEXT," +
            "${DroidconContract.SessionEntry.COLUMN_NAME_DESCRIPTION} TEXT)"

private const val SQL_DELETE_SESSION_ENTRIES = "DROP TABLE IF EXISTS ${DroidconContract.SessionEntry.TABLE_NAME}"

//-- Locations
private const val SQL_CREATE_LOCATION_ENTRIES =
    "CREATE TABLE ${DroidconContract.LocationEntry.TABLE_NAME} (" +
            "${DroidconContract.LocationEntry.COLUMN_NAME_LOCATIONID} TEXT PRIMARY KEY," +
            "${DroidconContract.LocationEntry.COLUMN_NAME_DIRECTIONS} TEXT)"

private const val SQL_DELETE_LOCATION_ENTRIES = "DROP TABLE IF EXISTS ${DroidconContract.LocationEntry.TABLE_NAME}"

//-- Favorites
private const val SQL_CREATE_FAVORITE_ENTRIES =
    "CREATE TABLE ${DroidconContract.FavoriteEntry.TABLE_NAME} (" +
            "${DroidconContract.FavoriteEntry.COLUMN_NAME_SESSIONID} TEXT PRIMARY KEY," +
            "${DroidconContract.FavoriteEntry.COLUMN_NAME_ISFAVORITE} INTEGER)"

private const val SQL_DELETE_FAVORITE_ENTRIES = "DROP TABLE IF EXISTS ${DroidconContract.FavoriteEntry.TABLE_NAME}"

//-- Embeddings
private const val SQL_CREATE_EMBEDDING_ENTRIES =
    "CREATE TABLE ${DroidconContract.EmbeddingEntry.TABLE_NAME} (" +
            "${DroidconContract.EmbeddingEntry.COLUMN_NAME_SESSIONID} TEXT PRIMARY KEY," +
            "${DroidconContract.EmbeddingEntry.COLUMN_NAME_VECTOR} TEXT)"

private const val SQL_DELETE_EMBEDDING_ENTRIES = "DROP TABLE IF EXISTS ${DroidconContract.EmbeddingEntry.TABLE_NAME}"



class DroidconDbHelper(var context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_SESSION_ENTRIES)
        db.execSQL(SQL_CREATE_LOCATION_ENTRIES)
        db.execSQL(SQL_CREATE_FAVORITE_ENTRIES)
        db.execSQL(SQL_CREATE_EMBEDDING_ENTRIES)

        seedLocations(db)
        seedSessions(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_SESSION_ENTRIES)
        db.execSQL(SQL_DELETE_LOCATION_ENTRIES)
        db.execSQL(SQL_DELETE_FAVORITE_ENTRIES)
        db.execSQL(SQL_DELETE_EMBEDDING_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 6
        const val DATABASE_NAME = "Droidcon.db"
    }

    private fun seedLocations(db: SQLiteDatabase) {
        db.execSQL("INSERT INTO locations VALUES ('Robertson 1','Upstairs to the right')")
        db.execSQL("INSERT INTO locations VALUES ('Robertson 2','Upstairs to the left')")
        db.execSQL("INSERT INTO locations VALUES ('Fisher East','Outside off the courtyard')")
        db.execSQL("INSERT INTO locations VALUES ('Fisher West','Downstairs behind the sponsor tables')")
        Log.i("LLM", "seedLocations 4 rows")
    }

    private fun seedSessions(db: SQLiteDatabase) {
        var rowCount = 0
        for (session in DroidconSessionObjects.droidconSessions) {
            val s = session.value
            db.execSQL(
                "INSERT INTO sessions VALUES ('${session.key}','${s.speaker}','${s.role}','${s.location}','${s.date}','${s.time}','${s.subject}','${s.description}')"
            )
            rowCount++
        }
        Log.i("LLM", "seedSessions $rowCount rows")
    }

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
        Log.i("LLM", "schema: $out")
        return out
    }
}