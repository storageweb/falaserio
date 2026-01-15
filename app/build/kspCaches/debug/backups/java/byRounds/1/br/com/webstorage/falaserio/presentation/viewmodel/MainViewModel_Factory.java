package br.com.webstorage.falaserio.presentation.viewmodel;

import br.com.webstorage.falaserio.data.repository.CreditsRepository;
import br.com.webstorage.falaserio.data.repository.HistoryRepository;
import br.com.webstorage.falaserio.domain.audio.AudioRecorder;
import br.com.webstorage.falaserio.domain.usecase.AnalyzeAudioUseCase;
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<AudioRecorder> audioRecorderProvider;

  private final Provider<AnalyzeAudioUseCase> analyzeAudioUseCaseProvider;

  private final Provider<CreditsRepository> creditsRepositoryProvider;

  private final Provider<HistoryRepository> historyRepositoryProvider;

  private MainViewModel_Factory(Provider<AudioRecorder> audioRecorderProvider,
      Provider<AnalyzeAudioUseCase> analyzeAudioUseCaseProvider,
      Provider<CreditsRepository> creditsRepositoryProvider,
      Provider<HistoryRepository> historyRepositoryProvider) {
    this.audioRecorderProvider = audioRecorderProvider;
    this.analyzeAudioUseCaseProvider = analyzeAudioUseCaseProvider;
    this.creditsRepositoryProvider = creditsRepositoryProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(audioRecorderProvider.get(), analyzeAudioUseCaseProvider.get(), creditsRepositoryProvider.get(), historyRepositoryProvider.get());
  }

  public static MainViewModel_Factory create(Provider<AudioRecorder> audioRecorderProvider,
      Provider<AnalyzeAudioUseCase> analyzeAudioUseCaseProvider,
      Provider<CreditsRepository> creditsRepositoryProvider,
      Provider<HistoryRepository> historyRepositoryProvider) {
    return new MainViewModel_Factory(audioRecorderProvider, analyzeAudioUseCaseProvider, creditsRepositoryProvider, historyRepositoryProvider);
  }

  public static MainViewModel newInstance(AudioRecorder audioRecorder,
      AnalyzeAudioUseCase analyzeAudioUseCase, CreditsRepository creditsRepository,
      HistoryRepository historyRepository) {
    return new MainViewModel(audioRecorder, analyzeAudioUseCase, creditsRepository, historyRepository);
  }
}
