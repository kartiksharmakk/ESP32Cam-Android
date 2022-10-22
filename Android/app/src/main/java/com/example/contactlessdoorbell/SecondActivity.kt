package com.example.contactlessdoorbell

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactlessdoorbell.databinding.ActivitySecondBinding
import com.example.contactlessdoorbell.db.Timestamp
import com.example.contactlessdoorbell.db.TimestampDatabase
import com.example.contactlessdoorbell.db.TimestampRepository


class SecondActivity : AppCompatActivity() {
    private lateinit var timestampViewModel: TimestampViewModel
    private lateinit var binding: ActivitySecondBinding
    var dayStamp:String?=null
    var timeStamp:String?=null
    var notification_id:Int?=null
    var i=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_second)
        val dao = TimestampDatabase.getInstance(application).TimestampDAO
        val repository = TimestampRepository(dao)

        val factory = TimestampViewModelFactory(repository)
        timestampViewModel = ViewModelProvider(this, factory).get(TimestampViewModel::class.java)
        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.

        //background fcm backend notification data
            intent.extras?.let {

                dayStamp = it.getString("DATE:")
                timeStamp = it.getString("HOUR:")
                notification_id=it.getString("NOTIFICATION_ID")?.toInt()
                Log.d("TAG", "Date hai: $dayStamp aur time hai : $timeStamp")
            }

            val intentForeground = intent
            if (intentForeground.getStringExtra("from").equals("foregroundfcm")) {
                dayStamp = intentForeground.getStringExtra("DATE:")
                timeStamp = intentForeground.getStringExtra("HOUR:")
                notification_id=intentForeground.getStringExtra("NOTIFICATION_ID")?.toInt()
            }

        binding.myViewModel = timestampViewModel
        binding.lifecycleOwner = this

        timestampViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
        initRecyclerView()

        if(timeStamp!=null&&dayStamp!=null) {
            timestampViewModel.insertSubscriber(Timestamp(null, timeStamp, dayStamp))

        }
        binding.clearall.setOnClickListener {
            timestampViewModel.clearAll()
        }
    }

    private fun initRecyclerView() {
        binding.timestampRecyclerView.layoutManager = LinearLayoutManager(this)
        displaySubscribersList()
    }

    private fun displaySubscribersList() {
        timestampViewModel.getSavedSubscribers().observe(this, Observer {
        binding.timestampRecyclerView.adapter=MyRecyclerViewAdapter(it)
        })
    }

}