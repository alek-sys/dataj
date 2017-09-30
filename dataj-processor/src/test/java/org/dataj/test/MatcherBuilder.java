package org.dataj.test;

import com.github.javaparser.ast.CompilationUnit;
import org.hamcrest.BaseMatcher;

public interface MatcherBuilder {
    BaseMatcher<CompilationUnit> build(CompilationUnit compilationUnit);
}
