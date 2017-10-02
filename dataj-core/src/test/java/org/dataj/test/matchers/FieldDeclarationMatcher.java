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
