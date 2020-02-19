package io.gnosis.kouban.core.ui.safe.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.gnosis.kouban.core.ui.base.BaseFragment
import io.gnosis.kouban.core.databinding.FragmentTransactionsBinding
import io.gnosis.kouban.core.databinding.ItemTransactionBinding
import io.gnosis.kouban.core.ui.MainViewModel
import io.gnosis.kouban.core.ui.OnAddressChangedListener
import io.gnosis.kouban.core.ui.adapter.BaseAdapter
import io.gnosis.kouban.core.ui.base.Loading
import io.gnosis.kouban.core.ui.base.Error
import io.gnosis.kouban.core.ui.helper.AddressHelper
import io.gnosis.kouban.core.utils.parseEthereumAddress
import io.gnosis.kouban.data.models.ServiceSafeTx
import kotlinx.coroutines.channels.consumeEach
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import pm.gnosis.model.Solidity
import pm.gnosis.svalinn.common.utils.snackbar
import pm.gnosis.utils.asEthereumAddress
import timber.log.Timber

class TransactionsFragment : BaseFragment<FragmentTransactionsBinding>() {

    private val viewModel by currentScope.viewModel<TransactionsViewModel>(this)
    private val mainViewModel by sharedViewModel<MainViewModel>()
    private val adapter by currentScope.inject<BaseAdapter<ServiceSafeTx, ItemTransactionBinding, BaseTransactionViewHolder>>()
    private val addressChangeListener: OnAddressChangedListener = { it?.let { address -> load(address) } }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentTransactionsBinding =
        FragmentTransactionsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            list.layoutManager = LinearLayoutManager(context)
            list.adapter = adapter
            list.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

            swipeToRefresh.setOnRefreshListener {
                mainViewModel.address.let { address ->
                    if (address != null) {
                        load(address)
                    } else {
                        swipeToRefresh.isRefreshing = false
                    }
                }
            }
        }

        if (arguments?.containsKey("safeAddress") == true) {
            mainViewModel.address = arguments!!.getString("safeAddress", "").asEthereumAddress()!!
            mainViewModel.address?.let { load(it) }
        } else {
            mainViewModel.address?.let { load(it) }
        }
        mainViewModel.addListener(addressChangeListener)
    }

    private fun load(address: Solidity.Address) {
        viewModel.loadTransactionsOf(address).observe(viewLifecycleOwner, Observer {
            when (it) {
                is Loading -> binding.swipeToRefresh.isRefreshing = it.isLoading
                is Transactions -> adapter.setItemsUnsafe(it.transactions.flatMap { transactions -> transactions.value })
                is Error -> {
                    it.throwable.printStackTrace()
                    Timber.e(it.throwable)
                    snackbar(binding.root, "SOME ERROR")
                }
            }
        })
    }

    override fun onDestroyView() {
        mainViewModel.removeListener(addressChangeListener)
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): TransactionsFragment = TransactionsFragment()
    }
}
