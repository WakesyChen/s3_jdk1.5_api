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

package com.amazonaws.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

/**
 * Socket delegate class. Subclasses could extend this class, so that
 * they only need to override methods they are interested in enhancing.
 *
 */
public class DelegateSocket extends Socket {

    protected final Socket sock;

    public DelegateSocket(Socket sock) {
        this.sock = sock;
    }

    
    public void connect(SocketAddress endpoint) throws IOException {
        sock.connect(endpoint);
    }

    
    public void connect(SocketAddress endpoint, int timeout) throws IOException {
        sock.connect(endpoint, timeout);
    }

    
    public void bind(SocketAddress bindpoint) throws IOException {
        sock.bind(bindpoint);
    }

    
    public InetAddress getInetAddress() {
        return sock.getInetAddress();
    }

    
    public InetAddress getLocalAddress() {
        return sock.getLocalAddress();
    }

    
    public int getPort() {
        return sock.getPort();
    }

    
    public int getLocalPort() {
        return sock.getLocalPort();
    }

    
    public SocketAddress getRemoteSocketAddress() {
        return sock.getRemoteSocketAddress();
    }

    
    public SocketAddress getLocalSocketAddress() {
        return sock.getLocalSocketAddress();
    }

    
    public SocketChannel getChannel() {
        return sock.getChannel();
    }

    
    public InputStream getInputStream() throws IOException {
        return sock.getInputStream();
    }

    
    public OutputStream getOutputStream() throws IOException {
        return sock.getOutputStream();
    }

    
    public void setTcpNoDelay(boolean on) throws SocketException {
        sock.setTcpNoDelay(on);
    }

    
    public boolean getTcpNoDelay() throws SocketException {
        return sock.getTcpNoDelay();
    }

    
    public void setSoLinger(boolean on, int linger) throws SocketException {
        sock.setSoLinger(on, linger);
    }

    
    public int getSoLinger() throws SocketException {
        return sock.getSoLinger();
    }

    
    public void sendUrgentData(int data) throws IOException {
        sock.sendUrgentData(data);
    }

    
    public void setOOBInline(boolean on) throws SocketException {
        sock.setOOBInline(on);
    }

    
    public boolean getOOBInline() throws SocketException {
        return sock.getOOBInline();
    }

    
    public void setSoTimeout(int timeout) throws SocketException {
        sock.setSoTimeout(timeout);
    }

    
    public int getSoTimeout() throws SocketException {
        return sock.getSoTimeout();
    }

    
    public void setSendBufferSize(int size) throws SocketException {
        sock.setSendBufferSize(size);
    }

    
    public int getSendBufferSize() throws SocketException {
        return sock.getSendBufferSize();
    }

    
    public void setReceiveBufferSize(int size) throws SocketException {
        sock.setReceiveBufferSize(size);
    }

    
    public int getReceiveBufferSize() throws SocketException {
        return sock.getReceiveBufferSize();
    }

    
    public void setKeepAlive(boolean on) throws SocketException {
        sock.setKeepAlive(on);
    }

    
    public boolean getKeepAlive() throws SocketException {
        return sock.getKeepAlive();
    }

    
    public void setTrafficClass(int tc) throws SocketException {
        sock.setTrafficClass(tc);
    }

    
    public int getTrafficClass() throws SocketException {
        return sock.getTrafficClass();
    }

    
    public void setReuseAddress(boolean on) throws SocketException {
        sock.setReuseAddress(on);
    }

    
    public boolean getReuseAddress() throws SocketException {
        return sock.getReuseAddress();
    }

    
    public void close() throws IOException {
        sock.close();
    }

    
    public void shutdownInput() throws IOException {
        sock.shutdownInput();
    }

    
    public void shutdownOutput() throws IOException {
        sock.shutdownOutput();
    }

    
    public String toString() {
        return sock.toString();
    }

    
    public boolean isConnected() {
        return sock.isConnected();
    }

    
    public boolean isBound() {
        return sock.isBound();
    }

    
    public boolean isClosed() {
        return sock.isClosed();
    }

    
    public boolean isInputShutdown() {
        return sock.isInputShutdown();
    }

    
    public boolean isOutputShutdown() {
        return sock.isOutputShutdown();
    }

    
    public void setPerformancePreferences(int connectionTime, int latency,
            int bandwidth) {
        sock.setPerformancePreferences(connectionTime, latency, bandwidth);
    }
}
