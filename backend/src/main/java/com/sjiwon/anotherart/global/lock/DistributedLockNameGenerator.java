package com.sjiwon.anotherart.global.lock;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class DistributedLockNameGenerator {
    private static final ExpressionParser expressionParser = new SpelExpressionParser();

    public static Object generate(
            final String prefix,
            final String key,
            final String[] parameterNames,
            final Object[] args
    ) {
        final StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return prefix + parseKey(key, context);
    }

    private static Object parseKey(final String key, final StandardEvaluationContext context) {
        return expressionParser.parseExpression(key).getValue(context, Object.class);
    }
}
