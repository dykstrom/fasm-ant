package se.dykstrom.ant.fasm;

import org.apache.tools.ant.BuildException;
import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static se.dykstrom.ant.fasm.FormatUtils.getFileType;
import static se.dykstrom.ant.fasm.FormatUtils.getFormat;

public class TestFormatUtils {

    @Test
    public void testGetFormat() {
        String format0 = "PE console";
        String format1 = "PE64 console DLL";
        String format2 = "PE GUI 4.0";

        assertEquals(format0.toLowerCase(), getFormat(Stream.of("format " + format0)));
        assertEquals(format1.toLowerCase(), getFormat(Stream.of("entry main", "format " + format1)));
        assertEquals(format2.toLowerCase(), getFormat(Stream.of("format " + format2, "section '.data' data readable")));
    }

    @Test
    public void testGetFormat_NoFormat() {
        assertEquals("binary", getFormat(Stream.of("mov eax, 0")));
    }

    @Test
    public void testGetFileType() {
        assertEquals(".bin", getFileType("binary"));
        assertEquals(".foo", getFileType("binary as 'foo'"));
        assertEquals(".exe", getFileType("pe console"));
        assertEquals(".exe", getFileType("pe64 console"));
        assertEquals(".exe", getFileType("pe64 gui 4.0"));
        assertEquals(".exe", getFileType("pe64 gui 4.0 wdm"));
        assertEquals(".dll", getFileType("pe64 console dll"));
        assertEquals(".dll", getFileType("pe gui 4.0 dll at 7000000h"));
        assertEquals(".obj", getFileType("coff"));
        assertEquals(".obj", getFileType("ms coff"));
        assertEquals(".exe", getFileType("mz"));
        assertEquals(".o", getFileType("elf"));
        assertEquals(".o", getFileType("elf64"));
        assertEquals("", getFileType("elf executable"));
        assertEquals("", getFileType("elf64 executable 3"));
    }

    @Test(expected = BuildException.class)
    public void testGetFileType_Exception() {
        getFileType("foo");
    }
}
