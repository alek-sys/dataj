package org.dataj.test;

import com.github.javaparser.ast.CompilationUnit;
import com.google.common.base.Strings;
import org.hamcrest.BaseMatcher;

public class MethodDeclarationMatcherBuilder implements MatcherBuilder {

    private final String methodName;
    private String[] params;
    private String className;

    public static MethodDeclarationMatcherBuilder method(String methodName) {
        return new MethodDeclarationMatcherBuilder(methodName);
    }

    private MethodDeclarationMatcherBuilder(String methodName) {
        this.methodName = methodName;
    }

    public MethodDeclarationMatcherBuilder withParams(String ...params) {
        this.params = params;
        return this;
    }

    public MethodDeclarationMatcherBuilder ofClass(String className) {
        this.className = className;
        return this;
    }

    @Override
    public BaseMatcher<CompilationUnit> build(CompilationUnit compilationUnit) {
        if (Strings.isNullOrEmpty(className) || Strings.isNullOrEmpty(methodName)) {
            throw new IllegalArgumentException(
                "Cannot build method matcher without class name and method name, use method(<name>).ofClass(<name>)");
        }

        return new MethodDeclarationMatcher(compilationUnit, className, methodName, params);
    }
}
