package com.example.assign3

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var activeSwitch: Switch
    private lateinit var viewAllButton: Button
    private lateinit var addButton: Button
    private lateinit var listView: ListView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var customerList: ArrayList<Customer>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameEditText = findViewById(R.id.nameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        activeSwitch = findViewById(R.id.activeSwitch)
        viewAllButton = findViewById(R.id.viewAllButton)
        addButton = findViewById(R.id.addButton)
        listView = findViewById(R.id.listView)

        dbHelper = DatabaseHelper(this)

        addButton.setOnClickListener {
            addCustomer()
        }

        viewAllButton.setOnClickListener {
            viewAllCustomers()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCustomer = customerList[position]
            val deleted = dbHelper.deleteCustomer(selectedCustomer.id)
            if (deleted) {
                Toast.makeText(this, "Deleted: ${selectedCustomer.name}", Toast.LENGTH_SHORT).show()
                viewAllCustomers()
            } else {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addCustomer() {
        val name = nameEditText.text.toString().trim()
        val ageText = ageEditText.text.toString().trim()
        val isActive = activeSwitch.isChecked

        if (name.isEmpty() || ageText.isEmpty()) {
            Toast.makeText(this, "Please enter name and age", Toast.LENGTH_SHORT).show()
            return
        }

        val age = try {
            ageText.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            return
        }

        val id = dbHelper.addCustomer(name, age, isActive)
        if (id != -1L) {
            Toast.makeText(this, "Customer added successfully", Toast.LENGTH_SHORT).show()
            clearFields()
            viewAllCustomers() // Refresh the list
        } else {
            Toast.makeText(this, "Error adding customer", Toast.LENGTH_SHORT).show()
        }
    }

    private fun viewAllCustomers() {
        customerList = dbHelper.getAllCustomers()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            customerList.map { customer ->
                "Name: ${customer.name}, Age: ${customer.age}, isActive: ${if (customer.isActive) "Active" else "Inactive"}"
            }
        )
        listView.adapter = adapter
    }

    private fun clearFields() {
        nameEditText.text.clear()
        ageEditText.text.clear()
        activeSwitch.isChecked = false
    }
}
