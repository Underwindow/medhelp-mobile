package com.podoynikov.medhelpmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.podoynikov.medhelpmobile.Analysis
import com.podoynikov.medhelpmobile.R
import com.podoynikov.medhelpmobile.interfaces.ItemClickListener

class AnalysesRecyclerAdapter (context: Context, private var analyses: List<Analysis>, private val analysisClickListener: ItemClickListener)
    : RecyclerView.Adapter<AnalysesRecyclerAdapter.ViewHolder>(){
    private val inflater = LayoutInflater.from(context)

    fun setItems(_analyses: List<Analysis>)
    {
        analyses =_analyses
        notifyDataSetChanged()
    }

    //Создаем элемент списка который отображается на экране
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.analysis, parent, false)
        return ViewHolder(view)
    }

    //Задаем значения для элемента списка
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val analysis = analyses[position]
        holder.bind(analysis)
        holder.itemView.setOnClickListener {
            analysisClickListener.onItemClickListener(analysis)
        }
    }

    //Получаем количество элементов в списке
    override fun getItemCount(): Int {
        return analyses.size
    }

    class ViewHolder constructor(view: View): RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(R.id.analysisName)
        private val checkView = view.findViewById<ImageView>(R.id.checkmark)
//        private val progressBar = view.findViewById<ImageView>(R.id.progressBar)
        private val materialCardView = view.findViewById<MaterialCardView>(R.id.materialCard)
        fun bind(analysis: Analysis) {
            nameView.text = analysis.name
            checkView.isVisible = analysis.isChecked
            materialCardView.strokeColor = ContextCompat.getColor(materialCardView.context, analysis.statusColor)
        }
    }
}