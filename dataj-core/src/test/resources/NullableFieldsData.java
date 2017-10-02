package com.example;

import org.dataj.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Data
class NullableFieldsData {
    @Nonnull
    String name;

    @Nullable
    Integer age;

    public NullableFieldsData(@Nonnull String name, @Nullable Integer age) {
        this.name = name;
        this.age = age;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String value) {
        this.name = value;
    }

    @Nullable
    public Integer getAge() {
        return age;
    }

    public void setAge(@Nullable Integer value) {
        this.age = value;
    }
}