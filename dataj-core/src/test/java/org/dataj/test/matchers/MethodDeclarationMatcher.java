/*
 * Copyright 2017 Alexey Nesterov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
