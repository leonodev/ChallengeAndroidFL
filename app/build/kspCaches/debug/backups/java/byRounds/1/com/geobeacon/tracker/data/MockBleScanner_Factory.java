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
public final class MockBleScanner_Factory implements Factory<MockBleScanner> {
  @Override
  public MockBleScanner get() {
    return newInstance();
  }

  public static MockBleScanner_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MockBleScanner newInstance() {
    return new MockBleScanner();
  }

  private static final class InstanceHolder {
    private static final MockBleScanner_Factory INSTANCE = new MockBleScanner_Factory();
  }
}
