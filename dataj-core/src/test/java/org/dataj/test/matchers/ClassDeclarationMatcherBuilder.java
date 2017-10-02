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

public class ClassDeclarationMatcherBuilder implements MatcherBuilder {

    private final String className;

    private ClassDeclarationMatcherBuilder(String className) {
        this.className = className;
    }

    public static ClassDeclarationMatcherBuilder classNamed(String className) {
        return new ClassDeclarationMatcherBuilder(className);
    }

    @Override
    public BaseMatcher<CompilationUnit> build(CompilationUnit compilationUnit) {
        return new ClassDeclarationMatcher(compilationUnit, className);
    }
}
