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

import com.google.common.base.Preconditions;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

public class ToolRunner {
  public static void run(Tool tool, String... args) throws Exception {
    Preconditions.checkNotNull(tool, "tool cannot be null.");
    ArgumentParser parser = ArgumentParsers.newFor(tool.name())
        .addHelp(true)
        .build();
    tool.options(parser);
    parser.description(tool.description());
    Namespace namespace = parser.parseArgsOrFail(args);

    Tool executeTool = namespace.get("tool");

    if (null == executeTool) {
      executeTool = tool;
    }
    try {
      executeTool.execute(namespace);
    } finally {
      executeTool.close();
    }
  }
}
