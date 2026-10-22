package com.unknowncoder05.escudo400

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

data class AllowedNumber(val id: Long, val name: String, val phone: String)
data class CallEvent(val id: Long, val phone: String, val blocked: Boolean, val reason: Reason, val time: Long)

class CallStore(context: Context, name: String = "escudo400.db") : SQLiteOpenHelper(context, name, null, 1) {
    private val preferences = context.getSharedPreferences("$name.settings", Context.MODE_PRIVATE)
    var revision by mutableIntStateOf(0)
        private set

    var enabled: Boolean
        get() = preferences.getBoolean("enabled", true)
        set(value) { preferences.edit().putBoolean("enabled", value).apply(); revision++ }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE allowed (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, phone TEXT NOT NULL UNIQUE)")
        db.execSQL("CREATE TABLE events (id INTEGER PRIMARY KEY AUTOINCREMENT, phone TEXT NOT NULL, blocked INTEGER NOT NULL, reason TEXT NOT NULL, time INTEGER NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun allowedNumbers(): List<AllowedNumber> = readableDatabase.query(
        "allowed", arrayOf("id", "name", "phone"), null, null, null, null, "name COLLATE NOCASE, id"
    ).use { cursor -> buildList {
        while (cursor.moveToNext()) add(AllowedNumber(cursor.getLong(0), cursor.getString(1), cursor.getString(2)))
    } }

    fun contains(phone: String?): Boolean = phone != null && readableDatabase.query(
        "allowed", arrayOf("id"), "phone = ?", arrayOf(phone), null, null, null, "1"
    ).use { it.moveToFirst() }

    fun save(name: String, phone: String, id: Long? = null) {
        require(name.trim().isNotEmpty()) { "Escribe un nombre para identificar el número." }
        val normalized = requireNotNull(CallPolicy.normalize(phone)) {
            "Introduce 9 cifras españolas o un número internacional con + y código de país."
        }
        val values = ContentValues().apply { put("name", name.trim()); put("phone", normalized) }
        if (id == null) writableDatabase.insertOrThrow("allowed", null, values)
        else check(writableDatabase.update("allowed", values, "id = ?", arrayOf(id.toString())) == 1)
        revision++
    }

    fun delete(id: Long) {
        writableDatabase.delete("allowed", "id = ?", arrayOf(id.toString()))
        revision++
    }

    fun record(phone: String?, decision: Decision) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.insertOrThrow("events", null, ContentValues().apply {
                put("phone", CallPolicy.normalize(phone) ?: phone.orEmpty().ifBlank { "Oculto" })
                put("blocked", if (decision.blocked) 1 else 0)
                put("reason", decision.reason.name)
                put("time", System.currentTimeMillis())
            })
            db.execSQL("DELETE FROM events WHERE id NOT IN (SELECT id FROM events ORDER BY id DESC LIMIT 100)")
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
        revision++
    }

    fun events(): List<CallEvent> = readableDatabase.query(
        "events", arrayOf("id", "phone", "blocked", "reason", "time"), null, null, null, null, "id DESC", "100"
    ).use { cursor -> buildList {
        while (cursor.moveToNext()) add(CallEvent(cursor.getLong(0), cursor.getString(1),
            cursor.getInt(2) == 1, Reason.valueOf(cursor.getString(3)), cursor.getLong(4)))
    } }

    companion object {
        @Volatile private var instance: CallStore? = null
        fun get(context: Context): CallStore = instance ?: synchronized(this) {
            instance ?: CallStore(context.applicationContext).also { instance = it }
        }
    }
}
