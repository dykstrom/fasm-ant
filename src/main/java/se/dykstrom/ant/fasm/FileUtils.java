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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Contains static utility methods related to file management.
 *
 * @author Johan Dykstrom
 */
final class FileUtils {

    private FileUtils() { }

    /**
     * Returns {@code true} if the given source file needs recompilation.
     * A source file needs to be compiled if either the destination file
     * does not exist, or the source file is newer than the destination file.
     *
     * @param srcFile The source file.
     * @param destFile The destination file.
     * @return True if the source file needs recompilation.
     */
    public static boolean needsRecompilation(Path srcFile, Path destFile) {
        return Files.notExists(destFile) || isNewer(srcFile, destFile);
    }

    /**
     * Returns {@code true} if {@code file1} is newer than {@code file2}.
     * If any of the files does not exist, this method throws an exception.
     *
     * @param file1 The first file.
     * @param file2 The second file.
     * @return True if the first file is newer than the second file.
     * @throws BuildException If any of the files does not exist.
     */
    public static boolean isNewer(Path file1, Path file2) {
        try {
            return Files.getLastModifiedTime(file1).compareTo(Files.getLastModifiedTime(file2)) > 0;
        } catch (IOException e) {
            throw new BuildException("Cannot check file modification time", e);
        }
    }

    /**
     * Creates the directory of the file specified in {@code file} if it does not exist already.
     *
     * @param file The file to create a directory for.
     * @throws BuildException If the directory could not be created.
     */
    public static void makeDirectory(Path file) {
        Path directory = file.getParent();

        if (directory != null && Files.notExists(directory)) {
            try {
                Files.createDirectories(directory);
                System.out.println("Created dir: " + directory);
            } catch (IOException e) {
                throw new BuildException("Cannot create destination directory: " + directory, e);
            }
        }
    }

    /**
     * Finds out the file type (the file extension) of the destination file by analyzing
     * the source file given by {@code srcFile}.
     *
     * @param srcFile The source file.
     * @return The destination file type, for example ".exe".
     */
    public static String getDestFileType(Path srcFile) {
        try {
            Stream<String> lines = Files.lines(srcFile);
            String format = FormatUtils.getFormat(lines);
            return FormatUtils.getFileType(format);
        } catch (IOException e) {
            throw new BuildException("Cannot read source file '" + srcFile + "'", e);
        }
    }

    /**
     * Returns the destination path matching the given source filename and destination directory.
     * The source filename can be a simple filename, or it may start with a relative path component.
     * The file extension should include the dot if applicable.
     *
     * @param srcFilename The source filename.
     * @param destDirectory The destination directory.
     * @param destExtension The extension to use for the destination file.
     */
    public static Path getDestPath(String srcFilename, Path destDirectory, String destExtension) {
        return destDirectory.resolve(getBasename(srcFilename) + destExtension);
    }

    /**
     * Returns the basename, that is, the filename without extension, for the given source {@code filename}.
     *
     * @param filename The source filename.
     * @return The basename for the given filename.
     */
    public static String getBasename(String filename) {
        int index = filename.lastIndexOf(".");
        return index != -1 ? filename.substring(0, index) : filename;
    }
}
