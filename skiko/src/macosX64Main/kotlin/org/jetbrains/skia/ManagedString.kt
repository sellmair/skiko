package org.jetbrains.skia

import org.jetbrains.skia.impl.Library.Companion.staticLoad
import org.jetbrains.skia.impl.Managed
import org.jetbrains.skia.impl.Stats
import org.jetbrains.skia.impl.reachabilityBarrier

import org.jetbrains.skia.*
import org.jetbrains.skiko.skia.native.*

import kotlinx.cinterop.*

actual class ManagedString actual internal constructor(ptr: Long) : Managed(ptr, _FinalizerHolder.PTR) {
    companion object {
        // @SymbolName("org_jetbrains_skia_ManagedString__1nMake")
        // external fun _nMake(s: String?): Long
        fun _nMake(s: String?): Long = bridge_org_jetbrains_skia_ManagedString__1nMake(s?.cstr)

        @SymbolName("bridge_org_jetbrains_skia_ManagedString__1nGetFinalizer")
        external fun _nGetFinalizer(): Long

        // @SymbolName("org_jetbrains_skia_ManagedString__1nToString")
        // external fun _nToString(ptr: Long): String
        fun _nToString(ptr: Long): String = bridge_org_jetbrains_skia_ManagedString__1nToString(ptr)!!.toKString()

        // @SymbolName("org_jetbrains_skia_ManagedString__1nInsert")
        // external fun _nInsert(ptr: Long, offset: Int, s: String?)
        fun _nInsert(ptr: Long, offset: Int, s: String?) = bridge_org_jetbrains_skia_ManagedString__1nInsert(ptr, offset, s?.cstr)

        // @SymbolName("org_jetbrains_skia_ManagedString__1nAppend")
        // external fun _nAppend(ptr: Long, s: String?)
        fun _nAppend(ptr: Long, s: String?) = bridge_org_jetbrains_skia_ManagedString__1nAppend(ptr, s?.cstr)

        @SymbolName("bridge_org_jetbrains_skia_ManagedString__1nRemoveSuffix")
        external fun _nRemoveSuffix(ptr: Long, from: Int)

        @SymbolName("bridge_org_jetbrains_skia_ManagedString__1nRemove")
        external fun _nRemove(ptr: Long, from: Int, length: Int)

        init {
            staticLoad()
        }
    }

    actual constructor(s: String?) : this(_nMake(s)) {
        Stats.onNativeCall()
    }

    actual override fun toString(): String {
        return try {
            Stats.onNativeCall()
            _nToString(_ptr)
        } finally {
            reachabilityBarrier(this)
        }
    }

    actual fun insert(offset: Int, s: String): ManagedString {
        Stats.onNativeCall()
        _nInsert(_ptr, offset, s)
        return this
    }

    actual fun append(s: String): ManagedString {
        Stats.onNativeCall()
        _nAppend(_ptr, s)
        return this
    }

    actual fun remove(from: Int): ManagedString {
        Stats.onNativeCall()
        _nRemoveSuffix(_ptr, from)
        return this
    }

    actual fun remove(from: Int, length: Int): ManagedString {
        Stats.onNativeCall()
        _nRemove(_ptr, from, length)
        return this
    }

    internal object _FinalizerHolder {
        val PTR = _nGetFinalizer()
    }
}