package com.github.jcustenborder.kafka.tools.connect;

import com.github.jcustenborder.kafka.connect.client.KafkaConnectClient;
import com.github.jcustenborder.kafka.connect.client.model.ConnectorStatusResponse;
import com.github.jcustenborder.kafka.connect.client.model.State;
import com.github.jcustenborder.kafka.connect.client.model.TaskStatusResponse;
import com.google.common.base.Preconditions;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;

public class RestartTaskTool extends AbstractConnectTool {
  private static final Logger log = LoggerFactory.getLogger(RestartTaskTool.class);

  @Override
  public String name() {
    return "restart-task";
  }

  @Override
  public String description() {
    return "Command is used to restart a task for a connector.";
  }

  static final String DEST_TASK = "task";
  static final String DEST_ALL_FAILED = "allFailedTasks";

  @Override
  public void arguments(ArgumentParser parser) {
    addConnectorArgument(parser).required(true);

    parser.addArgument("--task")
        .dest(DEST_TASK)
        .help("The task id(s) that should be restarted.")
        .type(Integer.class)
        .nargs("+");

    parser.addArgument("--all-failed")
        .dest(DEST_ALL_FAILED)
        .action(storeTrue())
        .type(Boolean.class)
        .setDefault(false)
        .help("Flag to include all of the tasks that are in a failed state.");


  }

  @Override
  public void execute(Namespace namespace) throws Exception {
    KafkaConnectClient client = client(namespace);
    final List<String> connectors = namespace.getList(ConnectConstants.DEST_CONNECTOR);
    final List<Integer> taskIds = namespace.getList(DEST_TASK);
    final boolean allFailedTasks = namespace.getBoolean(DEST_ALL_FAILED);

    if (!allFailedTasks) {
      Preconditions.checkNotNull(taskIds, "--task must be specified.");
    }

    for (String connectorName : connectors) {
      List<Integer> restartTasks;

      if (allFailedTasks) {
        ConnectorStatusResponse statusResponse = client.status(connectorName);
        restartTasks = statusResponse.tasks().stream()
            .filter(t -> t.state() == State.Failed)
            .map(TaskStatusResponse::id)
            .collect(Collectors.toList());
      } else {
        restartTasks = taskIds;
      }

      for (Integer taskId : restartTasks) {
        log.info("Restating task. Connector = '{}' TaskID = '{}'", connectorName, taskId);
        client.restart(connectorName, taskId);
      }
    }
  }

}
