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
