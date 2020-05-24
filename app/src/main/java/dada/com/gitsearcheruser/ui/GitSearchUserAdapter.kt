package dada.com.gitsearcheruser.ui

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dada.com.gitsearcheruser.R
import dada.com.gitsearcheruser.data.GitUser
import dada.com.gitsearcheruser.repositary.NetworkState

class GitSearchUserAdapter(private val retryCallback: () -> Unit)
    : PagedListAdapter<GitUser,RecyclerView.ViewHolder>(GIT_USER_COMPARATOR){
    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_git_users -> GitUserViewHolder.create(parent)
            R.layout.item_network_state -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_git_users -> (holder as GitUserViewHolder).bind(getItem(position))
            R.layout.item_network_state -> (holder as NetworkStateItemViewHolder).bindTo(
                networkState)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.item_network_state
        } else {
            R.layout.item_git_users
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED_SUCCESS

    companion object {
        val GIT_USER_COMPARATOR = object : DiffUtil.ItemCallback<GitUser>() {
            override fun areContentsTheSame(oldItem: GitUser, newItem: GitUser): Boolean =
                oldItem.avatarUrl == newItem.avatarUrl

            override fun areItemsTheSame(oldItem: GitUser, newItem: GitUser): Boolean =
                oldItem.login == newItem.login


        }

    }

}