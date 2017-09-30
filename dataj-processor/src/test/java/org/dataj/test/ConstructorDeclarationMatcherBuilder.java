package org.dataj.test;

import com.github.javaparser.ast.CompilationUnit;

public class ConstructorDeclarationMatcherBuilder {
    private String className;
    private String[] params;

    public static ConstructorDeclarationMatcherBuilder constructor() {
        return new ConstructorDeclarationMatcherBuilder();
    }

    private ConstructorDeclarationMatcherBuilder() {

    }

    public ConstructorDeclarationMatcherBuilder ofClass(String className) {
        this.className = className;
        return this;
    }

    public ConstructorDeclarationMatcherBuilder withParams(String ...params) {
        this.params = params;
        return this;
    }

    public ConstructorDeclarationMatcher build(CompilationUnit compilationUnit) {
        return new ConstructorDeclarationMatcher(compilationUnit, this.className, params);
    }
}
