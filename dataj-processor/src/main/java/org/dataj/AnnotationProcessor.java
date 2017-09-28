package org.dataj;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@SupportedAnnotationTypes("org.dataj.Data")
@SupportedSourceVersion(SourceVersion.RELEASE_9)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement element: annotations) {
            for (Element el: roundEnv.getElementsAnnotatedWith(element)) {
                TypeElement clazz = (TypeElement) el;
                writeDataClassFor(clazz);
            }
        }

        return false;
    }

    private void writeDataClassFor(TypeElement clazz) {
        String builderClassName = clazz.getSimpleName() + "Data";
        String builderFileName = clazz.getQualifiedName() + "Data";
        PackageElement packageElement = (PackageElement) clazz.getEnclosingElement();

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(builderClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        VariableElement[] variableElements = clazz.getEnclosedElements().stream()
                .filter(e -> e instanceof VariableElement)
                .map(e -> (VariableElement) e)
                .toArray(VariableElement[]::new);

        classBuilder.addMethod(buildConstructorSpec(variableElements));

        for (VariableElement variableElement : variableElements) {
            classBuilder.addField(buildField(variableElement));
            classBuilder.addMethod(buildGetterSpec(variableElement));
            classBuilder.addMethod(buildSetterSpec(variableElement));
        }

        JavaFile javaFile = JavaFile.builder(packageElement.getQualifiedName().toString(), classBuilder.build())
                .build();

        try {
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderFileName);
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                javaFile.writeTo(out);
            }
        } catch (IOException ex) {

        }
    }

    private FieldSpec buildField(VariableElement variableElement) {
        return FieldSpec.builder(
                TypeName.get(variableElement.asType()),
                variableElement.getSimpleName().toString(),
                Modifier.PRIVATE)
                .build();
    }

    private MethodSpec buildConstructorSpec(VariableElement[] variableElements) {
        MethodSpec.Builder builder = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        for (VariableElement variableElement : variableElements) {
            String name = variableElement.getSimpleName().toString();
            ParameterSpec build = ParameterSpec
                    .builder(TypeName.get(variableElement.asType()), name)
                    .build();

            builder.addParameter(build);
            builder.addStatement("this.$L = $L", name, name);
        }

        return builder.build();
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
        final ParameterSpec value = ParameterSpec.builder(TypeName.get(element.asType()), "value").build();

        return MethodSpec
                .methodBuilder(getMethodName("set", fieldName))
                .addParameter(value)
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$L = value", fieldName)
                .build();
    }

    private String getMethodName(String prefix, String fieldName) {
        final String methodName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        return prefix + methodName;
    }
}
