package com.geobeacon.tracker.di;

import com.geobeacon.tracker.data.local.AppDatabase;
import com.geobeacon.tracker.data.local.SensorDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class DatabaseModule_ProvideSensorDaoFactory implements Factory<SensorDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideSensorDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SensorDao get() {
    return provideSensorDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideSensorDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideSensorDaoFactory(databaseProvider);
  }

  public static SensorDao provideSensorDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSensorDao(database));
  }
}
