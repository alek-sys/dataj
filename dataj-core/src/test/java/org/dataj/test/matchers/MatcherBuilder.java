package org.dataj.test.matchers;

import com.github.javaparser.ast.CompilationUnit;
import org.hamcrest.BaseMatcher;

interface MatcherBuilder {
    BaseMatcher<CompilationUnit> build(CompilationUnit compilationUnit);
}
