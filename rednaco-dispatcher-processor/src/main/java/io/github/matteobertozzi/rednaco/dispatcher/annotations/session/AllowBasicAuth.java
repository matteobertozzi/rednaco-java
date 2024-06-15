package io.github.matteobertozzi.rednaco.dispatcher.annotations.session;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface AllowBasicAuth {
  String realm() default "auth";
}
