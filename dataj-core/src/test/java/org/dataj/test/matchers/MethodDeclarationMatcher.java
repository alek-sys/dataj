package org.dataj.test.matchers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.List;
import java.util.Optional;

class MethodDeclarationMatcher extends BaseMatcher<CompilationUnit> {

    private final CompilationUnit referenceCompilationUnit;
    private final String methodName;
    private final String[] params;
    private final String className;

    MethodDeclarationMatcher(CompilationUnit referenceCompilationUnit, String classMame, String methodName, String[] params) {
        this.referenceCompilationUnit = referenceCompilationUnit;
        this.className = classMame;
        this.methodName = methodName;
        this.params = params;
    }

    @Override
    public boolean matches(Object item) {
        Optional<ClassOrInterfaceDeclaration> referenceClass = referenceCompilationUnit.getClassByName(className);
        Optional<ClassOrInterfaceDeclaration> actualClass = ((CompilationUnit) item).getClassByName(className);

        return referenceClass.isPresent()
                && actualClass.isPresent()
                && methodsAreEqual(referenceClass.get(), actualClass.get(), methodName, params);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.referenceCompilationUnit.toString());
    }

    private boolean methodsAreEqual(
            ClassOrInterfaceDeclaration reference,
            ClassOrInterfaceDeclaration actual,
            String methodName,
            String ...paramTypes) {

        if (paramTypes == null) paramTypes = new String[0];

        List<MethodDeclaration> referenceMethod = reference.getMethodsBySignature(methodName, paramTypes);
        List<MethodDeclaration> actualMethod = actual.getMethodsBySignature(methodName, paramTypes);

        return referenceMethod.equals(actualMethod);
    }
}
