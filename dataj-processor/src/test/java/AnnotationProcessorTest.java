import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.dataj.AnnotationProcessor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.List;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.hamcrest.CoreMatchers.is;

@DisplayName("when processing a class with a single non final field")
class AnnotationProcessorTest {

    final String fileHeader = "package com.example; import org.dataj.Data; ";
    final String filePath = "com.example.TestData";
    private Compilation compilation;

    @BeforeEach
    void setUp() throws Exception {
        compilation = compile("@Data class Test { int field; }");
    }

    @Test @DisplayName("should successfully compile when an annotation applied")
    void testDataClass() {
        assertThat(compilation).succeeded();
    }

    @Test @DisplayName("should generate a companion data class with default suffix")
    void testSuffix() {
        assertThat(compilation)
                .generatedSourceFile(filePath)
                .contentsAsUtf8String()
                .isNotEmpty();
    }

    @Test @DisplayName("should generate a public and final class")
    void testPublicFinalClass() {
        assertGeneratedSourceIncludes("public final class TestData");
    }

    @Test @DisplayName("should generate backing fields for fields from initial class")
    void testFields() {
        assertGeneratedSourceIncludes("private int field;");
    }

    @Test @DisplayName("should generate getter for a field")
    void testGetters() {
        assertGeneratedSourceIncludes("public int getField\\(\\) \\{\\s+return field;\\s+}");
    }

    @Test @DisplayName("should generate setter for a field")
    void testSetters() {
        assertGeneratedSourceIncludes("public void setField\\(int value\\) \\{\\s+field = value;\\s+}");
    }

    @Test @DisplayName("should generate an all-args constructors")
    void testConstructor() throws Exception {
        assertGeneratedSourceIncludes("public TestData\\(int field\\) \\{\\s+this.field = field;\\s+}");
    }

    @Test @DisplayName("should add annotation to the getter")
    void testAnnotation() throws Exception {
        compilation = compile("import javax.annotation.Nonnull; @Data class Test { @Nonnull int field; }");

        assertThat(compilation)
                .generatedSourceFile(filePath)
                .contentsAsUtf8String()
                .containsMatch("\\@Nonnull\\s+public int getField\\(\\) \\{\\s+return field;\\s+}");
    }

    @Test @DisplayName("should generate hashcode method")
    void testHashCode() throws IOException {
        Compilation twoFields = compile("@Data class Test { String name; int age; }");

        JavaFileObject javaFileObject = twoFields.generatedSourceFile("com.example.TestData").get();
        CompilationUnit reference = JavaParser.parseResource("TestData.java");
        CompilationUnit actual = JavaParser.parse(javaFileObject.openInputStream());

        ClassOrInterfaceDeclaration referenceClass = reference.getClassByName("TestData").get();
        ClassOrInterfaceDeclaration actualClass = actual.getClassByName("TestData").get();

        MatcherAssert.assertThat(methodsAreEqual(referenceClass, actualClass, "hashCode"), is(true));
    }

    @Test @DisplayName("should generate equals method")
    void testEquals() throws IOException {
        Compilation twoFields = compile("@Data class Test { String name; int age; }");

        JavaFileObject javaFileObject = twoFields.generatedSourceFile("com.example.TestData").get();
        CompilationUnit reference = JavaParser.parseResource("TestData.java");
        CompilationUnit actual = JavaParser.parse(javaFileObject.openInputStream());

        ClassOrInterfaceDeclaration referenceClass = reference.getClassByName("TestData").get();
        ClassOrInterfaceDeclaration actualClass = actual.getClassByName("TestData").get();

        org.hamcrest.MatcherAssert.assertThat(methodsAreEqual(referenceClass, actualClass, "equals", "Object"), is(true));
    }

    private boolean methodsAreEqual(
            ClassOrInterfaceDeclaration reference,
            ClassOrInterfaceDeclaration actual,
            String methodName,
            String ...paramTypes) {

        List<MethodDeclaration> referenceMethod = reference.getMethodsBySignature(methodName, paramTypes);
        List<MethodDeclaration> actualMethod = actual.getMethodsBySignature(methodName, paramTypes);

        return referenceMethod.equals(actualMethod);
    }

    private void assertGeneratedSourceIncludes(String regex) {
        assertThat(compilation)
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
