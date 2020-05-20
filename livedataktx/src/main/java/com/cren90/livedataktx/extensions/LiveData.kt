/**
 * Created by Chris Renfrow on 1/25/19.
 */

package com.cren90.livedataktx.extensions

import androidx.lifecycle.*
import com.cren90.livedataktx.NonNullMutableLiveData

@Suppress("unused")
inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (T) -> Unit) {
    this.observe(owner, Observer { it?.apply(observer) })
}

@Suppress("unused")
inline fun <T> MutableLiveData<T>.observe(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit
) {
    this.observe(owner, Observer { it?.apply(observer) })
}

fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { value = initialValue }

inline fun <T> dependentLiveData(vararg dependencies: LiveData<*>, crossinline mapper: () -> T?) =
    MediatorLiveData<T>().also { mediatorLiveData ->
        val observer = Observer<Any> { mediatorLiveData.value = mapper() }
        dependencies.forEach { dependencyLiveData ->
            mediatorLiveData.addSource(dependencyLiveData, observer)
        }
    }

inline fun <T> LiveData<T>.observeChange(): LiveData<T> {
    val changedLiveData = MediatorLiveData<T>()
    changedLiveData.addSource(this, object : Observer<T> {
        private var isInitialized = false
        private var lastValue: T? = null

        override fun onChanged(newValue: T) {
            if (!isInitialized) {
                isInitialized = true
                lastValue = newValue
                changedLiveData.postValue(lastValue)
            } else if ((newValue == null && lastValue != null || lastValue != newValue)) {
                lastValue = newValue

                changedLiveData.postValue(lastValue)
            }
        }
    })

    return changedLiveData
}

fun <T> MutableLiveData<T>.notify() {
    this.value = this.value
}

fun <T : Any?> mutableLiveDataOf(initialValue: T? = null): MutableLiveData<T> =
    MutableLiveData<T>().apply {
        value = initialValue
    }


fun <T : Any?> liveDataOf(initialValue: T? = null): LiveData<T> = MutableLiveData<T>().apply {
    value = initialValue
}

fun <T : Any?> nonNullMutableLiveDataOf(initialValue: T): NonNullMutableLiveData<T> =
    NonNullMutableLiveData(initialValue)

@Suppress("unused")
fun <X, Y> LiveData<X>.map(func: (X) -> Y): LiveData<Y> = Transformations.map(
    this,
    func
)

@Suppress("unused")
fun <X, Y> LiveData<X>.switchMap(func: (X?) -> LiveData<Y>): LiveData<Y> =
    Transformations.switchMap(
        this,
        func
    )

fun <X, Y> LiveData<X>.switchMapMutable(func: (X?) -> MutableLiveData<Y>): MutableLiveData<Y> {
    val result = MediatorLiveData<Y>()
    result.addSource(this, object : Observer<X> {
        var mSource: MutableLiveData<Y>? = null

        override fun onChanged(x: X?) {
            val newLiveData = func(x)
            if (mSource === newLiveData) {
                return
            }
            if (mSource != null) {
                result.removeSource(mSource!!)
            }
            mSource = newLiveData
            if (mSource != null) {
                result.addSource(mSource!!) { y -> result.setValue(y) }
            }
        }
    })
    return result
}

