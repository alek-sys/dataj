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
import static org.dataj.test.ClassDeclarationMatcherBuilder.classNamed;
import static org.dataj.test.CompilationUnitMatcherBuilder.andCompilationUnit;
import static org.hamcrest.MatcherAssert.assertThat;

@DisplayName("when processing a class with annotations")
class AnnotatedClassProcessingTest {

    private Compilation compilation;
    private CompilationUnit referenceSource;
    private CompilationUnit actualSource;

    @BeforeEach
    void setUp() throws Exception {
        String source = "package com.example; import org.dataj.Data; @Entity @Data class Annotated { int age; }";

        compilation = javac()
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