package se.dykstrom.ant.fasm;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TestFasm {

    private static final String SRC_DIR = "src/test/asm";
    private static final String DEST_DIR = "target/test-binaries";

    private static final String SRC_FILE = SRC_DIR + "/pe64_console.asm";
    private static final String DEST_FILE = DEST_DIR + "/pe64_console.exe";

    private static final Path SRC_PATH = Paths.get(SRC_FILE);
    private static final Path DEST_PATH = Paths.get(DEST_FILE);

    private static final String ERROR_PROPERTY = "error";
    private static final String UPDATED_PROPERTY = "updated";

    private Project project;
    private Fasm fasm;

    @Before
    public void setUp() {
        assertTrue("Missing source file; check working directory", Files.exists(SRC_PATH));
        createProjectAndTask();
    }

    /**
     * Creates the Ant project and the Fasm task.
     */
    private void createProjectAndTask() {
        project = new Project();
        fasm = new Fasm();
        fasm.setProject(project);
        fasm.setSrcDir(SRC_DIR);
        fasm.setDestDir(DEST_DIR);
        fasm.setErrorProperty(ERROR_PROPERTY);
        fasm.setUpdatedProperty(UPDATED_PROPERTY);
    }

    @Test
    public void testExecute() throws Exception {
        // Remove destination file to force a recompile
        Files.deleteIfExists(DEST_PATH);

        // Test
        fasm.execute();

        // Expect no errors and at least one file updated
        assertNull(project.getProperty(ERROR_PROPERTY));
        assertEquals("true", project.getProperty(UPDATED_PROPERTY));
        // Expect that destination file exists again
        assertTrue(Files.exists(DEST_PATH));

        // Reset
        createProjectAndTask();

        // Test again without removing destination file
        fasm.execute();

        // Expect no errors and no file updated
        assertNull(project.getProperty(ERROR_PROPERTY));
        assertNull(project.getProperty(UPDATED_PROPERTY));
    }

    @Test
    public void testExecute_BuildError() throws Exception {
        // Remove destination file to force a recompile
        Files.deleteIfExists(DEST_PATH);

        // Set invalid memory option to force an error
        fasm.setMemory(0);

        // Test
        try {
            fasm.execute();
            fail("Expected BuildException");
        } catch (BuildException e) {
            // Expect an error (updated property does not matter)
            assertEquals("true", project.getProperty(ERROR_PROPERTY));
        }
    }
}
