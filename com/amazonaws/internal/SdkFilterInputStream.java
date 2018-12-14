/*
 * Copyright 2013-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.internal;

import static com.amazonaws.util.SdkRuntime.shouldAbort;

import com.amazonaws.AbortedException;
import com.amazonaws.annotation.SdkProtectedApi;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Base class for AWS Java SDK specific {@link FilterInputStream}.
 */
public class SdkFilterInputStream extends FilterInputStream implements
        MetricAware, Releasable {
    private volatile boolean aborted = false;

    protected SdkFilterInputStream(InputStream in) {
        super(in);
    }

    /**
     * @return The wrapped stream.
     */
    @SdkProtectedApi
    public InputStream getDelegateStream() {
        return in;
    }

    
    public boolean isMetricActivated() {
        if (in instanceof MetricAware) {
            MetricAware metricAware = (MetricAware)in;
            return metricAware.isMetricActivated();
        }
        return false;
    }

    /**
     * Aborts with subclass specific abortion logic executed if needed.
     * Note the interrupted status of the thread is cleared by this method.
     * @throws AbortedException if found necessary.
     */
    protected final void abortIfNeeded() {
        if (shouldAbort()) {
            abort();    // execute subclass specific abortion logic
            throw new AbortedException();
        }
    }

    /**
     * Can be used to provide abortion logic prior to throwing the
     * AbortedException. If the wrapped {@code InputStream} is also an instance
     * of this class, then it will also be aborted, otherwise this is a no-op.
     */
    public void abort() {
        if (in instanceof SdkFilterInputStream) {
            ((SdkFilterInputStream) in).abort();
        }
        aborted = true;
    }

    protected boolean isAborted() {
        return aborted;
    }

    
    public int read() throws IOException {
        abortIfNeeded();
        return in.read();
    }

    
    public int read(byte b[], int off, int len) throws IOException {
        abortIfNeeded();
        return in.read(b, off, len);
    }

    
    public long skip(long n) throws IOException {
        abortIfNeeded();
        return in.skip(n);
    }

    
    public int available() throws IOException {
        abortIfNeeded();
        return in.available();
    }

    
    public void close() throws IOException {
        in.close();
        abortIfNeeded();
    }

    
    public synchronized void mark(int readlimit) {
        abortIfNeeded();
        in.mark(readlimit);
    }

    
    public synchronized void reset() throws IOException {
        abortIfNeeded();
        in.reset();
    }

    
    public boolean markSupported() {
        abortIfNeeded();
        return in.markSupported();
    }

    
    public void release() {
        // Don't call IOUtils.release(in, null) or else could lead to infinite loop
        SdkIOUtils.closeQuietly(this);
        if (in instanceof Releasable) {
            // This allows any underlying stream that has the close operation
            // disabled to be truly released
            Releasable r = (Releasable)in;
            r.release();
        }
    }
}
