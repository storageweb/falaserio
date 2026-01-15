package br.com.webstorage.falaserio.presentation.viewmodel;

import br.com.webstorage.falaserio.data.repository.CreditsRepository;
import br.com.webstorage.falaserio.domain.billing.BillingManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class CreditsViewModel_Factory implements Factory<CreditsViewModel> {
  private final Provider<CreditsRepository> creditsRepositoryProvider;

  private final Provider<BillingManager> billingManagerProvider;

  private CreditsViewModel_Factory(Provider<CreditsRepository> creditsRepositoryProvider,
      Provider<BillingManager> billingManagerProvider) {
    this.creditsRepositoryProvider = creditsRepositoryProvider;
    this.billingManagerProvider = billingManagerProvider;
  }

  @Override
  public CreditsViewModel get() {
    return newInstance(creditsRepositoryProvider.get(), billingManagerProvider.get());
  }

  public static CreditsViewModel_Factory create(
      Provider<CreditsRepository> creditsRepositoryProvider,
      Provider<BillingManager> billingManagerProvider) {
    return new CreditsViewModel_Factory(creditsRepositoryProvider, billingManagerProvider);
  }

  public static CreditsViewModel newInstance(CreditsRepository creditsRepository,
      BillingManager billingManager) {
    return new CreditsViewModel(creditsRepository, billingManager);
  }
}
