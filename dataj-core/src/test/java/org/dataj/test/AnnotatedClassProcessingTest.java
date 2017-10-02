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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.dataj.AnnotationProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.io.IOException;

import static com.google.testing.compile.Compiler.javac;
import static org.dataj.test.matchers.ClassDeclarationMatcherBuilder.classNamed;
import static org.dataj.test.matchers.CompilationUnitMatcherBuilder.andCompilationUnit;
import static org.hamcrest.MatcherAssert.assertThat;

@DisplayName("when processing a class with annotations")
class AnnotatedClassProcessingTest {

    private CompilationUnit referenceSource;
    private CompilationUnit actualSource;

    @BeforeEach
    void setUp() throws Exception {
        String source = "package com.example; import org.dataj.Data; @Entity @Data class Annotated { int age; }";

        Compilation compilation = javac()
                .withProcessors(new AnnotationProcessor())
                .compile(
                        JavaFileObjects.forResource("Entity.java"),
                        JavaFileObjects.forSourceString("Annotated", source)
                );

        JavaFileObject outputFile = compilation.generatedSourceFile("com.example.AnnotatedData").get();
        actualSource = JavaParser.parse(outputFile.openInputStream());
        referenceSource = JavaParser.parseResource("AnnotatedData.java");
    }

    @Test
    @DisplayName("should apply the same annotations to the generated class")
    void testClassAnnotations() throws IOException {
        assertThat(actualSource, andCompilationUnit(referenceSource)
                .hasSame(classNamed("AnnotatedData")));
    }
}