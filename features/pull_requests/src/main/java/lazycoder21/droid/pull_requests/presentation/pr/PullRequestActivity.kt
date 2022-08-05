package lazycoder21.droid.pull_requests.presentation.pr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import lazycoder21.droid.common.enitity.Resource
import lazycoder21.droid.common.utils.showErrorMessage
import lazycoder21.droid.common.utils.showIf
import lazycoder21.droid.pull_requests.databinding.ActivityPullRequestBinding
import lazycoder21.droid.pull_requests.domain.model.PullRequest
import lazycoder21.droid.pull_requests.presentation.adapter.PullRequestRvAdapter
import lazycoder21.droid.pull_requests.presentation.adapter.factory.ItemTypeFactory

@AndroidEntryPoint
class PullRequestActivity : AppCompatActivity() {

    private val viewModel: PullRequestViewModel by viewModels()
    private var _binding: ActivityPullRequestBinding? = null
    private val adapter = PullRequestRvAdapter(ItemTypeFactory())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPullRequestBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }

        initRecyclerView()
        observeLiveData()
        loadData()
    }

    private fun initRecyclerView() {
        _binding?.recyclerView?.apply {
            adapter = this@PullRequestActivity.adapter
        }
    }

    private fun loadData() {
        viewModel.fetchPullRequest()
    }

    private fun observeLiveData() {
        viewModel.pullRequest.observe(this) {
            when (it) {
                is Resource.Error -> showErrorMessage(it.message.asString(this))
                is Resource.Loading -> updateLoadingState(it.isLoading)
                is Resource.Success -> onSuccessList(it.data)
            }
        }
    }

    private fun onSuccessList(list: List<PullRequest>) {
        adapter.clearAndInsertItems(list)
    }

    private fun updateLoadingState(isLoading: Boolean) {
        _binding?.progressBarRoot?.progressBar?.showIf(isLoading)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel.pullRequest.removeObservers(this)
    }

    companion object {
        fun Activity.navigateToPullRequestActivity() {
            val source = this
            val destination = PullRequestActivity::class.java

            source.startActivity(
                Intent(
                    source, destination
                )
            )
        }
    }
}