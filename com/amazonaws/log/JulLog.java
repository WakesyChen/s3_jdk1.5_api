/*
 * Copyright 2015-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.log;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to delegate internal logging of the signers and core classes to JUL.
 */
final class JulLog implements InternalLogApi {

    private final Logger log;

    JulLog(Logger logger) {
        this.log = logger;
    }

    
    public void debug(Object message) {
        log.log(Level.FINE, String.valueOf(message));
    }

    
    public void debug(Object message, Throwable t) {
        log.log(Level.FINE, String.valueOf(message), t);
    }

    
    public void error(Object message) {
        log.log(Level.SEVERE, String.valueOf(message));
    }

    
    public void error(Object message, Throwable t) {
        log.log(Level.SEVERE, String.valueOf(message), t);
    }

    
    public void fatal(Object message) {
        log.log(Level.SEVERE, String.valueOf(message));
    }

    
    public void fatal(Object message, Throwable t) {
        log.log(Level.SEVERE, String.valueOf(message), t);
    }

    
    public void info(Object message) {
        log.log(Level.INFO, String.valueOf(message));
    }

    
    public void info(Object message, Throwable t) {
        log.log(Level.INFO, String.valueOf(message), t);
    }

    
    public boolean isDebugEnabled() {
        return log.isLoggable(Level.FINE);
    }

    
    public boolean isErrorEnabled() {
        return log.isLoggable(Level.SEVERE);
    }

    
    public boolean isFatalEnabled() {
        return log.isLoggable(Level.SEVERE);
    }

    
    public boolean isInfoEnabled() {
        return log.isLoggable(Level.INFO);
    }

    
    public boolean isTraceEnabled() {
        return log.isLoggable(Level.FINER);
    }

    
    public boolean isWarnEnabled() {
        return log.isLoggable(Level.WARNING);
    }

    
    public void trace(Object message) {
        log.log(Level.FINER, String.valueOf(message));
    }

    
    public void trace(Object message, Throwable t) {
        log.log(Level.FINER, String.valueOf(message), t);
    }

    
    public void warn(Object message) {
        log.log(Level.WARNING, String.valueOf(message));
    }

    
    public void warn(Object message, Throwable t) {
        log.log(Level.WARNING, String.valueOf(message), t);
    }
}
