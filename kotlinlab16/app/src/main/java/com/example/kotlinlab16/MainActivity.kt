package com.example.kotlinlab16

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val items: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize database and adapter
        dbrw = MyDBHelper(this).writableDatabase
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter

        setListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbrw.close()
    }

    private fun setListener() {
        val edBook = findViewById<EditText>(R.id.edBook)
        val edPrice = findViewById<EditText>(R.id.edPrice)

        findViewById<Button>(R.id.btnInsert).setOnClickListener {
            if (edBook.text.isEmpty() || edPrice.text.isEmpty()) {
                showToast("欄位請勿留空")
            } else {
                try {
                    dbrw.execSQL(
                        "INSERT INTO myTable(book, price) VALUES(?, ?)",
                        arrayOf(edBook.text.toString(), edPrice.text.toString())
                    )
                    showToast("新增: ${edBook.text}, 價格: ${edPrice.text}")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("新增失敗: $e")
                }
            }
        }

        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            if (edBook.text.isEmpty() || edPrice.text.isEmpty()) {
                showToast("欄位請勿留空")
            } else {
                try {
                    dbrw.execSQL(
                        "UPDATE myTable SET price = ? WHERE book LIKE ?",
                        arrayOf(edPrice.text.toString(), edBook.text.toString())
                    )
                    showToast("更新: ${edBook.text}, 價格: ${edPrice.text}")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("更新失敗: $e")
                }
            }
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            if (edBook.text.isEmpty()) {
                showToast("書名請勿留空")
            } else {
                try {
                    dbrw.execSQL(
                        "DELETE FROM myTable WHERE book LIKE ?",
                        arrayOf(edBook.text.toString())
                    )
                    showToast("刪除: ${edBook.text}")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("刪除失敗: $e")
                }
            }
        }

        findViewById<Button>(R.id.btnQuery).setOnClickListener {
            val queryString = if (edBook.text.isEmpty()) {
                "SELECT * FROM myTable"
            } else {
                "SELECT * FROM myTable WHERE book LIKE ?"
            }

            val args = if (edBook.text.isEmpty()) null else arrayOf(edBook.text.toString())
            val c = dbrw.rawQuery(queryString, args)

            items.clear()
            showToast("共有 ${c.count} 筆資料")
            while (c.moveToNext()) {
                items.add("書名: ${c.getString(0)}\t\t\t\t價格: ${c.getInt(1)}")
            }
            adapter.notifyDataSetChanged()
            c.close()
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun cleanEditText() {
        findViewById<EditText>(R.id.edBook).setText("")
        findViewById<EditText>(R.id.edPrice).setText("")
    }
}