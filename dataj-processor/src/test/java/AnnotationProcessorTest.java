import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.dataj.AnnotationProcessor;
import org.junit.Test;

import java.nio.charset.Charset;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class AnnotationProcessorTest {

    @Test
    public void shouldGenerateCompanionDataClass() {
        Compilation compilation = compile("@Data class Test {}");
        assertThat(compilation).succeeded();
    }

    @Test
    public void shouldGenerateClassWithSuffix() {
        Compilation compilation = compile("@Data class Test {}");

        assertThat(compilation)
                .generatedSourceFile("TestData")
                .contentsAsString(Charset.defaultCharset())
                .isNotEmpty();
    }

    @Test
    public void shouldExtendProvidedClass() throws Exception {
        Compilation compilation = compile("@Data class Test {}");

        assertThat(compilation)
                .generatedSourceFile("TestData")
                .contentsAsString(Charset.defaultCharset())
                .contains("extends Test {");
    }

    @Test
    public void shouldGenerateGettersForNonFinalFields() {
        Compilation compilation = compile("@Data class Test { int field; }");

        assertThat(compilation)
                .generatedSourceFile("TestData")
                .contentsAsUtf8String()
                .containsMatch("public int getField\\(\\) \\{\\s+return field;\\s+}");
    }

    private Compilation compile(String source) {
        final String imports = "import org.dataj.Data; ";

        return javac()
            .withProcessors(new AnnotationProcessor())
            .compile(JavaFileObjects.forSourceString("Test", imports + source));
    }
}
