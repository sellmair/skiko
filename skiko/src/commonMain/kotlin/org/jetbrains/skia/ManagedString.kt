package org.jetbrains.skia

import org.jetbrains.skia.impl.Library.Companion.staticLoad
import org.jetbrains.skia.impl.Managed
import org.jetbrains.skia.impl.Stats
import org.jetbrains.skia.impl.reachabilityBarrier

expect class ManagedString internal constructor(ptr: Long) : Managed {
    constructor(s: String?)
    override fun toString(): String
    fun insert(offset: Int, s: String): ManagedString
    fun append(s: String): ManagedString
    fun remove(from: Int): ManagedString
    fun remove(from: Int, length: Int): ManagedString
}