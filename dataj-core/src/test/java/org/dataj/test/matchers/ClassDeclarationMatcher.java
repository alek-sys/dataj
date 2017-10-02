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
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Optional;

class ClassDeclarationMatcher extends BaseMatcher<CompilationUnit> {
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
