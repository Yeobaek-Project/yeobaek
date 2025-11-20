package com.ssafy.yeobaek.config;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.type.BasicType;
import org.hibernate.type.spi.TypeConfiguration;

public class CustomMySQLDialect extends MySQL8Dialect {

    public CustomMySQLDialect() {
        super();
    }

    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);

        TypeConfiguration typeConfiguration = functionContributions.getTypeConfiguration();
        BasicType doubleType = typeConfiguration.getBasicTypeForJavaType(Double.class);

        // 삼각 함수 및 수학 함수 등록
        functionContributions.getFunctionRegistry().registerPattern("RADIANS", "radians(?1)", doubleType);
        functionContributions.getFunctionRegistry().registerPattern("DEGREE", "degrees(?1)", doubleType);
        functionContributions.getFunctionRegistry().registerPattern("COS", "cos(?1)", doubleType);
        functionContributions.getFunctionRegistry().registerPattern("SIN", "sin(?1)", doubleType);
        functionContributions.getFunctionRegistry().registerPattern("ACOS", "acos(?1)", doubleType);
        functionContributions.getFunctionRegistry().registerPattern("ATAN2", "atan2(?1, ?2)", doubleType);
        functionContributions.getFunctionRegistry().registerPattern("SQRT", "sqrt(?1)", doubleType);
        functionContributions.getFunctionRegistry().registerPattern("POWER", "pow(?1, ?2)", doubleType);

        functionContributions.getFunctionRegistry().registerPattern("DIV", "(?1 / ?2)", doubleType);
        functionContributions.getFunctionRegistry().registerPattern("DIFF", "(?1 - ?2)", doubleType);
        functionContributions.getFunctionRegistry().registerPattern("PROD", "(?1 * ?2)", doubleType);
        functionContributions.getFunctionRegistry().registerPattern("SUM_FUNC", "(?1 + ?2)", doubleType);
    }
}
