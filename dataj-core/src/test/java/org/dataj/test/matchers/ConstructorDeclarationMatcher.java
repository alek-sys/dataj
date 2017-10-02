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
