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
        PackageElement packageElement = (PackageElement) clazz.getEnclosingElement();

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(builderClassName)
                .superclass(TypeName.get(clazz.asType()))
                .addModifiers(Modifier.PUBLIC);

        clazz.getEnclosedElements().stream()
                .filter(e -> e instanceof VariableElement)
                .map(e -> (VariableElement)e)
                .map(this::buildMethodSpec)
                .forEach(classBuilder::addMethod);

        JavaFile javaFile = JavaFile.builder(packageElement.getQualifiedName().toString(), classBuilder.build())
                .build();

        try {
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderClassName);
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                javaFile.writeTo(out);
            }
        } catch (IOException ex) {

        }
    }

    private MethodSpec buildMethodSpec(VariableElement element) {
        String fieldName = element.getSimpleName().toString();
        return MethodSpec
                .methodBuilder(getGetterMethodName(fieldName))
                .returns(TypeName.get(element.asType()))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L", fieldName)
                .build();
    }

    private String getGetterMethodName(String fieldName) {
        return String.format("get%s", fieldName);
    }
}
