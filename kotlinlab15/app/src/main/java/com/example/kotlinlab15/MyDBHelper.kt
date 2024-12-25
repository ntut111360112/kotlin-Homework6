package com.example.kotlinlab15

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Custom constructor extending SQLiteOpenHelper class
class MyDBHelper (
    context: Context,
    name: String = DB_NAME,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = VERSION
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private const val DB_NAME = "myDatabase" // Database name
        private const val VERSION = 1 // Database version
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create myTable with book as a string column and price as an integer column
        db.execSQL("CREATE TABLE myTable(book TEXT PRIMARY KEY, price INTEGER NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // When upgrading the database, drop the old table and recreate it
        db.execSQL("DROP TABLE IF EXISTS myTable")
        onCreate(db)
    }
}