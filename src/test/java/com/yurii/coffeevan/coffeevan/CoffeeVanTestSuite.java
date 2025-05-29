package com.yurii.coffeevan.coffeevan;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Coffee Van Test Suite")
@SelectPackages({
    "com.yurii.coffeevan.coffeevan.model",
    "com.yurii.coffeevan.coffeevan.db"
})
public class CoffeeVanTestSuite {
    // This class remains empty,
    // it is just used to run all tests in the specified packages
} 