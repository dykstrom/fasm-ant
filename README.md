<div style="text-align: center">

[![Build Status](https://travis-ci.com/dykstrom/fasm-ant.svg?branch=master)](https://travis-ci.com/dykstrom/fasm-ant)
![GitHub all releases](https://img.shields.io/github/downloads/dykstrom/fasm-ant/total)
![GitHub](https://img.shields.io/github/license/dykstrom/fasm-ant)

</div>

# fasm-ant

Project fasm-ant is a custom Ant task for building [flat assembler](http://flatassembler.net)
assembly files. It is designed to work like the built-in javac task, but with parameters
tailored for the flat assembler. With fasm-ant you can build a tree of source files, leaving
alone those that have not changed since the last build. Being an Ant project, fasm-ant is of
course built with ~~Ant~~ Maven itself.

### System requirements

*Minimum:* Java 8 and Ant 1.7.1.  
*Recommended:* Java 11 and Ant 1.10.9.

Tested on Windows 10 and Ubuntu Linux.

### Installation

Download the latest zip file from the GitHub [releases page](https://github.com/dykstrom/fasm-ant/releases),
unzip it, and copy the jar file to your Ant lib directory. That's it!

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
    <th style="text-align: left">Attribute</th>
    <th style="text-align: left">Description</th>
    <th style="text-align: left">Required</th>
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
    <td>excludes</td>
    <td>comma- or space-separated list of files (may be specified using wildcard patterns)
    that must be excluded; no files (except default excludes) are excluded when omitted</td>
    <td>No</td>
  </tr>
  <tr>
    <td>failonerror</td>
    <td>indicates whether compilation errors will fail the build (default is true)</td>
    <td>No</td>
  </tr>
  <tr>
    <td>includes</td>
    <td>comma- or space-separated list of files (may be specified using wildcard patterns)
    that must be included; all .asm files are included when omitted</td>
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
    <td>the property to set (to the value "true") if compilation has taken place and has
    been successful</td>
    <td>No</td>
  </tr>
</table>

### Parameters specified as nested elements

This task forms an implicit [FileSet](https://ant.apache.org/manual/Types/fileset.html)
and supports most attributes of &lt;fileset&gt; (dir becomes srcdir) as well as the
nested &lt;include&gt;, &lt;exclude&gt; and &lt;patternset&gt; elements.

##### compilerarg

You can specify additional command line arguments for the compiler with nested
&lt;compilerarg&gt; elements. The &lt;compilerarg&gt; element has exactly one
attribute.

<table>
  <tr>
    <th style="text-align: left">Attribute</th>
    <th style="text-align: left">Description</th>
    <th style="text-align: left">Required</th>
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
