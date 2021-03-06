// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.util.serialization;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Date;

public final class ClassUtil {
  private ClassUtil() {
  }

  @NotNull
  public static Class<?> typeToClass(@NotNull Type type) {
    if (type instanceof Class) {
      return (Class<?>)type;
    }
    else if (type instanceof TypeVariable) {
      Type bound = ((TypeVariable)type).getBounds()[0];
      return bound instanceof Class ? (Class)bound : (Class<?>)((ParameterizedType)bound).getRawType();
    }
    else if (type instanceof WildcardType) {
      return (Class<?>)((WildcardType)type).getUpperBounds()[0];
    }
    else {
      return (Class<?>)((ParameterizedType)type).getRawType();
    }
  }

  public static boolean isPrimitive(@NotNull Class<?> aClass) {
    return aClass.isPrimitive() ||
           aClass == String.class ||
           aClass == Integer.class ||
           aClass == Long.class ||
           aClass == Boolean.class ||
           aClass == Double.class ||
           aClass == Float.class ||
           aClass.isEnum() ||
           Date.class.isAssignableFrom(aClass);
  }
}
