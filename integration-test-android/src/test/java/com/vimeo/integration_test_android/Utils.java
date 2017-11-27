/*
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2017 Vimeo
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.vimeo.integration_test_android;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.vimeo.sample.stag.generated.Stag;

import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Utils class for testing type adapter generation.  This is effectively a copy of
 * com.vimeo.sample.Utils.
 */
public final class Utils {

    private Utils() {
    }

    private static <T> TypeAdapter<T> getTypeAdapter(@NotNull Class<T> clazz) {
        Gson gson = new Gson();
        Stag.Factory factory = new Stag.Factory();
        TypeToken<T> innerModelType = TypeToken.get(clazz);
        return factory.create(gson, innerModelType);
    }

    /**
     * Verifies that a TypeAdapter was generated for the specified class.
     *
     * @param clazz the class to check.
     * @param <T>   the type of the class, used internally.
     * @throws Exception throws an exception if an adapter was not generated.
     */
    public static <T> void verifyTypeAdapterGeneration(@NotNull Class<T> clazz) throws Exception {
        TypeAdapter<T> typeAdapter = getTypeAdapter(clazz);
        assertNotNull("Type adapter should have been generated by Stag", typeAdapter);
    }

    /**
     * Verifies that a TypeAdapter was NOT generated for the specified class.
     *
     * @param clazz the class to check.
     * @param <T>   the type of the class, used internally.
     * @throws Exception throws an exception if an adapter was generated.
     */
    public static <T> void verifyNoTypeAdapterGeneration(@NotNull Class<T> clazz) throws Exception {
        TypeAdapter<T> typeAdapter = getTypeAdapter(clazz);
        assertNull("Type adapter should not have been generated by Stag", typeAdapter);
    }

}
