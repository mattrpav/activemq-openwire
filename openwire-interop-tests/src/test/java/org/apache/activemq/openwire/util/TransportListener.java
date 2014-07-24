/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.openwire.util;

import java.io.IOException;

/**
 * An asynchronous listener of commands
 */
public interface TransportListener {

    /**
     * called to process a command
     *
     * @param command
     */
    void onCommand(Object command);

    /**
     * An unrecoverable exception has occurred on the transport
     *
     * @param error
     */
    void onException(IOException error);

    /**
     * The transport has suffered an interruption from which it hopes to recover
     *
     */
    void transportInterupted();

    /**
     * The transport has resumed after an interruption
     *
     */
    void transportResumed();

}
