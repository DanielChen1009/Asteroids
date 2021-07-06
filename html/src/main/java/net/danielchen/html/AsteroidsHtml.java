package net.danielchen.html;

import com.google.gwt.core.client.EntryPoint;
import net.danielchen.core.Asteroids;
import playn.html.HtmlPlatform;

public class AsteroidsHtml implements EntryPoint {

  @Override public void onModuleLoad () {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    // use config to customize the HTML platform, if needed
    HtmlPlatform plat = new HtmlPlatform(config);
    plat.assets().setPathPrefix("asteroids/");
    plat.graphics().setSize(400, 400);
    plat.setTitle("Asteroids");
    new Asteroids(plat);
    plat.start();
  }
}
