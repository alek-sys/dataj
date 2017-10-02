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

package org.dataj;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@SupportedAnnotationTypes("org.dataj.Data")
@SupportedSourceVersion(SourceVersion.RELEASE_9)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    final private Set<String> JSR305_ANNOTATIONS =
            Sets.newHashSet("javax.annotation.Nonnull", "javax.annotation.Nullable", "javax.annotation.CheckForNull");

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement element : annotations) {
            for (Element el : roundEnv.getElementsAnnotatedWith(element)) {
                TypeElement clazz = (TypeElement) el;
                try {
                    writeDataClassFor(clazz);
                } catch (IOException e) {
                    Messager messager = processingEnv.getMessager();
                    String msg = String.format("dataj: %s", e.getMessage());
                    messager.printMessage(Diagnostic.Kind.WARNING, msg);
                }
            }
        }

        return false;
    }

    private void writeDataClassFor(TypeElement clazz) throws IOException {
        String builderClassName = clazz.getSimpleName() + "Data";
        String builderFileName = clazz.getQualifiedName() + "Data";
        PackageElement packageElement = (PackageElement) clazz.getEnclosingElement();

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(builderClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        copyAnnotations(clazz, classBuilder);

        VariableElement[] fieldElements = getFieldElements(clazz);

        if (fieldElements.length != 0) {
            classBuilder.addMethod(buildConstructorSpec(fieldElements));

            for (VariableElement fieldElement : fieldElements) {
                classBuilder.addField(buildField(fieldElement));
                classBuilder.addMethod(buildGetterSpec(fieldElement));
                classBuilder.addMethod(buildSetterSpec(fieldElement));
            }

            classBuilder.addMethod(buildHashcodeSpec(fieldElements));
            classBuilder.addMethod(buildEqualsSpec(fieldElements, builderClassName));
        }

        JavaFile javaFile = JavaFile
            .builder(packageElement.getQualifiedName().toString(), classBuilder.build())
            .build();

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderFileName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            javaFile.writeTo(out);
        }
    }

    private VariableElement[] getFieldElements(TypeElement clazz) {
        return clazz.getEnclosedElements().stream()
                .filter(e -> e instanceof VariableElement)
                .map(e -> (VariableElement) e)
                .toArray(VariableElement[]::new);
    }

    private void copyAnnotations(TypeElement clazz, TypeSpec.Builder classBuilder) {
        clazz.getAnnotationMirrors().forEach(annotation -> {
            final String annotationTypeName = annotation.getAnnotationType().toString();
            if (!annotationTypeName.equals(Data.class.getTypeName())) {
                classBuilder.addAnnotation(AnnotationSpec.get(annotation));
            }
        });
    }

    private MethodSpec buildEqualsSpec(VariableElement[] fields, String ownType) {
        final Object[] fieldNames = getFieldNames(fields);

        ArrayList<String> sourceLines = new ArrayList<>();
        for (int i = 1; i <= fieldNames.length; i++) {
            sourceLines.add(String.format("Objects.equals($%dL, other.$%dL)", i + 1, i + 1));
        }

        final String pattern = String.format(
                "if (this == o) return true;\n" +
                "if (o == null || getClass() != o.getClass()) return false;\n" +
                "$1L other = ($1L) o;\n" +
                "return %s",
                String.join(" && ", sourceLines));

        return MethodSpec.methodBuilder("equals")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.OBJECT, "o")
                .returns(TypeName.BOOLEAN)
                .addStatement(pattern, concatArrays(ownType, fieldNames))
                .build();
    }

    private MethodSpec buildHashcodeSpec(VariableElement[] variableElements) {
        final Object[] fieldNames = getFieldNames(variableElements);

        final List<String> strs = Collections.nCopies(variableElements.length, "$L");
        final String pattern = String.format("return $T.hash(%s)", String.join(", ", strs));

        return MethodSpec.methodBuilder("hashCode")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.INT)
                .addStatement(pattern, concatArrays(Objects.class, fieldNames))
                .build();
    }

    private Object[] getFieldNames(VariableElement[] variableElements) {
        return Arrays.stream(variableElements)
                .map(VariableElement::getSimpleName)
                .toArray();
    }

    private FieldSpec buildField(VariableElement variableElement) {
        return FieldSpec.builder(
                TypeName.get(variableElement.asType()),
                variableElement.getSimpleName().toString(),
                Modifier.PRIVATE)
                .build();
    }

    private MethodSpec buildConstructorSpec(VariableElement[] fieldElements) {
        MethodSpec.Builder builder = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        for (VariableElement fieldElement : fieldElements) {
            String name = fieldElement.getSimpleName().toString();
            builder.addParameter(getParameterSpec(fieldElement, name));
            builder.addStatement("this.$L = $L", name, name);
        }

        return builder.build();
    }

    private ParameterSpec getParameterSpec(VariableElement fieldElement, String name) {
        ParameterSpec.Builder paramBuilder = ParameterSpec
                .builder(TypeName.get(fieldElement.asType()), name);

        fieldElement.getAnnotationMirrors().forEach(annotationMirror -> {
            String annotationTypeName = annotationMirror.getAnnotationType().toString();
            if (JSR305_ANNOTATIONS.contains(annotationTypeName)) {
                paramBuilder.addAnnotation(AnnotationSpec.get(annotationMirror));
            }
        });

        return paramBuilder.build();
    }

    private MethodSpec buildGetterSpec(VariableElement element) {
        String fieldName = element.getSimpleName().toString();

        MethodSpec.Builder builder = MethodSpec
                .methodBuilder(getMethodName("get", fieldName))
                .returns(TypeName.get(element.asType()))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L", fieldName);

        element.getAnnotationMirrors().forEach(annotationMirror ->
                builder.addAnnotation(AnnotationSpec.get(annotationMirror)));

        return builder.build();
    }

    private MethodSpec buildSetterSpec(VariableElement element) {
        final String fieldName = element.getSimpleName().toString();

        return MethodSpec
                .methodBuilder(getMethodName("set", fieldName))
                .addParameter(getParameterSpec(element, "value"))
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.$L = value", fieldName)
                .build();
    }

    private String getMethodName(String prefix, String fieldName) {
        final String methodName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        return prefix + methodName;
    }

    private Object[] concatArrays(Object head, Object[] tail) {
        Object[] result = new Object[tail.length + 1];
        result[0] = head;
        System.arraycopy(tail, 0, result, 1, tail.length);

        return result;
    }
}
