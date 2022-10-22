package com.example.contactlessdoorbell

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.contactlessdoorbell.databinding.ListItemBinding
import com.example.contactlessdoorbell.db.Timestamp


class MyRecyclerViewAdapter(private val timestampsList: List<Timestamp>) : RecyclerView.Adapter<MyViewHolder> ()
{
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(timestampsList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        val binding : ListItemBinding  = DataBindingUtil.inflate(layoutInflater,R.layout.list_item,parent,false)
        return MyViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return timestampsList.size
    }

}

class MyViewHolder(val binding: ListItemBinding):RecyclerView.ViewHolder(binding.root){

    fun bind(timestamp: Timestamp){

        binding.timeTextView.text= timestamp.time
        binding.dateTextView.text=timestamp.date

    }
}