package io.github.matteobertozzi.rednaco.dispatcher.annotations.session;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface RateLimited {
  enum RateLimitOn { IP, SESSION, SESSION_OWNER }

  RateLimitOn on();
  int limit();
  int windowSec() default 60; // 1min
}
