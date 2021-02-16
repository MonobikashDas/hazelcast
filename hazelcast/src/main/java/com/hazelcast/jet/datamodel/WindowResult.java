/*
 * Copyright (c) 2008-2021, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.datamodel;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

import static com.hazelcast.jet.impl.util.DateUtil.toLocalTime;

/**
 * Holds the result of an aggregate operation performed over a time
 * window.
 *
 * @param <R> type of aggregated result
 *
 * @since 3.0
 */
public class WindowResult<R> implements IdentifiedDataSerializable {

    private long start;
    private long end;
    private R result;
    private boolean isEarly;

    WindowResult() {
    }

    /**
     * @param start  start time of the window
     * @param end    end time of the window
     * @param result result of aggregation
     * @param isEarly whether this is an early result, to be followed by the final one
     */
    public WindowResult(long start, long end, @Nonnull R result, boolean isEarly) {
        this.start = start;
        this.end = end;
        this.result = result;
        this.isEarly = isEarly;
    }

    /**
     * Constructs a window result that is not early.
     *
     * @param start  start time of the window
     * @param end    end time of the window
     * @param result result of aggregation
     */
    public WindowResult(long start, long end, @Nonnull R result) {
        this(start, end, result, false);
    }

    /**
     * Returns the starting timestamp of the window.
     */
    public long start() {
        return start;
    }

    /**
     * Returns the ending timestamp of the window.
     */
    public long end() {
        return end;
    }

    /**
     * Returns the aggregated result.
     */
    @Nonnull
    public R result() {
        return result;
    }

    /**
     * Returns whether this is an early window result, to be followed by the
     * final one.
     */
    public boolean isEarly() {
        return isEarly;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WindowResult<?> that = (WindowResult<?>) obj;
        return this.start == that.start
                && this.end == that.end
                && this.isEarly == that.isEarly
                && Objects.equals(this.result, that.result);
    }

    @Override
    public int hashCode() {
        int hc = 17;
        hc = 73 * hc + Long.hashCode(start);
        hc = 73 * hc + Long.hashCode(end);
        hc = 73 * hc + Boolean.hashCode(isEarly);
        hc = 73 * hc + Objects.hashCode(result);
        return hc;
    }

    @Override
    public String toString() {
        return String.format(
                "WindowResult{start=%s, end=%s, value='%s', isEarly=%s}",
                toLocalTime(start), toLocalTime(end), result, isEarly);
    }

    @Override
    public int getFactoryId() {
        return JetDatamodelDataSerializerHook.FACTORY_ID;
    }

    @Override
    public int getClassId() {
        return JetDatamodelDataSerializerHook.WINDOW_RESULT;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(start);
        out.writeLong(end);
        out.writeBoolean(isEarly);
        out.writeObject(result);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        start = in.readLong();
        end = in.readLong();
        isEarly = in.readBoolean();
        result = in.readObject();
    }
}
