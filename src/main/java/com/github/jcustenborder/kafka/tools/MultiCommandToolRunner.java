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
import java.util.List;
import java.util.function.Consumer;

public class MultiCommandToolRunner  {
  private static final Logger log = LoggerFactory.getLogger(MultiCommandToolRunner.class);

  public static void run(List<Tool> tools, String... args) throws Exception {
    Preconditions.checkNotNull(tools, "tool cannot be null.");
    ArgumentParser parser = ArgumentParsers.newFor("connect-command")
        .addHelp(true)
        .build();

    Subparsers subparsers = parser.addSubparsers();

    tools.stream()
        .sorted(Comparator.comparing(Tool::name))
        .forEach(tool -> {
          Subparser subParser = subparsers.addParser(tool.name())
              .setDefault("tool", tool)
              .description(tool.description());
          tool.options(subParser);
        });

    Namespace namespace = parser.parseArgsOrFail(args);
    log.trace("{}", namespace);
    Tool selectedTool = namespace.get("tool");
    selectedTool.execute(namespace);
  }
}
