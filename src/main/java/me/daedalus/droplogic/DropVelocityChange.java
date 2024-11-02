package me.daedalus.droplogic;

import java.util.Optional;
import java.util.Set;

public class DropVelocityChange {
  public Optional<Set<Integer>> overridenBlocks = Optional.empty();
  public double[] randomValues = new double[5];

  DropVelocityChange() { }

  DropVelocityChange(Optional<Set<Integer>> overridenBlocks, double[] randomValues) {
    this.overridenBlocks = overridenBlocks;
    this.randomValues = randomValues;
  }
}
