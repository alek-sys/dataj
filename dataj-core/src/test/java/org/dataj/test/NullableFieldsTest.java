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
import static org.dataj.test.matchers.CompilationUnitMatcherBuilder.andCompilationUnit;
import static org.dataj.test.matchers.ConstructorDeclarationMatcherBuilder.constructor;
import static org.dataj.test.matchers.MethodDeclarationMatcherBuilder.method;
import static org.hamcrest.MatcherAssert.assertThat;

@DisplayName("when processing a class with JSR305 annotated fields")
class NullableFieldsTest {

    private CompilationUnit actualSource;
    private CompilationUnit referenceSource;

    @BeforeEach
    void setUp() throws IOException {
        String source = "package com.example; " +
                "import org.dataj.Data; " +
                "import javax.annotation.Nonnull;" +
                "import javax.annotation.Nullable;" +
                "@Data class NullableFields { @Nonnull String name; @Nullable Integer age; }";

        Compilation compilation = javac()
                .withProcessors(new AnnotationProcessor())
                .compile(
                        JavaFileObjects.forSourceString("NullableFields", source)
                );

        JavaFileObject outputFile = compilation.generatedSourceFile("com.example.NullableFieldsData").get();
        actualSource = JavaParser.parse(outputFile.openInputStream());
        referenceSource = JavaParser.parseResource("NullableFieldsData.java");
    }

    @Test
    @DisplayName("should apply JSR305 annotations to constructor parameters ")
    void testConstructorParams() {
        assertThat(actualSource, andCompilationUnit(referenceSource)
                .hasSame(constructor().ofClass("NullableFieldsData").withParams("String", "Integer")));
    }

    @Test
    @DisplayName("should apply JSR305 annotations to a getter")
    void testGetterAnnotation() {
        // nullable
        assertThat(actualSource, andCompilationUnit(referenceSource)
                .hasSame(method("getName").ofClass("NullableFieldsData")));

        // nonnull
        assertThat(actualSource, andCompilationUnit(referenceSource)
                .hasSame(method("getAge").ofClass("NullableFieldsData")));
    }

    @Test
    @DisplayName("should apply JSR305 annotations to a setter")
    void testSetterAnnotation() {
        // nullable
        assertThat(actualSource, andCompilationUnit(referenceSource)
                .hasSame(method("setName").ofClass("NullableFieldsData").withParams("String")));

        // nonnull
        assertThat(actualSource, andCompilationUnit(referenceSource)
                .hasSame(method("setAge").ofClass("NullableFieldsData").withParams("Integer")));
    }
}
