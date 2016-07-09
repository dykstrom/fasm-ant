package se.dykstrom.ant.fasm;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestProcessUtils {

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
