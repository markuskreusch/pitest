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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.pitest.extension.Configuration;
import org.pitest.extension.TestDiscoveryListener;
import org.pitest.extension.TestUnit;
import org.pitest.extension.TestUnitFinder;
import org.pitest.extension.TestUnitProcessor;
import org.pitest.functional.FCollection;
import org.pitest.internal.TestClass;
import org.pitest.junit.adapter.PITJUnitRunner;
import org.pitest.junit.adapter.RunnerAdapter;
import org.pitest.junit.adapter.RunnerAdapterDescriptionTestUnit;

public class JUnitCustomRunnerTestUnitFinder implements TestUnitFinder {

  public boolean canHandle(final Class<?> clazz, final boolean alreadyHandled) {
    return !alreadyHandled;
  }

  public Collection<TestUnit> findTestUnits(final TestClass a,
      final Configuration b, final TestDiscoveryListener listener,
      final TestUnitProcessor processor) {
    final RunWith runWith = a.getClazz().getAnnotation(RunWith.class);
    if ((runWith != null) && !runWith.value().equals(PITJUnitRunner.class)
        && !runWith.value().equals(Suite.class)) {

      final RunnerAdapter adapter = new RunnerAdapter(a.getClazz());
      final List<RunnerAdapterDescriptionTestUnit> units = adapter
          .getDescriptions();
      listener.recieveTests(units);

      return FCollection.map(Collections.<TestUnit> singletonList(adapter),
          processor);

    }

    return Collections.emptyList();
  }

}