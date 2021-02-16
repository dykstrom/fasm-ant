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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Contains static utility methods related to format directives and file types.
 *
 * @author Johan Dykstrom
 */
final class FormatUtils {

    private static final Pattern PATTERN_BINARY = Pattern.compile("^binary$");
    private static final Pattern PATTERN_BINARY_AS = Pattern.compile("^binary\\s+as\\s+'(.*)'$");
    private static final Pattern PATTERN_COFF = Pattern.compile("^.*coff$");
    private static final Pattern PATTERN_ELF = Pattern.compile("^elf.*$");
    private static final Pattern PATTERN_ELF_EXE = Pattern.compile("^elf(64)*\\s+executable.*$");
    private static final Pattern PATTERN_FORMAT = Pattern.compile("^\\s*format\\s+(.*)$");
    private static final Pattern PATTERN_MZ = Pattern.compile("^mz.*$");
    private static final Pattern PATTERN_PE = Pattern.compile("^pe(64)*.*$");
    private static final Pattern PATTERN_PE_DLL = Pattern.compile("^pe(64)*.*dll.*$");

    private static final String TYPE_BIN = ".bin";
    private static final String TYPE_DLL = ".dll";
    private static final String TYPE_EMPTY = "";
    private static final String TYPE_EXE = ".exe";
    private static final String TYPE_O = ".o";
    private static final String TYPE_OBJ = ".obj";

    private FormatUtils() { }

    /**
     * Returns the first format found by parsing the given stream of strings.
     * For example, if the directive "format PE console" is found in the stream,
     * the string "PE console" will be returned. If no format directive is
     * found, the default format "binary" is returned.
     *
     * @param lines A stream of strings to parse, typically lines read from a source file.
     * @return The format directive found.
     */
    static String getFormat(Stream<String> lines) {
        return lines
                .map(PATTERN_FORMAT::matcher)
                .filter(Matcher::matches)
                .map(matcher -> matcher.group(1))
                .findFirst()
                .orElse("binary")
                .toLowerCase()
                .trim();
    }

    /**
     * Returns the file type (the file extension) corresponding to the given {@code format}.
     * For example, this method will return ".exe" if the given format is "pe console".
     *
     * @param format The format to parse.
     * @return The file type, for example ".exe" or ".dll".
     */
    static String getFileType(String format) {
        Matcher matcher;

        matcher = PATTERN_BINARY_AS.matcher(format);
        if (matcher.matches()) {
            return "." + matcher.group(1);
        }

        matcher = PATTERN_BINARY.matcher(format);
        if (matcher.matches()) {
            return TYPE_BIN;
        }

        matcher = PATTERN_PE_DLL.matcher(format);
        if (matcher.matches()) {
            return TYPE_DLL;
        }

        matcher = PATTERN_PE.matcher(format);
        if (matcher.matches()) {
            return TYPE_EXE;
        }

        matcher = PATTERN_MZ.matcher(format);
        if (matcher.matches()) {
            return TYPE_EXE;
        }

        matcher = PATTERN_COFF.matcher(format);
        if (matcher.matches()) {
            return TYPE_OBJ;
        }

        matcher = PATTERN_ELF_EXE.matcher(format);
        if (matcher.matches()) {
            return TYPE_EMPTY;
        }

        matcher = PATTERN_ELF.matcher(format);
        if (matcher.matches()) {
            return TYPE_O;
        }

        throw new BuildException("Cannot parse format '" + format + "'");
    }
}
