package org.dataj.test.matchers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Optional;

class ConstructorDeclarationMatcher extends BaseMatcher<CompilationUnit> {
    private final CompilationUnit compilationUnit;
    private final String className;
    private final String[] params;

    ConstructorDeclarationMatcher(CompilationUnit compilationUnit, String className, String ...params) {
        this.compilationUnit = compilationUnit;
        this.className = className;
        this.params = params != null ? params : new String[0];
    }

    @Override
    public boolean matches(Object item) {
        Optional<ClassOrInterfaceDeclaration> referenceClass = compilationUnit.getClassByName(className);
        Optional<ClassOrInterfaceDeclaration> actualClass = ((CompilationUnit) item).getClassByName(className);

        if (referenceClass.isPresent() && actualClass.isPresent()) {
            Optional<ConstructorDeclaration> referenceConstructor = referenceClass.get().getConstructorByParameterTypes(params);
            Optional<ConstructorDeclaration> actualConstructor = actualClass.get().getConstructorByParameterTypes(params);

            return referenceConstructor.isPresent()
                    && actualConstructor.isPresent()
                    && referenceConstructor.get().equals(actualConstructor.get());
        }

        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.compilationUnit.toString());
    }
}
