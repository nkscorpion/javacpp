/*
 * Copyright (C) 2011,2012,2013 Samuel Audet
 *
 * This file is part of JavaCPP.
 *
 * JavaCPP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * JavaCPP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCPP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.googlecode.javacpp;

import java.nio.ShortBuffer;

/**
 * The peer class to native pointers and arrays of {@code short}.
 * All operations take into account the position and limit, when appropriate.
 *
 * @author Samuel Audet
 */
public class ShortPointer extends Pointer {
    /**
     * Allocates enough memory for the array and copies it.
     *
     * @param array the array to copy
     * @see #put(short[])
     */
    public ShortPointer(short ... array) {
        this(array.length);
        put(array);
    }
    /**
     * For direct buffers, calls {@link Pointer#Pointer(Buffer)}, while for buffers
     * backed with an array, allocates enough memory for the array and copies it.
     *
     * @param buffer the Buffer to reference or copy
     * @see #put(short[])
     */
    public ShortPointer(ShortBuffer buffer) {
        super(buffer);
        if (buffer != null && buffer.hasArray()) {
            short[] array = buffer.array();
            allocateArray(array.length);
            put(array);
            position(buffer.position());
            limit(buffer.limit());
        }
    }
    /**
     * Allocates a native {@code short} array of the given size.
     *
     * @param size the number of {@code short} elements to allocate
     */
    public ShortPointer(int size) {
        try {
            allocateArray(size);
        } catch (UnsatisfiedLinkError e) {
            throw new RuntimeException("No native JavaCPP library in memory. (Has Loader.load() been called?)", e);
        }
    }
    /** @see Pointer#Pointer() */
    public ShortPointer() { }
    /** @see Pointer#Pointer(Pointer) */
    public ShortPointer(Pointer p) { super(p); }
    private native void allocateArray(int size);

    /** @see Pointer#position(int) */
    @Override public ShortPointer position(int position) {
        return super.position(position);
    }
    /** @see Pointer#limit(int) */
    @Override public ShortPointer limit(int limit) {
        return super.limit(limit);
    }
    /** @see Pointer#capacity(int) */
    @Override public ShortPointer capacity(int capacity) {
        return super.capacity(capacity);
    }

    /** @return {@code get(0)} */
    public short get() { return get(0); }
    /** @return the i-th {@code short} value of a native array */
    public native short get(int i);
    /** @return {@code put(0, s)} */
    public ShortPointer put(short s) { return put(0, s); }
    /**
     * Copies the {@code short} value to the i-th element of a native array.
     *
     * @param i the index into the array
     * @param s the {@code short} value to copy
     * @return this
     */
    public native ShortPointer put(int i, short s);

    /** @return {@code get(array, 0, array.length)} */
    public ShortPointer get(short[] array) { return get(array, 0, array.length); }
    /** @return {@code put(array, 0, array.length)} */
    public ShortPointer put(short ... array) { return put(array, 0, array.length); }
    /**
     * Reads a portion of the native array into a Java array.
     *
     * @param array the array to write to
     * @param offset the offset into the array where to start writing
     * @param length the length of data to read and write
     * @return this
     */
    public native ShortPointer get(short[] array, int offset, int length);
    /**
     * Writes a portion of a Java array into the native array.
     *
     * @param array the array to read from
     * @param offset the offset into the array where to start reading
     * @param length the length of data to read and write
     * @return this
     */
    public native ShortPointer put(short[] array, int offset, int length);

    /** @return {@code asByteBuffer().asShortBuffer()} */
    @Override public final ShortBuffer asBuffer() {
        return asByteBuffer().asShortBuffer();
    }
}
