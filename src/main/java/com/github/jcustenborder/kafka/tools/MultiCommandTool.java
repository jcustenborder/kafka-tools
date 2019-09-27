package com.github.jcustenborder.kafka.tools;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class MultiCommandTool implements Tool {
  private static final Logger log = LoggerFactory.getLogger(MultiCommandTool.class);

  protected abstract List<Tool> tools();

  protected static final String DEST_TOOL = "tool";

  @Override
  public void arguments(ArgumentParser parser) {
    Subparsers subparsers = parser.addSubparsers();

    for (Tool tool : tools()) {
      Subparser subParser = subparsers.addParser(tool.name())
          .setDefault(DEST_TOOL, tool)
          .description(tool.description());
      tool.arguments(subParser);
    }
  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    Tool tool = namespace.get(DEST_TOOL);
    tool.execute(namespace);
  }
}
