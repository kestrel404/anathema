package net.sf.anathema.framework.environment;

import net.sf.anathema.lib.exception.ExceptionHandler;
import net.sf.anathema.lib.resources.Resources;

public class ApplicationEnvironment implements Environment {
  private final Resources resources;
  private final ExceptionHandler handler;

  public ApplicationEnvironment(Resources resources, ExceptionHandler handler) {
    this.resources = resources;
    this.handler = handler;
  }

  @Override
  public void handle(Throwable throwable, String message) {
    handler.handle(throwable, message);
  }

  @Override
  public boolean supportsKey(String key) {
    return resources.supportsKey(key);
  }

  @Override
  public String getString(String key, Object... arguments) {
    return resources.getString(key, arguments);
  }
}