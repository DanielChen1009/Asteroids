package io.github.danielchen1009.html;

import com.google.gwt.core.client.EntryPoint;
import playn.html.HtmlPlatform;
import io.github.danielchen1009.core.Asteroids;

public class AsteroidsHtml implements EntryPoint {

  @Override public void onModuleLoad () {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    config.experimentalFullscreen = true;
    // use config to customize the HTML platform, if needed
    HtmlPlatform plat = new HtmlPlatform(config);
    plat.assets().setPathPrefix("asteroids/");
    new Asteroids(plat);
    plat.start();
  }
}
