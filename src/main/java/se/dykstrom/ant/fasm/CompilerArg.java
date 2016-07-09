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

/**
 * A lightweight container for compiler args, supporting only values.
 *
 * @author Johan Dykstrom
 */
@SuppressWarnings("unused")
public class CompilerArg {

    private String value;

    /**
     * Sets the value property of the compiler arg.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns the value property of the compiler arg.
     */
    public String getValue() {
        return value;
    }
}
