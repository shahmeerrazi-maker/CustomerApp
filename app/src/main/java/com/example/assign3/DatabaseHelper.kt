package com.example.assign3
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "CustomerDatabase.db"
        private const val TABLE_CUSTOMERS = "customers"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_AGE = "age"
        private const val COLUMN_IS_ACTIVE = "is_active"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = """
            CREATE TABLE $TABLE_CUSTOMERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_AGE INTEGER,
                $COLUMN_IS_ACTIVE INTEGER
            )
        """.trimIndent()
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CUSTOMERS")
        onCreate(db)
    }

    fun addCustomer(name: String, age: Int, isActive: Boolean): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_AGE, age)
            put(COLUMN_IS_ACTIVE, if (isActive) 1 else 0)
        }
        val id = db.insert(TABLE_CUSTOMERS, null, values)
        db.close()
        return id
    }

    fun getAllCustomers(): ArrayList<Customer> {
        val customerList = ArrayList<Customer>()
        val selectQuery = "SELECT * FROM $TABLE_CUSTOMERS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val customer = Customer(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ACTIVE)) == 1
                )
                customerList.add(customer)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return customerList
    }
    fun deleteCustomer(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_CUSTOMERS, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

}

data class Customer(
    val id: Int,
    val name: String,
    val age: Int,
    val isActive: Boolean
)
