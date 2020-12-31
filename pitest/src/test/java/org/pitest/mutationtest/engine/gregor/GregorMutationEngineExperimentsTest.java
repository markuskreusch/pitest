/*
 * Copyright 2011 Henry Coles
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
package org.pitest.mutationtest.engine.gregor;

import org.junit.Test;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.engine.gregor.config.DefaultMutationEngineConfiguration;
import org.pitest.mutationtest.engine.gregor.config.Mutator;
import org.pitest.mutationtest.engine.gregor.mutators.ConditionalsBoundaryMutator;
import org.pitest.mutationtest.engine.gregor.mutators.MathMutator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class GregorMutationEngineExperimentsTest {

  private GregorMutationEngine testee;

  @Test
  public void printMutations() {
    final Collection<MethodMutatorFactory> mutators = Mutator
        .fromStrings(Arrays.asList("TRUE_RETURNS", "MATH"));
    final DefaultMutationEngineConfiguration config = new DefaultMutationEngineConfiguration(
        i -> true, mutators);
    this.testee = new GregorMutationEngine(config);

    Mutater mutater = this.testee.createMutator(ClassloaderByteArraySource.fromContext());
    mutater.findMutations(ClassName.fromClass(MutatedClass.class)).forEach(mutation -> {
      System.out.println(mutation);
    });
  }

  @Test
  public void applyFirstMutation() throws IOException {
    final Collection<MethodMutatorFactory> mutators = Mutator
            .fromStrings(Arrays.asList("TRUE_RETURNS", "MATH"));
    final DefaultMutationEngineConfiguration config = new DefaultMutationEngineConfiguration(
            i -> true, mutators);
    this.testee = new GregorMutationEngine(config);

    Mutater mutater = this.testee.createMutator(ClassloaderByteArraySource.fromContext());
    MutationDetails firstMutation = mutater.findMutations(ClassName.fromClass(MutatedClass.class)).get(0);
    MutationDetails secondMutation = mutater.findMutations(ClassName.fromClass(MutatedClass.class)).get(1);
    MutationDetails thirdMutation = mutater.findMutations(ClassName.fromClass(MutatedClass.class)).get(2);
    byte[] bytes = mutater.getMetaMutation(firstMutation.getId(), secondMutation.getId(), thirdMutation.getId()).getBytes();
    Path path = Paths.get("tmp.class");
    System.out.println(path.toAbsolutePath());
    Files.write(path, bytes);
  }

}

class MutatedClass {

  public boolean mutatedMethod(int a, int b) {
    if (a + b == 0) {
      return false;
    }

    return false;
  }

}
