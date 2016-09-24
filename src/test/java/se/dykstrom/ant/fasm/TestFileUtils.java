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

import org.apache.tools.ant.BuildException;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.*;
import static se.dykstrom.ant.fasm.FileUtils.*;

public class TestFileUtils {

    @Test
    public void testNeedsRecompilation() throws Exception {
        Path[] paths = createTempFiles();

        assertTrue(needsRecompilation(paths[1], paths[0]));
        assertFalse(needsRecompilation(paths[0], paths[1]));
    }

    @Test
    public void testNeedsRecompilation_NoDestFile() throws Exception {
        Path[] paths = createTempFiles();

        assertTrue(needsRecompilation(paths[0], Paths.get("does-not-exist.tmp")));
    }

    @Test
    public void testIsNewer() throws Exception {
        Path[] paths = createTempFiles();

        assertTrue(isNewer(paths[1], paths[0]));
        assertFalse(isNewer(paths[0], paths[1]));
        assertFalse(isNewer(paths[0], paths[0]));
    }

    @Test(expected = BuildException.class)
    public void testIsNewer_Exception() throws Exception {
        isNewer(Paths.get("does-not-exist.tmp"), Paths.get("does-not-exist.tmp"));
    }

    @Test
    public void testGetDestFileType() {
        assertEquals(".exe", FileUtils.getDestFileType(Paths.get("src/test/asm/pe64_console.asm")));
        assertEquals(".obj", FileUtils.getDestFileType(Paths.get("src/test/asm/coff.asm")));
    }

    @Test(expected = BuildException.class)
    public void testGetDestFileType_Exception() {
        FileUtils.getDestFileType(Paths.get("does-not-exist.asm"));
    }

    @Test
    public void testGetDestPath() {
        assertEquals(Paths.get("c:/Files/out/file.exe"), getDestPath("file.asm", Paths.get("c:/Files/out"), ".exe"));
        assertEquals(Paths.get("c:/Files/out/sub/file.exe"), getDestPath("sub/file.asm", Paths.get("c:/Files/out"), ".exe"));
    }

    @Test
    public void testGetBasename() {
        assertEquals("file", getBasename("file.asm"));
        assertEquals("file", getBasename("file"));
        assertEquals("c:/Temp/file", getBasename("c:/Temp/file.asm"));
        assertEquals("c:/Temp/file", getBasename("c:/Temp/file"));
        assertEquals("C:\\Temp\\file", getBasename("C:\\Temp\\file.asm"));
        assertEquals("C:\\Temp\\file", getBasename("C:\\Temp\\file"));
    }

    // -----------------------------------------------------------------------

    /**
     * Creates two temp files with some milliseconds in between.
     *
     * @return An array of two file paths, where the first file is older , and the second file is newer.
     * @throws Exception If file creation failed.
     */
    private static Path[] createTempFiles() throws Exception {
        Path[] paths = new Path[2];
        paths[0] = Files.createTempFile(null, null);
        Thread.sleep(10);
        paths[1] = Files.createTempFile(null, null);
        Arrays.stream(paths).forEach(path -> path.toFile().deleteOnExit());
        return paths;
    }
}
