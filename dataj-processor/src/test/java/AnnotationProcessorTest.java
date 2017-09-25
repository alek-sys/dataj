import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.dataj.AnnotationProcessor;
import org.junit.Before;
import org.junit.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class AnnotationProcessorTest {

    private Compilation compilation;

    @Before
    public void setUp() throws Exception {
        compilation = compile("@Data class Test { int field; }");
    }

    @Test
    public void shouldGenerateCompanionDataClass() {
        assertThat(compilation).succeeded();
    }

    @Test
    public void shouldGenerateClassWithSuffix() {
        assertThat(compilation)
                .generatedSourceFile("TestData")
                .contentsAsUtf8String()
                .isNotEmpty();
    }

    @Test
    public void shouldGeneratePublicFinalClass() {
        assertGeneratedSourceIncludes("public final class TestData");
    }

    @Test
    public void shouldExtendProvidedClass() throws Exception {
        assertGeneratedSourceIncludes("extends Test \\{");
    }

    @Test
    public void shouldGenerateGettersForNonFinalFields() {
        assertGeneratedSourceIncludes("public int getField\\(\\) \\{\\s+return field;\\s+}");
    }

    @Test
    public void shouldGenerateSettersForNonFinalFields() {
        assertGeneratedSourceIncludes("public void setField\\(int value\\) \\{\\s+field = value;\\s+}");
    }

    @Test
    public void shouldGenerateConstructor() throws Exception {
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
