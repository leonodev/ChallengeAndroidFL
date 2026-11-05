package com.geobeacon.tracker.data;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class MockLocationTracker_Factory implements Factory<MockLocationTracker> {
  @Override
  public MockLocationTracker get() {
    return newInstance();
  }

  public static MockLocationTracker_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MockLocationTracker newInstance() {
    return new MockLocationTracker();
  }

  private static final class InstanceHolder {
    private static final MockLocationTracker_Factory INSTANCE = new MockLocationTracker_Factory();
  }
}
