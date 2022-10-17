package com.example.roomdbdemo

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdbdemo.databinding.ItemRowBinding

class ItemAdapter(private val items: ArrayList<EmployeeEntity>,
                    //variable for updateListener which require a id and return a Unit
                    val updateListener: (id: Int)-> Unit,
                    //variable for deleteListener which require a id and return a Unit
                    val deleteListener: (id: Int)-> Unit
): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root){

        val llMain = binding.llMain
        val tvName = binding.tvName
        val tvEmail = binding.tvEmail
        val ivEdit = binding.ivEdit
        val ivDelete = binding.ivDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRowBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]

        holder.tvName.text = item.name
        holder.tvEmail.text = item.email

        if (position % 2 == 0){
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGray))
        } else {
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
        //When click on edit then invoke it throug the current id of the selected item
        holder.ivEdit.setOnClickListener {
            updateListener.invoke(item.id)
        }
        //When click on delete then invoke it throug the current id of the selected item
        holder.ivDelete.setOnClickListener {
            deleteListener.invoke(item.id)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}