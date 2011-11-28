/*
 * Copyright 2010 Henry Coles
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations under the License. 
 */
package org.pitest.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.jmock.MockObjectTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pitest.ConcreteConfiguration;
import org.pitest.extension.TestDiscoveryListener;
import org.pitest.extension.TestUnit;
import org.pitest.extension.TestUnitProcessor;
import org.pitest.extension.common.IdentityTestUnitProcessor;
import org.pitest.extension.common.NullDiscoveryListener;

import com.example.TheoryTest;

public class JUnitCustomRunnerTestUnitFinderTest {

  private JUnitCustomRunnerTestUnitFinder testee;

  @Mock
  private TestDiscoveryListener           listener;

  @Mock
  private TestUnitProcessor               processor;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.testee = new JUnitCustomRunnerTestUnitFinder();
  }

  @Test
  public void shouldFindTestsInJUnitTheoryTest() {
    final Collection<TestUnit> actual = findWithTestee(TheoryTest.class);
    assertEquals(3, actual.size());
  }

  @RunWith(Parameterized.class)
  public static class ParameterisedTest {

    public ParameterisedTest(final int i) {

    }

    @Parameters
    public static Collection<Object[]> params() {
      return Arrays.asList(new Object[][] { { 1 }, { 2 }, { 3 }, { 4 } });
    }

    @Test
    public void test() {
    }

  }

  @Test
  public void shouldCreateSingleTestUnitForParameterizedTest() {
    // fixme would be better to properly split into tests
    final Collection<TestUnit> actual = findWithTestee(ParameterisedTest.class);
    assertEquals(1, actual.size());
  }

  public static class CustomSuiteRunner extends Suite {

    public CustomSuiteRunner(final Class<?> klass, final RunnerBuilder rb)
        throws InitializationError {
      super(klass, rb);
    }

  }

  public static class One {
    @Test
    public void one() {

    }

    @Test
    public void two() {

    }
  }

  public static class Two {
    @Test
    public void one() {

    }

    @Test
    public void two() {

    }
  }

  @RunWith(CustomSuiteRunner.class)
  @SuiteClasses({ One.class, Two.class })
  public static class CustomSuite {

  }

  @Test
  public void shouldNotFindTestsInCustomSuite() {
    final Collection<TestUnit> actual = findWithTestee(CustomSuite.class);
    assertTrue(actual.isEmpty());
  }

  public static class Three {
    @Test
    public void one() {

    }

    @Test
    public void two() {

    }
  }

  @RunWith(CustomSuiteRunner.class)
  @SuiteClasses({ CustomSuite.class, Three.class })
  public static class NestCustomSuite {

  }

  @Test
  public void shouldNotFindTestsInNestedCustomSuites() {
    final Collection<TestUnit> actual = findWithTestee(NestCustomSuite.class);
    assertTrue(actual.isEmpty());
  }

  private Collection<TestUnit> findWithTestee(final Class<?> clazz) {
    return this.testee.findTestUnits(clazz, new ConcreteConfiguration(),
        this.listener, new IdentityTestUnitProcessor());
  }

  public static class NotATest {

  }

  @Test
  public void shouldNotFindTestInNonTestClasses() {
    final Collection<TestUnit> actual = findWithTestee(NotATest.class);
    assertTrue(actual.isEmpty());
  }

  public static class JMockTest extends MockObjectTestCase {
    public void testExample() {

    }
  }

  @Test
  public void shouldFindTestUnitsInCustomJUnit3Class() {
    final Collection<TestUnit> actual = this.testee.findTestUnits(
        JMockTest.class, null, new NullDiscoveryListener(),
        new IdentityTestUnitProcessor());
    assertFalse(actual.isEmpty());
  }

  public static class JUnit3Test extends TestCase {
    public void testStuff() {

    }
  }

  @Test
  public void shouldApplyTestProcessorExactlyOnce() {
    this.testee.findTestUnits(JMockTest.class, null,
        new NullDiscoveryListener(), this.processor);

    verify(this.processor, times(1)).apply(any(TestUnit.class));

  }

  @Test
  public void shouldListOfAllTestUnitsToDiscoveryListener() {
    this.testee.findTestUnits(JMockTest.class, null, this.listener,
        new IdentityTestUnitProcessor());
    verify(this.listener, times(1)).receiveTests(anyListOf(TestUnit.class));
  }

}
