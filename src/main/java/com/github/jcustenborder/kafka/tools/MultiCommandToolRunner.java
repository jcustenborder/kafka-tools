package com.github.jcustenborder.kafka.tools;

import com.google.common.base.Preconditions;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class MultiCommandToolRunner {
  private static final Logger log = LoggerFactory.getLogger(MultiCommandToolRunner.class);

  public static void run(MultiCommandTool tool, String... args) throws Exception {

    Preconditions.checkNotNull(tool, "tool cannot be null.");
    ArgumentParser parser = ArgumentParsers.newFor(tool.name())
        .addHelp(true)
        .build();
    Subparsers subparsers = parser.addSubparsers();
    tool.tools().stream()
        .sorted(Comparator.comparing(Tool::name))
        .forEach(t -> {
          Subparser subParser = subparsers.addParser(t.name())
              .setDefault("tool", t)
              .description(t.description());
          t.options(subParser);
        });

    Namespace namespace = parser.parseArgsOrFail(args);
    log.trace("{}", namespace);
    Tool selectedTool = namespace.get("tool");
    selectedTool.execute(namespace);
  }
}
