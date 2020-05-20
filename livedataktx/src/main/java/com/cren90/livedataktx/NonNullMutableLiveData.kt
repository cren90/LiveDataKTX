/**
 * Created by Chris Renfrow on 1/29/19.
 */

package com.cren90.livedataktx

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

class NonNullMutableLiveData<T>(private val initialValue: T) : MutableLiveData<T>(),
    NonNullLiveData<T> {

    override fun getValue(): T {
        return super.getValue() ?: initialValue
    }

    fun notifyContentChanged() {
        postValue(value)
    }

    override fun observe(owner: LifecycleOwner, observer: NonNullObserver<T>) {
        super.observe(owner, observer.getObserver())
    }

}