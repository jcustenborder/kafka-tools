/**
 * Copyright © 2019 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jcustenborder.kafka.tools;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.File;

import static net.sourceforge.argparse4j.impl.Arguments.store;

public class CommonArguments {


  private static final String DEST_BOOTSTRAP_SERVER = "bootstrapServer";
  private static final String DEST_OUTPUT_FILE = "outputFile";

  public static String bootstrapServer(Namespace namespace) {
    return namespace.getString(DEST_BOOTSTRAP_SERVER);
  }

  public static Argument bootstrapServer(ArgumentParser parser) {
    return parser.addArgument("--bootstrap-server")
        .action(store())
        .required(true)
        .type(String.class)
        .metavar("HOST1:PORT1[,HOST2:PORT2[...]]")
        .dest(DEST_BOOTSTRAP_SERVER)
        .help("Comma-separated list of Kafka brokers in the form HOST1:PORT1,HOST2:PORT2,...");
  }

  public static File outputFile(Namespace namespace) {
    return namespace.get(DEST_OUTPUT_FILE);
  }

  public static Argument outputFile(ArgumentParser parser, boolean required) {
    return parser.addArgument("--output", "-f")
        .action(store())
        .required(required)
        .type(File.class)
        .metavar("Path to file")
        .dest(DEST_OUTPUT_FILE)
        .help("Path to write the output of the command to.");
  }


}
