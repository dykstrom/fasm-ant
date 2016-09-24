/*
 * Copyright 2016 Johan Dykstrom
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Contains static utility methods related to process management.
 *
 * @author Johan Dykstrom
 */
@SuppressWarnings("SameParameterValue")
final class ProcessUtils {

    private ProcessUtils() { }

    /**
     * Sets up and returns a new compile process the executes the given {@code command}.
     */
    static Process setUpProcess(String... command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command).redirectErrorStream(true);
        Process process = builder.start();

        // Wait for the process to start and then end
        waitForStart(process, 5000, TimeUnit.MILLISECONDS);
        waitForEnd(process, 5000, TimeUnit.MILLISECONDS);

        // Return the already ended process
        return process;
    }

    /**
     * Tears down the given process.
     */
    static void tearDownProcess(Process process) {
        process.destroy();
    }

    /**
     * Causes the current thread to wait, if necessary, until the sub process represented by {@code process} has
     * started, or the specified waiting time elapses. If the sub process has already started, this method returns
     * immediately.
     *
     * @param process The process to wait for.
     * @param timeout The maximum wait time.
     * @param unit The time unit of the timeout argument.
     * @throws InterruptedException If the current thread is interrupted while waiting.
     * @throws IOException If an IO error occurs.
     * @see Process#waitFor(long, TimeUnit)
     */
    private static void waitForStart(Process process, long timeout, TimeUnit unit) throws InterruptedException, IOException {
        long start = System.nanoTime();
        long remaining = unit.toNanos(timeout);

        while (process.getInputStream().available() == 0 && remaining > 0) {
            Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(remaining) + 1, 10));
            remaining = unit.toNanos(timeout) - (System.nanoTime() - start);
        }
    }

    /**
     * The same as calling {@link Process#waitFor(long, TimeUnit)}.
     */
    private static void waitForEnd(Process process, long timeout, TimeUnit unit) throws InterruptedException {
        process.waitFor(timeout, unit);
    }

    /**
     * Reads all output that is available from the given {@code process}, and returns this as a single string.
     *
     * @param process The process to read from.
     * @return The process output.
     */
    static String readOutput(Process process) {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while (reader.ready()) {
                builder.append(reader.readLine()).append("\n");
            }
        } catch (IOException e) {
            builder.append(e.getMessage()).append("\n");
        }

        return builder.toString();
    }
}
