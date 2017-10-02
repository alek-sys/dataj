package org.dataj.test.matchers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Optional;

class FieldDeclarationMatcher extends BaseMatcher<CompilationUnit> {
    private final String className;
    private final String name;
    private final String typeName;
    private final boolean isPrivate;

    FieldDeclarationMatcher(String className, String name, String typeName, boolean isPrivate) {
        this.className = className;
        this.name = name;
        this.typeName = typeName;
        this.isPrivate = isPrivate;
    }

    @Override
    public boolean matches(Object item) {
        CompilationUnit cu = (CompilationUnit) item;

        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = cu.getClassByName(className).get();
        Optional<FieldDeclaration> fieldDeclaration = classOrInterfaceDeclaration.getFieldByName(name);

        if (fieldDeclaration.isPresent()) {
            boolean matches = fieldDeclaration.get().getElementType().asString().equals(typeName);
            if (isPrivate) {
                matches = matches && fieldDeclaration.get().getModifiers().contains(Modifier.PRIVATE);
            }

            return matches;
        }

        return false;
    }

    @Override
    public void describeTo(Description description) {

    }
}
