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
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.PatternSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * Compiles an assembly code source tree using the <a href="http://flatassembler.net">flat assembler</a>.
 * The source and destination directory will be recursively scanned for assembly source files to compile.
 * Only source files that have no corresponding output file, or where the output file is older than the
 * source file will be compiled.
 *
 * @author Johan Dykstrom
 */
@SuppressWarnings("unused,WeakerAccess")
public class Fasm extends MatchingTask {

    private static final String FAIL_MSG = "Compile failed; see the compiler error output for details.";

    private static final String DEFAULT_INCLUDES = "**/*.asm";

    // Task attributes
    private String compiler = "fasm";
    private String destDir;
    private String errorProperty;
    private boolean failOnError = true;
    private Integer memory;
    private Integer passes;
    private String srcDir;
    private String updatedProperty;

    // Nested elements
    private final List<CompilerArg> compilerArgs = new ArrayList<>();

    /** Set to true if there are errors. */
    private boolean errors;

    /** Set to true if any file has been updated (compiled). */
    private boolean updated;

    /** Keeps track of any include(s) configurations. */
    private boolean includeConfigured;

    @Override
    public void setIncludes(String includes) {
        includeConfigured = true;
        super.setIncludes(includes);
    }

    @Override
    public PatternSet.NameEntry createInclude() {
        includeConfigured = true;
        return super.createInclude();
    }

    /**
     * Sets the optional compiler attribute.
     */
    public void setCompiler(String compiler) {
        this.compiler = compiler;
    }

    /**
     * Sets the optional destination directory attribute.
     */
    public void setDestDir(String destDir) {
        this.destDir = destDir;
    }

    /**
     * Sets the optional "error property" attribute.
     */
    public void setErrorProperty(String errorProperty) {
        this.errorProperty = errorProperty;
    }

    /**
     * Sets the optional "fail on error" attribute.
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    /**
     * Sets the optional memory attribute.
     */
    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    /**
     * Sets the optional passes attribute.
     */
    public void setPasses(Integer passes) {
        this.passes = passes;
    }

    /**
     * Sets the mandatory source directory attribute.
     */
    public void setSrcDir(String srcDir) {
        this.srcDir = srcDir;
    }

    /**
     * Sets the optional "updated property" attribute.
     */
    public void setUpdatedProperty(String updatedProperty) {
        this.updatedProperty = updatedProperty;
    }

    /**
     * Adds a nested compiler argument.
     */
    public void addConfiguredCompilerArg(CompilerArg compilerArg) {
        compilerArgs.add(compilerArg);
    }

    @Override
    public void execute() throws BuildException {
        setUp();

        List<String> includedFiles = getIncludedFiles(srcDir);

        // Map source filename (including path) to destination filename (including path)
        Map<Path, Path> map = includedFiles.stream().collect(toMap(this::toSrcPath, this::toDestPath));

        // Make sure all destination directories exist
        map.values().forEach(FileUtils::makeDirectory);

        // Find out the source files to compile, and compile them
        map.entrySet().stream()
                .filter(entry -> FileUtils.needsRecompilation(entry.getKey(), entry.getValue()))
                .forEach(this::compile);

        tearDown();
    }

    private void setUp() {
        if (destDir == null) {
            destDir = srcDir;
        }
    }

    private void tearDown() {
        if (errors) {
            if (errorProperty != null) {
                getProject().setNewProperty(errorProperty, "true");
            }
            if (failOnError) {
                throw new BuildException(FAIL_MSG, getLocation());
            } else {
                log(FAIL_MSG, Project.MSG_INFO);
            }
        } else if (updated) {
            if (updatedProperty != null) {
                getProject().setNewProperty(updatedProperty, "true");
            }
        }
    }

    /**
     * Scans the source directory recursively, and returns a list of all files that match
     * the configured includes and excludes patterns. The returned filenames are relative
     * the source directory.
     */
    private List<String> getIncludedFiles(String srcDir) {
        if (!includeConfigured) {
            setIncludes(DEFAULT_INCLUDES);
        }
        return Arrays.asList(getDirectoryScanner(new File(srcDir)).getIncludedFiles());
    }

    /**
     * Returns the source filename, including path.
     */
    private Path toSrcPath(String srcFilename) {
        return Paths.get(srcDir, srcFilename);
    }

    /**
     * Returns the destination path, matching the given source filename.
     */
    private Path toDestPath(String srcFilename) {
        String destFileType = FileUtils.getDestFileType(Paths.get(srcDir, srcFilename));
        return FileUtils.getDestPath(srcFilename, Paths.get(destDir), destFileType);
    }

    /**
     * Compiles the source file specified by {@code entry} to the destination file specified by the same.
     */
    private void compile(Map.Entry<Path, Path> entry) {
        log("Building file: " + entry.getKey(), Project.MSG_INFO);

        Process process;
        try {
            String srcFile = entry.getKey().toString();
            String destFile = entry.getValue().toString();
            process = ProcessUtils.setUpProcess(buildArgs(srcFile, destFile));
        } catch (IOException | InterruptedException e) {
            throw new BuildException(e.getMessage(), e);
        }

        updated = true;
        if (process.exitValue() != 0) {
            log(FAIL_MSG, Project.MSG_ERR);
            log(ProcessUtils.readOutput(process), Project.MSG_INFO);
            errors = true;
        }

        ProcessUtils.tearDownProcess(process);
    }

    /**
     * Returns an array of arguments used to create the build process.
     */
    private String[] buildArgs(String srcFile, String destFile) {
        List<String> args = new ArrayList<>();
        args.add(compiler);
        if (memory != null) {
            args.add("-m");
            args.add(memory.toString());
        }
        if (passes != null) {
            args.add("-p");
            args.add(passes.toString());
        }
        compilerArgs.stream().flatMap(this::parseCompilerArg).forEach(args::add);
        args.add(srcFile);
        args.add(destFile);
        return args.toArray(new String[args.size()]);
    }

    /**
     * Parses the given compiler arg, and returns a stream that emits the different
     * parts of the compiler arg.
     */
    private Stream<String> parseCompilerArg(CompilerArg compilerArg) {
        return Arrays.stream(compilerArg.getValue().split("\\s+"));
    }
}
