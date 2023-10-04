package com.silverpine.uu.core

class UUDispatchGroup(private var groupCompletion: (() -> Unit))
{
    private var count: Int = 0

    @Synchronized
    fun enter()
    {
        count++
    }

    @Synchronized
    fun leave()
    {
        count--

        if (count <= 0)
        {
            uuDispatch(groupCompletion)
        }
    }
}