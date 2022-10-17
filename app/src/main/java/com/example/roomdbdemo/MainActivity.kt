package com.example.roomdbdemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdbdemo.databinding.ActivityMainBinding
import com.example.roomdbdemo.databinding.DialogUpdateBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val employeeDao = (application as EmployeeApp).db.employeeDao()

        binding?.btnAdd?.setOnClickListener {
            addRecord(employeeDao)
        }

        lifecycleScope.launch {
            employeeDao.fetchAllEmployee().collect {
                val list = ArrayList(it)

                setUpListOfDataIntoRecycleView(list, employeeDao)
            }
        }
    }

    //Create a function to add entry to the database
    fun addRecord(employeeDao: EmployeeDao){
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmailId?.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty()){
            lifecycleScope.launch {
                employeeDao.insert(EmployeeEntity(name = name, email = email))
                Toast.makeText(applicationContext, "Record Saved", Toast.LENGTH_LONG).show()
                binding?.etName?.text?.clear()
                binding?.etEmailId?.text?.clear()
            }
        } else {
            Toast.makeText(applicationContext, "Please input values", Toast.LENGTH_LONG).show()
        }
    }

    //Create a function to set up the recyclerview useing the itemAdapter
    private fun setUpListOfDataIntoRecycleView(employeeList: ArrayList<EmployeeEntity>, employeeDao: EmployeeDao){
        if (employeeList.isNotEmpty()){
            val itemAdapter = ItemAdapter(employeeList,
                {
                    updateId->
                    updateRecordDialog(updateId, employeeDao)
                },
                {
                    deleteId->
                    deleteRecordAlertDialog(deleteId, employeeDao)
                }
            )

            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
            binding?.rvItemsList?.adapter = itemAdapter

            binding?.rvItemsList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
        } else {
            binding?.rvItemsList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }

    //Create a function to update the Record
    private fun updateRecordDialog(id: Int, employeeDao: EmployeeDao){
        val updateDialog = Dialog(this, R.style.ThemeDialog)
        updateDialog.setCancelable(false)
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect {
                if(it != null){
                    binding.etUpdateEmailId.setText(it.email)
                    binding.etUpdateName.setText(it.name)
                }
            }
        }

        binding.tvUpdate.setOnClickListener {
            val name = binding.etUpdateName.text.toString()
            val email = binding.etUpdateEmailId.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()){
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id, name, email))
                    Toast.makeText(applicationContext, "Record Updated", Toast.LENGTH_LONG).show()
                    updateDialog.dismiss()
                }
            } else {
                Toast.makeText(applicationContext, "Name and email not to be blank", Toast.LENGTH_LONG).show()
            }
        }

        binding.tvCancel.setOnClickListener {
            updateDialog.dismiss()
        }

        updateDialog.show()
    }

    //Create a function to delete a Record
    private fun deleteRecordAlertDialog(id: Int, employeeDao: EmployeeDao){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setPositiveButton("Yes"){dialogInterface, _ ->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(applicationContext, "Record Deleted", Toast.LENGTH_LONG).show()
            }
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No"){dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}