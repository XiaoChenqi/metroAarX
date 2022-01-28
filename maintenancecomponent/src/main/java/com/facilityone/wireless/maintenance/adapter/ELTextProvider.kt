package com.facilityone.wireless.maintenance.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider

import com.facilityone.wireless.maintenance.R
import com.facilityone.wireless.maintenance.databinding.ItemElSelectorBinding
import com.facilityone.wireless.maintenance.databinding.ItemElTextBinding
import com.facilityone.wireless.maintenance.model.MaintenanceEnity
import com.facilityone.wireless.maintenance.model.SelectorModel


class ELTextProvider: BaseItemProvider<MaintenanceEnity.ElectronicLedgerEntity, BaseViewHolder>() {



    override fun viewType(): Int {
        return ElectronicLedgerAdapter.TYPE_EDIT
    }

    override fun layout(): Int {
        return R.layout.item_el_text
    }

    override fun convert(
        helper: BaseViewHolder,
        item: MaintenanceEnity.ElectronicLedgerEntity?,
        position: Int
    ) {
        val binding=getBinding<ItemElTextBinding>(helper.itemView)
        binding!!.tvTitle.text=(item!!.content) as String
        binding.etInput.hint = "请输入"+((item.content) as String)
        binding.etInput.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                //通过反射获取adapter对象
                val adapterReflection = helper.javaClass.getDeclaredField("adapter")
                adapterReflection.isAccessible = true
                val tempAdapter: ElectronicLedgerAdapter =
                    adapterReflection.get(helper) as ElectronicLedgerAdapter
                //从adapter对象中获取recycleview容器用于判断滑动状态
                val recyclerView = tempAdapter.tempRecycleView
                if (recyclerView.isComputingLayout) {
                    recyclerView.post {
                        Runnable {
                            item.value = s.toString()
                            tempAdapter.notifyItemChanged(
                                helper.layoutPosition,
                                ElectronicLedgerAdapter.ITEM_0_PAYLOAD
                            )
                        }
                    }
                } else {
                    item.value = s.toString()
                    tempAdapter.notifyItemChanged(
                        helper.layoutPosition,
                        ElectronicLedgerAdapter.ITEM_0_PAYLOAD
                    )
                }
            }

        })

    }

    fun <DB: ViewDataBinding>getBinding(view:View):DB?{
        return DataBindingUtil.bind<DB>(view)
    }
}