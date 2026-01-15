package br.com.webstorage.falaserio.di;

import br.com.webstorage.falaserio.data.local.AppDatabase;
import br.com.webstorage.falaserio.data.local.dao.CreditsDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideCreditsDaoFactory implements Factory<CreditsDao> {
  private final Provider<AppDatabase> databaseProvider;

  private DatabaseModule_ProvideCreditsDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public CreditsDao get() {
    return provideCreditsDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideCreditsDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideCreditsDaoFactory(databaseProvider);
  }

  public static CreditsDao provideCreditsDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCreditsDao(database));
  }
}
