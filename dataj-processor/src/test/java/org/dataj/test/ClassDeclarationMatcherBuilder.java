package org.dataj.test;

import com.github.javaparser.ast.CompilationUnit;
import org.hamcrest.BaseMatcher;

public class ClassDeclarationMatcherBuilder implements MatcherBuilder {

    private final String className;

    private ClassDeclarationMatcherBuilder(String className) {
        this.className = className;
    }

    public static ClassDeclarationMatcherBuilder classNamed(String className) {
        return new ClassDeclarationMatcherBuilder(className);
    }

    @Override
    public BaseMatcher<CompilationUnit> build(CompilationUnit compilationUnit) {
        return new ClassDeclarationMatcher(compilationUnit, className);
    }
}
