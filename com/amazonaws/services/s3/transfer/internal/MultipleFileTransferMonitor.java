/*
 * Copyright 2012-2018 Amazon Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazonaws.services.s3.transfer.internal;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.amazonaws.services.s3.transfer.Transfer;

public class MultipleFileTransferMonitor implements TransferMonitor {

    private final Collection<? extends AbstractTransfer> subTransfers;
    private final AbstractTransfer transfer;
    private final Future<?> future;

    public MultipleFileTransferMonitor(AbstractTransfer transfer, Collection<? extends AbstractTransfer> subTransfers) {
        this.subTransfers = subTransfers;
        this.transfer = transfer;

        /*
         * The future object is not publicly exposed, so we only need to worry
         * about implementing get(). The other methods are implemented badly,
         * just to meet the interface contract.
         */
        this.future = new Future<Object>() {

            
            public boolean cancel(boolean mayInterruptIfRunning) {
                return true;
            }

            
            public Object get() throws InterruptedException, ExecutionException {
                Object result = null;
                for ( AbstractTransfer download : MultipleFileTransferMonitor.this.subTransfers ) {
                    result = download.getMonitor().getFuture().get();
                }
                return result;
            }

            
            public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
                    TimeoutException {
                Object result = null;
                for ( AbstractTransfer subTransfer : MultipleFileTransferMonitor.this.subTransfers ) {
                    result = subTransfer.getMonitor().getFuture().get(timeout, unit);
                }
                return result;
            }

            
            public boolean isCancelled() {
                return MultipleFileTransferMonitor.this.transfer.getState() == Transfer.TransferState.Canceled;
            }

            
            public boolean isDone() {
                return MultipleFileTransferMonitor.this.isDone();
            }            
        };
    }

    
    public Future<?> getFuture() {
        return future;
    }

    
    public synchronized boolean isDone() {
        for ( Transfer subTransfer : subTransfers ) {
            if ( !subTransfer.isDone() )
                return false;
        }
        return true;
    }

    
}
