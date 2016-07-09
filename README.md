# fasm-ant

Project fasm-ant is a custom Ant task for building [flat assembler](http://flatassembler.net)
assembly files. It is designed to work like the built-in javac task, but with parameters
tailored for the flat assembler. With fasm-ant you can build a tree of source files, leaving
alone those that have not changed since the last build. Being an Ant project, fasm-ant is of
course built with ~~Ant~~ Maven itself.

### System requirements

You need Java 8 and Ant 1.7 or later to use fasm-ant. It has only been tested on Windows, but
with Java's old promise "write once, run anywhere" it might just work on Linux as well. :-)

### Installation

Download the latest fasm-ant jar file and drop it in your Ant lib directory. That's it!

### Configuration

To use the fasm-ant task in your build file, you first need to define it with taskdef. Put
something like this in your build file:

    <taskdef name="fasm" classname="se.dykstrom.ant.fasm.Fasm"/>

Now you can use fasm-ant as any other Ant task:

    <fasm srcdir="${src.dir}" destdir="${bin.dir}" includes="**/*.asm"/>

### Parameters

As said above, fasm-ant is designed to work like the built-in javac task. This means that
many of the parameters that can be used with javac can also be used with fasm-ant. The table
below lists all available fasm-ant parameters.

<table>
  <tr>
    <th align="left">Attribute</th>
    <th align="left">Description</th>
    <th align="left">Required</th>
  </tr>
  <tr>
    <td>compiler</td>
    <td>the compiler command used to run the flat assembler (default is "fasm")</td>
    <td>No</td>
  </tr>
  <tr>
    <td>destdir</td>
    <td>the destination directory (default is same as source directory)</td>
    <td>No</td>
  </tr>
  <tr>
    <td>errorproperty</td>
    <td>the property to set (to the value "true") if compilation fails</td>
    <td>No</td>
  </tr>
  <tr>
    <td>failonerror</td>
    <td>indicates whether compilation errors will fail the build (default is true)</td>
    <td>No</td>
  </tr>
  <tr>
    <td>memory</td>
    <td>the limit in kilobytes for the memory available to the assembler</td>
    <td>No</td>
  </tr>
  <tr>
    <td>passes</td>
    <td>the maximum allowed number of passes</td>
    <td>No</td>
  </tr>
  <tr>
    <td>srcdir</td>
    <td>the source directory</td>
    <td>Yes</td>
  </tr>
  <tr>
    <td>updatedproperty</td>
    <td>the property to set (to the value "true") if compilation has taken place and has been successful</td>
    <td>No</td>
  </tr>
</table>

### Parameters specified as nested elements

##### compilerarg

You can specify additional command line arguments for the compiler with nested
&lt;compilerarg&gt; elements. The &lt;compilerarg&gt; element has exactly one
attribute.

<table>
  <tr>
    <th align="left">Attribute</th>
    <th align="left">Description</th>
    <th align="left">Required</th>
  </tr>
  <tr>
    <td>value</td>
    <td>a single command-line argument; can contain space characters</td>
    <td>Yes</td>
  </tr>
</table>

### Example

The following is an example of a very simple build file that uses fasm-ant.

    <?xml version="1.0" encoding="ISO-8859-1"?>

    <project name="example" default="build" basedir=".">

        <!-- Properties -->
        <property name="src.dir" value="src"/>
        <property name="bin.dir" value="bin"/>

        <!-- Targets -->
        <target name="declare">
            <taskdef name="fasm" classname="se.dykstrom.ant.fasm.Fasm"/>
        </target>

        <target name="build" depends="declare">
            <fasm srcdir="${src.dir}" destdir="${bin.dir}" includes="**/*.asm">
                <compilerarg value="-d name=value"/>
            </fasm>
        </target>
    </project>

Note that this document only describes how to use fasm-ant. For information on how to use
the flat assembler itself, please visit [flatassembler.net](http://flatassembler.net).
