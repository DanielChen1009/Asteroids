package io.github.danielchen1009.android;

import playn.android.GameActivity;

import io.github.danielchen1009.core.Asteroids;

public class AsteroidsActivity extends GameActivity {

  @Override public void main () {
    new Asteroids(platform());
  }
}
