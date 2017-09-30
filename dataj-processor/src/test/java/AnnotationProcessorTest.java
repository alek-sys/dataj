import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
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
import static org.dataj.test.CompilationUnitMatcherBuilder.has;
import static org.dataj.test.ConstructorDeclarationMatcherBuilder.constructor;
import static org.dataj.test.FieldDeclarationMatcherBuilder.fieldNamed;
import static org.dataj.test.MethodDeclarationMatcherBuilder.method;
import static org.hamcrest.MatcherAssert.assertThat;

@DisplayName("when processing a class with a single non final field")
class AnnotationProcessorTest {

    final String fileHeader = "package com.example; import org.dataj.Data; import javax.annotation.Nonnull; ";
    final String filePath = "com.example.TestData";
    private Compilation compilation;
    private CompilationUnit referenceSource;
    private CompilationUnit actualSource;

    @BeforeEach
    void setUp() throws Exception {
        compilation = compile("@Data class Test { @Nonnull String name; int age; }");

        JavaFileObject outputFile = compilation.generatedSourceFile("com.example.TestData").get();
        actualSource = JavaParser.parse(outputFile.openInputStream());
        referenceSource = JavaParser.parseResource("TestData.java");
    }

    @Test @DisplayName("should successfully compile when an annotation applied")
    void testDataClass() {
        CompilationSubject.assertThat(compilation).succeeded();
    }

    @Test @DisplayName("should generate a public and final class")
    void testPublicFinalClass() {
        assertThat(actualSource, andCompilationUnit(referenceSource)
            .hasSame(classNamed("TestData")));
    }

    @Test @DisplayName("should generate backing fields for fields from initial class")
    void testFields() {
        assertThat(actualSource, has(fieldNamed("age").inClass("TestData").ofType("int").isPrivate()));
    }

    @Test @DisplayName("should generate getter for a field")
    void testGetters() {
        assertThat(actualSource, andCompilationUnit(referenceSource)
            .hasSame(method("getAge").ofClass("TestData")));
    }

    @Test @DisplayName("should generate setter for a field")
    void testSetters() {
        assertThat(actualSource, andCompilationUnit(referenceSource)
            .hasSame(method("setAge").withParams("int").ofClass("TestData")));
    }

    @Test @DisplayName("should generate an all-args constructors")
    void testConstructor() throws Exception {
        assertThat(actualSource, andCompilationUnit(referenceSource)
            .hasSame(constructor().ofClass("TestData").withParams("String", "int")));
    }

    @Test @DisplayName("should add annotation to the getter")
    void testAnnotation() throws Exception {
        final String getterWithAnnotationName = "getName";
        assertThat(actualSource, andCompilationUnit(referenceSource).hasSame(
            method(getterWithAnnotationName).ofClass("TestData")));
    }

    @Test @DisplayName("should generate hashcode method")
    void testHashCode() throws IOException {
        assertThat(actualSource, andCompilationUnit(referenceSource)
            .hasSame(method("hashCode").ofClass("TestData")));
    }

    @Test @DisplayName("should generate equals method")
    void testEquals() throws IOException {
        assertThat(actualSource, andCompilationUnit(referenceSource)
            .hasSame(method("equals").ofClass("TestData").withParams("Object")));
    }

    private void assertGeneratedSourceIncludes(String regex) {
        CompilationSubject.assertThat(compilation)
                .generatedSourceFile(filePath)
                .contentsAsUtf8String()
                .containsMatch(regex);
    }

    private Compilation compile(String source) {
        return javac()
            .withProcessors(new AnnotationProcessor())
            .compile(JavaFileObjects.forSourceString("Test", fileHeader + source));
    }
}
