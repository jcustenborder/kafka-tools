package com.github.jcustenborder.kafka.tools;

import com.github.jcustenborder.kafka.tools.broker.WaitForISRTool;
import com.github.jcustenborder.kafka.tools.connect.ConnectTool;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class DocumentationTest {
  private static final Logger log = LoggerFactory.getLogger(DocumentationTest.class);
  ListMultimap<String, Tool> toolLookup = LinkedListMultimap.create();

  @BeforeEach
  public void before() {
    toolLookup.put("Kafka", new WaitForISRTool());
    toolLookup.put("Kafka Connect", new ConnectTool());
  }

  void print(PrintWriter writer, Tool tool, int indent) {
    String prefix = Strings.repeat("#", indent);

    writer.format("%s %s", prefix, tool.name());
    writer.println();
    writer.println();
    writer.println(tool.description());

    ArgumentParser parser = ArgumentParsers.newFor(tool.name()).build();
    parser.description(tool.description());
    tool.arguments(parser);

    writer.println("```");

    parser.printHelp(writer);

    writer.println("```");

    if (tool instanceof MultiCommandTool) {
      MultiCommandTool multiCommandTool = (MultiCommandTool) tool;
      Subparsers subparsers = parser.addSubparsers();

      for (Tool subTool : multiCommandTool.tools()) {
        log.info("Processing {}", subTool);
        print(writer, subTool, indent + 1);
      }
    }
    writer.println();
  }

  @Test
  public void readme() {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    for (String section : this.toolLookup.keySet()) {
      writer.format("# %s", section);
      writer.println();
      writer.println();
      writer.println();

      List<Tool> tools = this.toolLookup.get(section);
      for (Tool tool : tools) {
        print(writer, tool, 2);
      }
    }

    log.info("\n{}", stringWriter);
  }

}
