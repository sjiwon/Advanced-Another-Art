package com.sjiwon.anotherart.auth.domain;

@FunctionalInterface
public interface AuthCodeGenerator {
    String get();
}
