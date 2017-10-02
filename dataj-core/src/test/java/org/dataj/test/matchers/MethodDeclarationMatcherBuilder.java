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
import com.google.common.base.Strings;
import org.hamcrest.BaseMatcher;

public class MethodDeclarationMatcherBuilder implements MatcherBuilder {

    private final String methodName;
    private String[] params;
    private String className;

    public static MethodDeclarationMatcherBuilder method(String methodName) {
        return new MethodDeclarationMatcherBuilder(methodName);
    }

    private MethodDeclarationMatcherBuilder(String methodName) {
        this.methodName = methodName;
    }

    public MethodDeclarationMatcherBuilder withParams(String ...params) {
        this.params = params;
        return this;
    }

    public MethodDeclarationMatcherBuilder ofClass(String className) {
        this.className = className;
        return this;
    }

    @Override
    public BaseMatcher<CompilationUnit> build(CompilationUnit compilationUnit) {
        if (Strings.isNullOrEmpty(className) || Strings.isNullOrEmpty(methodName)) {
            throw new IllegalArgumentException(
                "Cannot build method matcher without class name and method name, use method(<name>).ofClass(<name>)");
        }

        return new MethodDeclarationMatcher(compilationUnit, className, methodName, params);
    }
}
