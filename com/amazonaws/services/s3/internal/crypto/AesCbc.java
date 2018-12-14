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
package com.amazonaws.services.s3.internal.crypto;

class AesCbc extends ContentCryptoScheme {
     String getKeyGeneratorAlgorithm() { return "AES"; }
     String getCipherAlgorithm() { return "AES/CBC/PKCS5Padding"; }
     int getKeyLengthInBits() { return 256; }
     int getBlockSizeInBytes() { return 16; }
     int getIVLengthInBytes() { return 16; }
     long getMaxPlaintextSize() { return MAX_CBC_BYTES; }
}
