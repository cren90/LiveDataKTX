/**
 * Created by Chris Renfrow on 1/29/19.
 */

package com.cren90.livedataktx

import androidx.lifecycle.Observer

abstract class NonNullObserver<T> {

    fun getObserver(): Observer<T> = ActualObserver()

    abstract fun onValueChanged(t: T)

    private inner class ActualObserver : Observer<T> {

        override fun onChanged(t: T?) {
            t?.let { onValueChanged(it) }
        }
    }
}