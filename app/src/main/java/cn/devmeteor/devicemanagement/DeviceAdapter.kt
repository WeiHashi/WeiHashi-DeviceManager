package cn.devmeteor.devicemanagement

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cn.devmeteor.devicemanagement.RetrofitUtil.deviceApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class DeviceAdapter(var context: Context, var list: ArrayList<Device>) :
    RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.device_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.hostName.text = list[position].host_name
        holder.mac.text = list[position].device_id
        holder.allow.isChecked = list[position].allow
        holder.allow.setOnClickListener {
            deviceApi.allow(list[position].device_id, holder.allow.isChecked)
                .enqueue(object : Callback<Void> {
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mac: TextView = itemView.findViewById(R.id.device_mac)
        val hostName: TextView = itemView.findViewById(R.id.device_name)
        val allow: Switch = itemView.findViewById(R.id.allow)
    }
}