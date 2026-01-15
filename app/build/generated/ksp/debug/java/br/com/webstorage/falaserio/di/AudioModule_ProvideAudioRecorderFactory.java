package br.com.webstorage.falaserio.di;

import android.content.Context;
import br.com.webstorage.falaserio.domain.audio.AudioRecorder;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AudioModule_ProvideAudioRecorderFactory implements Factory<AudioRecorder> {
  private final Provider<Context> contextProvider;

  private AudioModule_ProvideAudioRecorderFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AudioRecorder get() {
    return provideAudioRecorder(contextProvider.get());
  }

  public static AudioModule_ProvideAudioRecorderFactory create(Provider<Context> contextProvider) {
    return new AudioModule_ProvideAudioRecorderFactory(contextProvider);
  }

  public static AudioRecorder provideAudioRecorder(Context context) {
    return Preconditions.checkNotNullFromProvides(AudioModule.INSTANCE.provideAudioRecorder(context));
  }
}
