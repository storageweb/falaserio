package br.com.webstorage.falaserio.data.repository;

import br.com.webstorage.falaserio.data.local.dao.CreditsDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class CreditsRepository_Factory implements Factory<CreditsRepository> {
  private final Provider<CreditsDao> creditsDaoProvider;

  private CreditsRepository_Factory(Provider<CreditsDao> creditsDaoProvider) {
    this.creditsDaoProvider = creditsDaoProvider;
  }

  @Override
  public CreditsRepository get() {
    return newInstance(creditsDaoProvider.get());
  }

  public static CreditsRepository_Factory create(Provider<CreditsDao> creditsDaoProvider) {
    return new CreditsRepository_Factory(creditsDaoProvider);
  }

  public static CreditsRepository newInstance(CreditsDao creditsDao) {
    return new CreditsRepository(creditsDao);
  }
}
