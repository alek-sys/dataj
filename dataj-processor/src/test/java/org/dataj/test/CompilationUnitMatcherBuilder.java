package org.dataj.test;

import com.github.javaparser.ast.CompilationUnit;
import org.hamcrest.BaseMatcher;

public class CompilationUnitMatcherBuilder {
    final private CompilationUnit compilationUnit;

    private CompilationUnitMatcherBuilder(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    public static CompilationUnitMatcherBuilder andCompilationUnit(CompilationUnit compilationUnit) {
        return new CompilationUnitMatcherBuilder(compilationUnit);
    }

    public BaseMatcher<CompilationUnit> hasSame(MatcherBuilder matcherBuilder) {
        return matcherBuilder.build(compilationUnit);
    }
}
