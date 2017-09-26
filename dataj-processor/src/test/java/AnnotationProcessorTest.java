import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.dataj.AnnotationProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

@DisplayName("when processing a class with a single non final field")
class AnnotationProcessorTest {

    private Compilation compilation;

    @BeforeEach
    void setUp() throws Exception {
        compilation = compile("@Data class Test { int field; }");
    }

    @Test @DisplayName("should generate a companion data class")
    void testDataClass() {
        assertThat(compilation).succeeded();
    }

    @Test @DisplayName("should generate a companion data class with default suffix")
    void testSuffix() {
        assertThat(compilation)
                .generatedSourceFile("TestData")
                .contentsAsUtf8String()
                .isNotEmpty();
    }

    @Test @DisplayName("should generate a public and final class")
    void testPublicFinalClass() {
        assertGeneratedSourceIncludes("public final class TestData");
    }

    @Test @DisplayName("should extend an annotated class")
    void testExtendProvidedClass() throws Exception {
        assertGeneratedSourceIncludes("extends Test \\{");
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

    private void assertGeneratedSourceIncludes(String regex) {
        assertThat(compilation)
                .generatedSourceFile("TestData")
                .contentsAsUtf8String()
                .containsMatch(regex);
    }

    private Compilation compile(String source) {
        final String imports = "import org.dataj.Data; ";

        return javac()
            .withProcessors(new AnnotationProcessor())
            .compile(JavaFileObjects.forSourceString("Test", imports + source));
    }
}
