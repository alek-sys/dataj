package org.dataj.test.matchers;

import com.github.javaparser.ast.CompilationUnit;
import org.hamcrest.BaseMatcher;

public class FieldDeclarationMatcherBuilder implements MatcherBuilder {
    private static String fieldName;
    private String typeName;
    private boolean isPrivate;
    private String className;

    public static FieldDeclarationMatcherBuilder fieldNamed(String fieldName) {
        FieldDeclarationMatcherBuilder.fieldName = fieldName;
        return new FieldDeclarationMatcherBuilder();
    }

    public FieldDeclarationMatcherBuilder ofType(String typeName) {
        this.typeName = typeName;
        return this;
    }

    public FieldDeclarationMatcherBuilder isPrivate() {
        this.isPrivate = true;
        return this;
    }

    public FieldDeclarationMatcherBuilder inClass(String className) {
        this.className = className;
        return this;
    }

    @Override
    public BaseMatcher<CompilationUnit> build(CompilationUnit compilationUnit) {
        return new FieldDeclarationMatcher(className, fieldName, typeName, isPrivate);
    }
}
