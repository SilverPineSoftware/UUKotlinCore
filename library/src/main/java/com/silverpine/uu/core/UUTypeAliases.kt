package com.silverpine.uu.core

typealias UUVoidBlock = () -> Unit
typealias UUErrorBlock = (UUError?) -> Unit
typealias UUObjectBlock<T> = (T) -> Unit
typealias UUListBlock<T> = (List<T>) -> Unit
typealias UUObjectErrorBlock<T> = (T?, UUError?) -> Unit
typealias UUListErrorBlock<T> = (List<T>?, UUError?) -> Unit
typealias UUResultBlock<T> = (UUResult<T>) -> Unit

fun UUVoidBlock.dispatch()
{
    uuDispatch()
    {
        this()
    }
}

fun UUErrorBlock.dispatch(error: UUError?)
{
    uuDispatch()
    {
        this(error)
    }
}

fun <T> UUObjectBlock<T>.dispatch(obj: T)
{
    uuDispatch()
    {
        this(obj)
    }
}

fun <T> UUListBlock<T>.dispatch(obj: List<T>)
{
    uuDispatch()
    {
        this(obj)
    }
}

fun <T> UUObjectErrorBlock<T>.dispatch(obj: T?, error: UUError?)
{
    uuDispatch()
    {
        this(obj, error)
    }
}

fun <T> UUListErrorBlock<T>.dispatch(obj: List<T>?, error: UUError?)
{
    uuDispatch()
    {
        this(obj, error)
    }
}

fun <T> UUResultBlock<T>.dispatch(result: UUResult<T>)
{
    uuDispatch()
    {
        this(result)
    }
}

