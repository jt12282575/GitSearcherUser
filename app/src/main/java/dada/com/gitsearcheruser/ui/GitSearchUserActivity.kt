package dada.com.gitsearcheruser.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModel
import androidx.activity.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dada.com.gitsearcheruser.R
import dada.com.gitsearcheruser.api.GitApi
import dada.com.gitsearcheruser.data.GitUser
import dada.com.gitsearcheruser.repositary.GitUserRepositary
import dada.com.gitsearcheruser.util.hideKeyboard
import kotlinx.android.synthetic.main.activity_git_search_user.*
import java.util.concurrent.Executors

class GitSearchUserActivity : AppCompatActivity() {
    private val ioExecutor = Executors.newFixedThreadPool(5)
    private val gitUserViewModel: GitUserViewModel by viewModels{
        object: AbstractSavedStateViewModelFactory(this, null){
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {

                val repo = GitUserRepositary(
                    GitApi.get(),ioExecutor

                )
                @Suppress("UNCHECKED_CAST")
                return GitUserViewModel(repo, handle) as T
            }
        }


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_git_search_user)
        initAdapter()
        initSearch()
    }

    private fun initAdapter() {
        val adapter = GitSearchUserAdapter(){
            gitUserViewModel.retry()
        }
        rcv_git_users_list.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(this@GitSearchUserActivity)
            setOnTouchListener { _, _ ->
                hideKeyboard()
                return@setOnTouchListener false
            }
        }
        gitUserViewModel.gitUsers.observe(this, Observer<PagedList<GitUser>> {
            adapter.submitList(it) {
                val layoutManager = (rcv_git_users_list.layoutManager as LinearLayoutManager)
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    rcv_git_users_list.scrollToPosition(position)
                }
            }
        })
        gitUserViewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
    }

    private fun initSearch() {
        et_search_git_users.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateGitUserFromSearch()
                true
            } else {
                false
            }
        }
        et_search_git_users.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateGitUserFromSearch()
                true
            } else {
                false
            }
        }
    }

    private fun updateGitUserFromSearch(){
        et_search_git_users.text.trim().toString().let {
            if (it.isNotEmpty()){
                gitUserViewModel.showGitUserQueryResult(it)
            }
        }
    }
}
