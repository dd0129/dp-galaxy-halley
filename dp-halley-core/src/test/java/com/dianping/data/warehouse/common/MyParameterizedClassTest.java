package com.dianping.data.warehouse.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MyParameterizedClassTest {

  private int multiplier;

  public MyParameterizedClassTest(int testParameter) {
    this.multiplier = testParameter;
  }

  // creates the test data
  @Parameters
  public static Collection<Object[]> data() {
    Object[][] data = new Object[][] { { 1 }, { 5 }, { 121 } ,{ 123 },{ 125 }};
    return Arrays.asList(data);
  }

  @Test
  public void testMultiplyException() {
    MyClass tester = new MyClass();
    assertEquals("Result", multiplier * multiplier,
        tester.multiply(multiplier, multiplier));
  }

}
 