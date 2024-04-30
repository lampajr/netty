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
package io.netty.channel.epoll;

import io.netty.channel.IoOps;

/**
 * Implementation of {@link IoOps} that is used by {@link EpollIoHandler} and so for epoll based transports.
 */
public final class EpollIoOps implements IoOps {

    static {
        // Need to ensure we load the native lib before trying to use the values in Native to construct the different
        // instances.
        Epoll.ensureAvailability();
    }

    /**
     * Interested in IO events that should be handled by accepting new connections
     */
    public static final EpollIoOps EPOLLOUT = new EpollIoOps(Native.EPOLLOUT);

    /**
     * Interested in IO events which should be handled by finish pending connect operations
     */
    public static final EpollIoOps EPOLLIN = new EpollIoOps(Native.EPOLLIN);

    /**
     * Interested in IO events which tell that the underlying channel is writable again.
     */
    public static final EpollIoOps EPOLLERR = new EpollIoOps(Native.EPOLLERR);

    /**
     * Interested in IO events which should be handled by reading data.
     */
    public static final EpollIoOps EPOLLRDHUP = new EpollIoOps(Native.EPOLLRDHUP);

    public static final EpollIoOps EPOLLET = new EpollIoOps(Native.EPOLLET);

    // Just use an array to store often used values.
    private static final EpollIoOps[] OPS;

    static {
        EpollIoOps all = new EpollIoOps(
                EPOLLOUT.value | EPOLLIN.value | EPOLLERR.value | EPOLLRDHUP.value);
        OPS = new EpollIoOps[all.value + 1];
        addToArray(OPS, EPOLLOUT);
        addToArray(OPS, EPOLLIN);
        addToArray(OPS, EPOLLERR);
        addToArray(OPS, EPOLLRDHUP);
        addToArray(OPS, all);
    }

    private static void addToArray(EpollIoOps[] array, EpollIoOps ops) {
        array[ops.value] = ops;
    }

    final int value;

    private EpollIoOps(int value) {
        this.value = value;
    }

    /**
     * Returns {@code true} if this {@link EpollIoOps} is a combination of the given {@link EpollIoOps}.
     * @param ops   the ops.
     * @return      {@code true} if a combination of the given.
     */
    public boolean contains(EpollIoOps ops) {
        return (value & ops.value) != 0;
    }

    /**
     * Return a {@link EpollIoOps} which is a combination of the current and the given {@link EpollIoOps}.
     *
     * @param ops   the {@link EpollIoOps} that should be added to this one.
     * @return      a {@link EpollIoOps}.
     */
    public EpollIoOps with(EpollIoOps ops) {
        if (contains(ops)) {
            return this;
        }
        return valueOf(value | ops.value());
    }

    /**
     * Return a {@link EpollIoOps} which is not a combination of the current and the given {@link EpollIoOps}.
     *
     * @param ops   the {@link EpollIoOps} that should be remove from this one.
     * @return      a {@link EpollIoOps}.
     */
    public EpollIoOps without(EpollIoOps ops) {
        if (!contains(ops)) {
            return this;
        }
        return valueOf(value & ~ops.value());
    }

    /**
     * Returns the underlying value of the {@link EpollIoOps}.
     *
     * @return value.
     */
    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EpollIoOps nioOps = (EpollIoOps) o;
        return value == nioOps.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    /**
     * Returns a {@link EpollIoOps} for the given value.
     *
     * @param   value the value
     * @return  the {@link EpollIoOps}.
     */
    public static EpollIoOps valueOf(int value) {
        final EpollIoOps ops;
        if (value > 0 && value < OPS.length) {
            ops = OPS[value];
            if (ops != null) {
                return ops;
            }
        } else if (value == EPOLLET.value) {
            return EPOLLET;
        }
        return new EpollIoOps(value);
    }
}
