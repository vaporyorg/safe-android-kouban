package io.gnosis.kouban.ui.transaction.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import io.gnosis.kouban.core.ui.adapter.BaseFactory
import io.gnosis.kouban.core.ui.adapter.UnsupportedViewType
import io.gnosis.kouban.databinding.ItemDetailsAddressBinding
import io.gnosis.kouban.databinding.ItemDetailsLabelDescriptionBinding
import io.gnosis.kouban.databinding.ItemDetailsTransactionTypeBinding
import io.gnosis.kouban.databinding.ItemTransactionDetailsLinkBinding
import pm.gnosis.model.Solidity

class TransactionDetailFactory : BaseFactory<BaseDetailViewHolder<Any>>() {

    override fun newViewHolder(viewBinding: ViewBinding, viewType: Int): BaseDetailViewHolder<Any> {
        return when (viewType) {
            ViewType.LabelDescription.ordinal -> LabelDescriptionViewHolder(viewBinding as ItemDetailsLabelDescriptionBinding)
            ViewType.LabelDate.ordinal -> LabelDateViewHolder(viewBinding as ItemDetailsLabelDescriptionBinding)
            ViewType.Address.ordinal -> AddressDetailsViewHolder(viewBinding as ItemDetailsAddressBinding)
            ViewType.TransactionType.ordinal -> TransactionTypeViewHolder(viewBinding as ItemDetailsTransactionTypeBinding)
            ViewType.Link.ordinal -> LinkViewHolder(viewBinding as ItemTransactionDetailsLinkBinding)
            else -> throw UnsupportedViewType()
        } as BaseDetailViewHolder<Any>
    }

    override fun layout(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewBinding {
        return when (viewType) {
            ViewType.LabelDescription.ordinal, ViewType.LabelDate.ordinal -> ItemDetailsLabelDescriptionBinding.inflate(layoutInflater, parent, false)
            ViewType.Address.ordinal -> ItemDetailsAddressBinding.inflate(layoutInflater, parent, false)
            ViewType.TransactionType.ordinal -> ItemDetailsTransactionTypeBinding.inflate(layoutInflater, parent, false)
            ViewType.Link.ordinal -> ItemTransactionDetailsLinkBinding.inflate(layoutInflater, parent, false)
            else -> throw UnsupportedViewType()
        }
    }

    override fun <T> viewTypeFor(item: T): Int {
        return when (item) {
            is Solidity.Address -> ViewType.Address.ordinal
            is LabelDescription -> ViewType.LabelDescription.ordinal
            is LabelDate -> ViewType.LabelDate.ordinal
            is TransactionTypeView -> ViewType.TransactionType.ordinal
            is Link -> ViewType.Link.ordinal
            else -> throw UnsupportedViewType(item.toString())
        }
    }

    private enum class ViewType {
        LabelDescription, LabelDate, Address, TransactionType, Link
    }
}
