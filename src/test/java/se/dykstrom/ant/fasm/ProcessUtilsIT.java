/*
 * Copyright 2016-2021 Johan Dykstrom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.dykstrom.ant.fasm;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProcessUtilsIT {

    @Test
    public void testSetUpProcess() throws Exception {
        Process process = null;
        try {
            // Start process and wait for it to finish
            process = ProcessUtils.setUpProcess("fasm");
            assertFalse(process.isAlive());

            String output = ProcessUtils.readOutput(process);
            assertTrue(output.contains("flat assembler"));
            assertTrue(output.contains("optional settings"));
        } finally {
            if (process != null) {
                ProcessUtils.tearDownProcess(process);
            }
        }
    }
}
