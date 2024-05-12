package com.example.employee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import com.example.employee.database.Todo
import com.example.employee.database.TodoDatabase
import com.example.employee.database.TodoRepository



import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: TodoAdapter
    private lateinit var viewModel: MainActivityData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView:RecyclerView = findViewById(R.id.rvTodolist)

        val repository = TodoRepository(TodoDatabase.getInstance(this))

         viewModel = ViewModelProvider(this)[MainActivityData::class.java]
         viewModel.data.observe(this){todos ->
             adapter = TodoAdapter(todos, repository, viewModel)
             recyclerView.adapter = adapter
             recyclerView.layoutManager = LinearLayoutManager(this)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val data = repository.getAllTodoItems()
            runOnUiThread {
                viewModel.setData(data)
            }
        }

        val btnAddItem: Button = findViewById(R.id.btnAdditem)

        btnAddItem.setOnClickListener {
            displayAlert(repository)
        }

    }
    fun displayAlert(repository: TodoRepository){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter New Employee:")
        builder.setMessage("Enter the Employee name below:")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->

            val item = input.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                repository.insert(Todo(item))
               // val data = repository.getAllTodoItems()
               // runOnUiThread {
               //     viewModel.setData(data)
              //  }
            }
        }
        // Set the negative button action
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
// Create and show the alert dialog
        val alertDialog = builder.create()
        alertDialog.show()
    }
    }



