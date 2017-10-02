/*
 * Copyright 2017 Alexey Nesterov
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

package org.dataj.test;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.JavaFileObjects;
import org.dataj.AnnotationProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.testing.compile.Compiler.javac;

@DisplayName("when applying annotation to a class with no fields")
class EmptyClassTests {

    @Test
    @DisplayName("should compile successfully")
    void testEmptyClass() {
        String source = "package com.example; " +
                "import org.dataj.Data; " +
                "@Data class Test { }";

        Compilation compiled = javac()
                .withProcessors(new AnnotationProcessor())
                .compile(JavaFileObjects.forSourceString("Test", source));

        CompilationSubject.assertThat(compiled).succeeded();
    }
}
