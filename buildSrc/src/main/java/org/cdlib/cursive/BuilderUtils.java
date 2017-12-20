package org.cdlib.cursive;

import com.squareup.javapoet.*;
import io.vavr.Lazy;
import org.apache.commons.text.WordUtils;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;

class BuilderUtils {

  private BuilderUtils() {
    // private to prevent instantiation
  }

  static void addField(TypeSpec.Builder builder, MethodSpec.Builder constructorBuilder, Type fieldType, String fieldName, boolean isOverride) {
    FieldSpec field = FieldSpec.builder(fieldType, fieldName, Modifier.PRIVATE, Modifier.FINAL).build();
    addField(builder, constructorBuilder, field, isOverride);
  }

  static void addField(TypeSpec.Builder builder, MethodSpec.Builder constructorBuilder, TypeName fieldType, String fieldName, boolean isOverride) {
    FieldSpec field = FieldSpec.builder(fieldType, fieldName, Modifier.PRIVATE, Modifier.FINAL).build();
    addField(builder, constructorBuilder, field, isOverride);
  }

  static void addField(TypeSpec.Builder builder, MethodSpec.Builder constructorBuilder, FieldSpec field, boolean isOverride) {
    MethodSpec.Builder accessorSpec = MethodSpec.methodBuilder("get" + WordUtils.capitalize(field.name))
      .addModifiers(Modifier.PUBLIC)
      .returns(field.type)
      .addStatement("return this.$N", field.name);

    if (isOverride) {
      accessorSpec = accessorSpec.addAnnotation(Override.class);
    }

    constructorBuilder
      .addParameter(field.type, field.name)
      .addStatement("this.$N = $N", field.name, field.name);

    builder.addField(field)
      .addMethod(accessorSpec.build());
  }

  static void addLazyField(TypeSpec.Builder builder, Type valueType, String fieldName, String valueStmt, boolean isOverride) {
    String valueMethod = fieldName + "Value";

    MethodSpec valueInitializer = MethodSpec.methodBuilder(valueMethod)
      .returns(valueType)
      .addModifiers(Modifier.PRIVATE)
      .addStatement(valueStmt)
      .build();

    FieldSpec field = FieldSpec.builder(ParameterizedTypeName.get(Lazy.class, valueType), fieldName, Modifier.PRIVATE, Modifier.FINAL)
      .initializer(String.format("Lazy.of(this::%s)", valueMethod))
      .build();

    MethodSpec.Builder accessorSpec = MethodSpec.methodBuilder("get" + WordUtils.capitalize(fieldName))
      .addModifiers(Modifier.PUBLIC)
      .returns(valueType)
      .addStatement("return this.$N.get()", fieldName);

    if (isOverride) {
      accessorSpec = accessorSpec.addAnnotation(Override.class);
    }

    builder.addField(field)
      .addMethod(accessorSpec
        .build())
      .addMethod(valueInitializer);
  }

  static void addConstant(TypeSpec.Builder builder, Type constClass, String constName, String format, Object... args) {
    FieldSpec fieldSpec = FieldSpec.builder(constClass, constName)
      .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
      .initializer(format, args)
      .build();
    builder.addField(fieldSpec);
  }
}
