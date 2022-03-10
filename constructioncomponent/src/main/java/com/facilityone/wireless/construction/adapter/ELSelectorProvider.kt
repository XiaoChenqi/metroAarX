package com.facilityone.wireless.construction.adapter


import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.facilityone.wireless.construction.R
import com.facilityone.wireless.construction.databinding.ItemClSelectorBinding

import com.facilityone.wireless.construction.module.ConstructionService
import com.facilityone.wireless.construction.module.SelectorModel


class ELSelectorProvider : BaseItemProvider<ConstructionService.ElectronicLedgerEntity,BaseViewHolder>() {



    override fun viewType(): Int {
        return ElectronicLedgerAdapter.TYPE_RADIO
    }

    override fun layout(): Int {
        return R.layout.item_cl_selector
    }

    override fun convert(
        helper: BaseViewHolder,
        item: ConstructionService.ElectronicLedgerEntity?,
        position: Int
    ) {

        val selectorModel=item!!.content as SelectorModel
        val binding=getBinding<ItemClSelectorBinding>(helper.itemView.rootView)!!

        try {

            if (binding!=null){
                binding.executePendingBindings()

                //标题文字
                binding.taskTitleTv.text=selectorModel.name


                //设置选择项显示的文字
                if (selectorModel.selectValues!=null&& selectorModel.selectValues!!.isNotEmpty()){
                    binding.rbNormal.text= selectorModel.selectValues!![0]
                    binding.rbException.text=selectorModel.selectValues!![1]
                }else{
                    binding.rbNormal.text= "正常"
                    binding.rbException.text="异常"
                }
                //判断上次是否选择过,操作过则重新渲染选择值,反之则清除状态
                if (item.value!=null){
                    //如果当前tag中存储值和item中存储值相同时则选择对应的选项,反之清除状态
                    if (binding.rgSelect.tag!=null&&item.value as String==binding.rgSelect.tag as String){
                        if (item.value as String== selectorModel.selectValues!![0] ){
                            binding.rgSelect.check(R.id.rbNormal)
                        }else{
                            binding.rgSelect.check(R.id.rbException)
                        }
                    }else{
                        //清除状态，并选择item存储的值
                        binding.rgSelect.clearCheck()
                        if (item.value as String== selectorModel.selectValues!![0] ){
                            binding.rgSelect.check(R.id.rbNormal)
                        }else{
                            binding.rgSelect.check(R.id.rbException)
                        }
                    }
                }else{
                    binding.rgSelect.clearCheck()
                }



                binding.rgSelect.setOnCheckedChangeListener { group, checkedId ->
                    //通过反射获取adapter对象
                    val adapterReflection=helper.javaClass.getDeclaredField("adapter")
                    adapterReflection.isAccessible=true
                    val tempAdapter:ElectronicLedgerAdapter  = adapterReflection.get(helper) as ElectronicLedgerAdapter
                    //从adapter对象中获取recycleview容器用于判断滑动状态
                    val recyclerView=tempAdapter.tempRecycleView
                    if (recyclerView!!.isComputingLayout){
                        recyclerView.post {
                            Runnable {
                                if (checkedId==R.id.rbNormal){
                                    binding.rgSelect.tag= selectorModel.selectValues!![0]
                                    item.value= selectorModel.selectValues!![0]
                                }else if (checkedId==R.id.rbException){
                                    binding.rgSelect.tag= selectorModel.selectValues!![1]
                                    item.value= selectorModel.selectValues!![1]
                                }

                                tempAdapter.notifyItemChanged(helper.layoutPosition,
                                ElectronicLedgerAdapter.ITEM_0_PAYLOAD)
                            }
                        }
                    }else{
                        if (checkedId==R.id.rbNormal){
                            binding.rgSelect.tag= selectorModel.selectValues!![0]
                            item.value= selectorModel.selectValues!![0]
                        }else if (checkedId==R.id.rbException){
                            binding.rgSelect.tag= selectorModel.selectValues!![1]
                            item.value= selectorModel.selectValues!![1]
                        }

                        tempAdapter.notifyItemChanged(helper.layoutPosition,
                            ElectronicLedgerAdapter.ITEM_0_PAYLOAD)
                    }



                }


            }
        }catch (e:Exception){
            print(e.stackTrace)
        }


//        binding.setVariable(position,item)
//        binding.taskTitleTv.text=selectorModel.name



        binding.taskTipTv.visibility=View.GONE
//
//        if (TextUtils.isEmpty(selectorModel.tips)){
//            binding.taskTipTv.visibility=View.GONE
//        }else{
//            binding.taskTipTv.visibility=View.VISIBLE
//            binding.taskTipTv.text = selectorModel.tips
//        }




    }

    fun <DB: ViewDataBinding>getBinding(view: View):DB?{
        return DataBindingUtil.bind(view)
    }


}