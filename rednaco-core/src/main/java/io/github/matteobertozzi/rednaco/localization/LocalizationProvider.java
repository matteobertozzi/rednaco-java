package io.github.matteobertozzi.rednaco.localization;

public interface LocalizationProvider {
  String get(final LocalizedResource resourceId, final Object... args);
}
