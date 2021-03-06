/*
 * Copyright 2016-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.protocol.json;

import com.amazonaws.util.TimestampFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.SdkClientException;
import com.amazonaws.util.BinaryUtils;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.Timestamp;
import software.amazon.ion.system.IonWriterBuilder;

@SdkInternalApi
abstract class SdkIonGenerator implements StructuredJsonGenerator {
    private final String contentType;
    protected final IonWriter writer;

    private SdkIonGenerator(IonWriter writer, String contentType) {
        this.writer = writer;
        this.contentType = contentType;
    }

    
    public StructuredJsonGenerator writeStartArray() {
        try {
            writer.stepIn(IonType.LIST);
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeNull() {
        try {
            writer.writeNull();
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeEndArray() {
        try {
            writer.stepOut();
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeStartObject() {
        try {
            writer.stepIn(IonType.STRUCT);
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeEndObject() {
        try {
            writer.stepOut();
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeFieldName(String fieldName) {
        writer.setFieldName(fieldName);
        return this;
    }

    
    public StructuredJsonGenerator writeValue(String val) {
        try {
            writer.writeString(val);
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeValue(boolean bool) {
        try {
            writer.writeBool(bool);
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeValue(long val) {
        try {
            writer.writeInt(val);
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeValue(double val) {
        try {
            writer.writeFloat(val);
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeValue(float val) {
        try {
            writer.writeFloat(val);
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeValue(short val) {
        try {
            writer.writeInt(val);
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeValue(int val) {
        try {
            writer.writeInt(val);
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeValue(ByteBuffer bytes) {
        try {
            writer.writeBlob(BinaryUtils.copyAllBytesFrom(bytes));
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeValue(Date date, TimestampFormat timestampFormat) {
        try {
            writer.writeTimestamp(Timestamp.forDateZ(date));
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeValue(BigDecimal value) {
        try {
            writer.writeDecimal(value);
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public StructuredJsonGenerator writeValue(BigInteger value) {
        try {
            writer.writeInt(value);
        } catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    
    public abstract byte[] getBytes();

    
    public String getContentType() {
        return contentType;
    }

    public static SdkIonGenerator create(IonWriterBuilder builder, String contentType) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        IonWriter writer = builder.build(bytes);
        return new ByteArraySdkIonGenerator(bytes, writer, contentType);
    }

    private static class ByteArraySdkIonGenerator extends SdkIonGenerator {
        private final ByteArrayOutputStream bytes;

        public ByteArraySdkIonGenerator(ByteArrayOutputStream bytes, IonWriter writer, String contentType) {
            super(writer, contentType);
            this.bytes = bytes;
        }

        
        public byte[] getBytes() {
            try {
                writer.finish();
            } catch (IOException e) {
                throw new SdkClientException(e);
            }
            return bytes.toByteArray();
        }
    }
}
