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
