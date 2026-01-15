package br.com.webstorage.falaserio.di;

import br.com.webstorage.falaserio.domain.audio.VsaAnalyzer;
import br.com.webstorage.falaserio.domain.usecase.AnalyzeAudioUseCase;
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
public final class VsaModule_ProvideAnalyzeAudioUseCaseFactory implements Factory<AnalyzeAudioUseCase> {
  private final Provider<VsaAnalyzer> vsaAnalyzerProvider;

  private VsaModule_ProvideAnalyzeAudioUseCaseFactory(Provider<VsaAnalyzer> vsaAnalyzerProvider) {
    this.vsaAnalyzerProvider = vsaAnalyzerProvider;
  }

  @Override
  public AnalyzeAudioUseCase get() {
    return provideAnalyzeAudioUseCase(vsaAnalyzerProvider.get());
  }

  public static VsaModule_ProvideAnalyzeAudioUseCaseFactory create(
      Provider<VsaAnalyzer> vsaAnalyzerProvider) {
    return new VsaModule_ProvideAnalyzeAudioUseCaseFactory(vsaAnalyzerProvider);
  }

  public static AnalyzeAudioUseCase provideAnalyzeAudioUseCase(VsaAnalyzer vsaAnalyzer) {
    return Preconditions.checkNotNullFromProvides(VsaModule.INSTANCE.provideAnalyzeAudioUseCase(vsaAnalyzer));
  }
}
