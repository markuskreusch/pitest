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
package org.pitest.mutationtest.engine.gregor;

import java.util.*;
import java.util.function.Predicate;

import org.pitest.functional.FCollection;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.engine.gregor.blocks.BlockCounter;
import org.pitest.mutationtest.engine.gregor.blocks.ConcreteBlockCounter;

class ClassContext implements BlockCounter {

  private ClassInfo                   classInfo;
  private String                      sourceFile;

  private Collection<MutationIdentifier>  targets  = Collections.emptyList();
  private final List<MutationDetails> mutations    = new ArrayList<>();

  private final ConcreteBlockCounter  blockCounter = new ConcreteBlockCounter();

  public Collection<MutationIdentifier> getTargetMutations() {
    return this.targets;
  }

  public ClassInfo getClassInfo() {
    return this.classInfo;
  }

  public String getJavaClassName() {
    return this.classInfo.getName().replace("/", ".");
  }

  public String getFileName() {
    return this.sourceFile;
  }

  public void setTargetMutations(final Collection<MutationIdentifier> targets) {
    this.targets = targets;
  }

  public List<MutationDetails> getMutationDetails(final MutationIdentifier id) {
    return FCollection.filter(this.mutations, hasId(id));
  }

  private static Predicate<MutationDetails> hasId(final MutationIdentifier id) {
    return a -> a.matchesId(id);
  }

  public void registerClass(final ClassInfo classInfo) {
    this.classInfo = classInfo;
  }

  public void registerSourceFile(final String source) {
    this.sourceFile = source;
  }

  public boolean shouldMutate(final MutationIdentifier newId) {
    return getTargetMutations().stream().filter(idMatches(newId)).findAny().isPresent();
  }

  private static Predicate<MutationIdentifier> idMatches(
      final MutationIdentifier newId) {
    return a -> a.matches(newId);
  }

  public Collection<MutationDetails> getCollectedMutations() {
    return this.mutations;
  }

  public void addMutation(final MutationDetails details) {
    this.mutations.add(details);

  }

  @Override
  public void registerNewBlock() {
    this.blockCounter.registerNewBlock();

  }

  @Override
  public void registerFinallyBlockStart() {
    this.blockCounter.registerFinallyBlockStart();
  }

  @Override
  public void registerFinallyBlockEnd() {
    this.blockCounter.registerFinallyBlockEnd();
  }

  public int getCurrentBlock() {
    return this.blockCounter.getCurrentBlockThisMethod();
  }

  @Override
  public void registerNewMethodStart() {
    this.blockCounter.registerNewMethodStart();
  }

  public boolean isWithinFinallyBlock() {
    return this.blockCounter.isWithinFinallyBlock();
  }

}
