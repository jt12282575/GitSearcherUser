package dada.com.gitsearcheruser.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dada.com.gitsearcheruser.R
import dada.com.gitsearcheruser.repositary.NetworkState
import dada.com.gitsearcheruser.repositary.Status

class NetworkStateItemViewHolder(view: View,
                                 private val retryCallback: () ->Unit
):RecyclerView.ViewHolder(view) {
    private val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
    private val retry = view.findViewById<Button>(R.id.retry_button)
    private val errorMsg = view.findViewById<TextView>(R.id.error_msg)
    private val showEmpty = view.findViewById<LinearLayout>(R.id.empty_container)
    private val showEnd = view.findViewById<LinearLayout>(R.id.result_end_container)
    init {
        retry.setOnClickListener {
            retryCallback()
        }
    }
    fun bindTo(networkState: NetworkState?) {
        progressBar.visibility = toVisibility(networkState?.status == Status.RUNNING)
        retry.visibility = toVisibility(networkState?.status == Status.FAILED)
        showEmpty.visibility = toVisibility(networkState?.status == Status.EMPTY)
        showEnd.visibility = toVisibility(networkState?.status == Status.NO_MORE_DATA)
        errorMsg.visibility = toVisibility(networkState?.status == Status.FAILED)
        errorMsg.text = networkState?.msg?:errorMsg.context.resources.getString(R.string.unknown_error)
    }

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_network_state, parent, false)
            return NetworkStateItemViewHolder(view, retryCallback)
        }

        fun toVisibility(constraint : Boolean): Int {
            return if (constraint) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }


}