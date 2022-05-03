package com.example.jpegtowebp

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentMainViewModel : ViewModel() {
    val cnt = MutableLiveData<Int>()
    val cbExif = MutableLiveData<Boolean>()

    val ratio = MutableLiveData<Float>()

    val uriList = MutableLiveData<MutableList<Uri>>()

    init{
        cnt.value = 0
        uriList.value = mutableListOf()
    }

    fun setList(list:MutableList<Uri>){
        uriList.postValue(list)
    }

    fun setRatio(value:Float){
        ratio.postValue(value)
    }

    fun addList(uri:Uri){
        val list = uriList.value
        list?.let{
            list.add(uri)
            uriList.postValue(list.toMutableList())
        }
    }
    fun removeItem(pos:Int){
        val list = uriList.value
        list?.let{
            it.removeAt(pos)
            uriList.postValue(list.toMutableList())
        }
    }
}