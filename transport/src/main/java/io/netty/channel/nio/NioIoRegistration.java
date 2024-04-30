/*
 * Copyright 2024 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel.nio;

import io.netty.channel.IoRegistration;

import java.nio.channels.SelectionKey;

/**
 * {@link IoRegistration} implementation that is used by {@link NioIoHandler}.
 */
public interface NioIoRegistration extends IoRegistration {
    /**
     * The underlying {@link SelectionKey}
     *
     * @return  the selection key.
     */
    SelectionKey selectionKey();

    /**
     * Update the {@link NioIoOps} for this registration.
     *
     * @param ops   the {@link NioIoOps} to use.
     */
    void updateInterestOps(NioIoOps ops);

    /**
     * The used {@link NioIoOps} for this registration.
     *
     * @return  ops.
     */
    NioIoOps interestOps();

    @Override
    void cancel();

    @Override
    NioIoHandler ioHandler();
}
