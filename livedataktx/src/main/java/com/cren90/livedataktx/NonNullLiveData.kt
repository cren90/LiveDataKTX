/**
 * Created by Chris Renfrow on 1/29/19.
 */

package com.cren90.livedataktx

import androidx.lifecycle.LifecycleOwner

interface NonNullLiveData<T> {
    fun getValue(): T

    fun observe(owner: LifecycleOwner, observer: NonNullObserver<T>)
}