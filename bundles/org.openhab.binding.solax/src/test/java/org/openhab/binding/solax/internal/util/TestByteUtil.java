/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.solax.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

/**
 * The {@link TestByteUtil} Simple test that tests the methods of the ByteUtil
 *
 * @author Konstantin Polihronov - Initial contribution
 */
@NonNullByDefault
public class TestByteUtil {
    @Test
    public void testRead32BitSigned() {
        assertEquals(2, ByteUtil.read32BitSigned((short) 0, (short) 2));
        assertEquals(65538, ByteUtil.read32BitSigned((short) 1, (short) 2));
        assertEquals(65536, ByteUtil.read32BitSigned((short) 1, (short) 0));
        assertEquals(65536, ByteUtil.read32BitSigned((short) 1, (short) 0));
    }
}
