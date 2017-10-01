package org.dataj.test;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Optional;

public class ClassDeclarationMatcher extends BaseMatcher<CompilationUnit> {
    private final CompilationUnit compilationUnit;
    private final String className;

    ClassDeclarationMatcher(CompilationUnit compilationUnit, String className) {
        this.compilationUnit = compilationUnit;
        this.className = className;
    }

    @Override
    public boolean matches(Object item) {
        CompilationUnit actual = (CompilationUnit) item;
        Optional<ClassOrInterfaceDeclaration> actualClass = actual.getClassByName(className);
        Optional<ClassOrInterfaceDeclaration> expectedClass = compilationUnit.getClassByName(className);

        return actualClass.isPresent() && expectedClass.isPresent()
                && actualClass.get().getNameAsString().equals(expectedClass.get().getNameAsString())
                && actualClass.get().getModifiers().equals(expectedClass.get().getModifiers())
                && actualClass.get().getAnnotations().equals(expectedClass.get().getAnnotations());

    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.className);
    }
}
