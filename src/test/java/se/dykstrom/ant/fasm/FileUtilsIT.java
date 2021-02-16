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

import org.apache.tools.ant.BuildException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static se.dykstrom.ant.fasm.FileUtils.*;

public class FileUtilsIT {

    /** Time to sleep between creating the test files. */
    private static long sleepTime = 10;

    @BeforeClass
    public static void setUpClass() {
        // We need longer sleep time on Linux because the file modification time is given in seconds
        if (Pattern.compile("[Ll]inux").matcher(System.getProperty("os.name")).find()) {
            sleepTime = 1000;
        }
    }

    @Test
    public void testNeedsRecompilation() throws Exception {
        Path[] paths = createTempFiles(sleepTime);

        assertTrue(needsRecompilation(paths[1], paths[0]));
        assertFalse(needsRecompilation(paths[0], paths[1]));
    }

    @Test
    public void testNeedsRecompilation_NoDestFile() throws Exception {
        Path[] paths = createTempFiles(10);

        assertTrue(needsRecompilation(paths[0], Paths.get("does-not-exist.tmp")));
    }

    @Test
    public void testIsNewer() throws Exception {
        Path[] paths = createTempFiles(sleepTime);

        assertTrue(isNewer(paths[1], paths[0]));
        assertFalse(isNewer(paths[0], paths[1]));
        assertFalse(isNewer(paths[0], paths[0]));
    }

    @Test(expected = BuildException.class)
    public void testIsNewer_Exception() {
        isNewer(Paths.get("does-not-exist.tmp"), Paths.get("does-not-exist.tmp"));
    }

    @Test
    public void testGetDestFileType() {
        assertEquals(".exe", FileUtils.getDestFileType(Paths.get("src/test/asm/pe64_console.asm")));
        assertEquals(".obj", FileUtils.getDestFileType(Paths.get("src/test/asm/coff.asm")));
        assertEquals("", FileUtils.getDestFileType(Paths.get("src/test/asm/elf64_executable.asm")));
    }

    @Test(expected = BuildException.class)
    public void testGetDestFileType_Exception() {
        FileUtils.getDestFileType(Paths.get("does-not-exist.asm"));
    }

    @Test
    public void testGetDestPath() {
        assertEquals(Paths.get("c:/Files/out/file.exe"), getDestPath("file.asm", Paths.get("c:/Files/out"), ".exe"));
        assertEquals(Paths.get("c:/Files/out/sub/file.exe"), getDestPath("sub/file.asm", Paths.get("c:/Files/out"), ".exe"));
        assertEquals(Paths.get("/usr/home/Files/out/sub/file"), getDestPath("sub/file.asm", Paths.get("/usr/home/Files/out"), ""));
    }

    // -----------------------------------------------------------------------

    /**
     * Creates two temp files with some milliseconds in between.
     *
     * @param millis The number of milliseconds to sleep between creating the files.
     * @return An array of two file paths, where the first file is older , and the second file is newer.
     * @throws Exception If file creation failed.
     */
    private static Path[] createTempFiles(long millis) throws Exception {
        Path[] paths = new Path[2];
        paths[0] = Files.createTempFile(null, null);
        Thread.sleep(millis);
        paths[1] = Files.createTempFile(null, null);
        Arrays.stream(paths).forEach(path -> path.toFile().deleteOnExit());
        return paths;
    }
}
