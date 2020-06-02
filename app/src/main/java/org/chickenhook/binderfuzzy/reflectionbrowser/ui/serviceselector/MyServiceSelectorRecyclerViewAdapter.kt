package org.chickenhook.binderfuzzy.reflectionbrowser.ui.serviceselector

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.chickenhook.binderfuzzy.R


import org.chickenhook.binderfuzzy.reflectionbrowser.ui.serviceselector.ServiceSelectorFragment.OnListFragmentInteractionListener
import org.chickenhook.binderfuzzy.reflectionbrowser.ui.serviceselector.items.ClassItem

import kotlinx.android.synthetic.main.fragment_service_selector.view.*

/**
 * [RecyclerView.Adapter] that can display a [ClassItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyServiceSelectorRecyclerViewAdapter(
    private val mValues: List<ClassItem>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyServiceSelectorRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as ClassItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_service_selector, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.id
        holder.mContentView.text = item.content

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
