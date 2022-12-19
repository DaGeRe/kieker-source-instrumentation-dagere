package net.kieker.sourceinstrumentation;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import net.kieker.sourceinstrumentation.instrument.InstrumentKiekerSource;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

public class SourceInstrumentationStarter implements Callable<Integer> {

   @Option(names = { "-folder", "--folder" }, description = "Folder where files should be instrumented", required = true)
   private File projectFolder;

   @Mixin
   private InstrumentationConfigMixin instrumentationConfigMixin;

   public static void main(final String[] args) {
      final CommandLine commandLine = new CommandLine(new SourceInstrumentationStarter());
      commandLine.execute(args);
   }

   @Override
   public Integer call() throws Exception {
      final Set<String> includedPatterns = createIncludedPatterns();
      final Set<String> excludedPattern = createExcludePatterns();
      
      final InstrumentationConfiguration configuration = new InstrumentationConfiguration(instrumentationConfigMixin.getUsedRecord(), instrumentationConfigMixin.isAggregate(),
            instrumentationConfigMixin.isCreateDefaultConstructor(),
            instrumentationConfigMixin.isEnableAdaptiveMonitoring(), includedPatterns, excludedPattern,
            !instrumentationConfigMixin.isDisableDeactivation(), instrumentationConfigMixin.getAggregationCount(), instrumentationConfigMixin.isExtractMethod(), instrumentationConfigMixin.isStrictMode());

      final InstrumentKiekerSource sourceInstrumenter = new InstrumentKiekerSource(configuration);
      sourceInstrumenter.instrumentProject(projectFolder);
      return 0;
   }

   private Set<String> createExcludePatterns() {
      Set<String> excludedPattern = new HashSet<>();
      if (instrumentationConfigMixin.getExcludedPatterns() != null) {
         for (String pattern : instrumentationConfigMixin.getExcludedPatterns()) {
            excludedPattern.add(pattern);
         }
      }
      return excludedPattern;
   }

   private HashSet<String> createIncludedPatterns() {
      final HashSet<String> includedPatterns = new HashSet<>();
      if (instrumentationConfigMixin.getIncludedPatterns() == null || instrumentationConfigMixin.getIncludedPatterns().isEmpty()) {
         includedPatterns.add("*");
      } else {
         for (String pattern : instrumentationConfigMixin.getIncludedPatterns()) {
            includedPatterns.add(pattern);
         }
      }
      return includedPatterns;
   }
}
