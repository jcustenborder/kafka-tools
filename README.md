# Introduction

# Kafka


## wait-for-isr

This tool is used to check if a broker(s) has under replicated partitions.
```
usage: wait-for-isr [-h] --bootstrap-server HOST1:PORT1[,HOST2:PORT2[...]]
                    [--output Path to file]
                    [--max-wait-time-unit {NANOSECONDS,MICROSECONDS,MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS}]
                    [--max-wait-time MAXWAITTIME]
                    [--time-between-checks-unit {NANOSECONDS,MICROSECONDS,MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS}]
                    [--time-between-checks TIMEBETWEENCHECKS]
                    brokers [brokers ...]

This tool is used to check if a broker(s) has under replicated partitions.

positional arguments:
  brokers                The broker id(s) that the tool should check for.

named arguments:
  -h, --help             show this help message and exit
  --bootstrap-server HOST1:PORT1[,HOST2:PORT2[...]]
                         Comma-separated list of Kafka  brokers in the form
                         HOST1:PORT1,HOST2:PORT2,...
  --output Path to file, -f Path to file
                         Path to write the output of the command to.
  --max-wait-time-unit {NANOSECONDS,MICROSECONDS,MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS}
                         The unit of time to wait.
  --max-wait-time MAXWAITTIME
                         The amount of time to wait.
  --time-between-checks-unit {NANOSECONDS,MICROSECONDS,MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS}
                         The unit of time to wait.
  --time-between-checks TIMEBETWEENCHECKS
                         The amount of time to wait.
```

# Kafka Connect


## connect-client

The connect-client command provides additinal functionality to assist with the administration of a Kafka Connect cluster. All actions are performed over the Kafka Connect REST API.
```
usage: connect-client [-h] [--connect-config CONNECTCONFIG] [--host HOST]
                      [--port PORT] [--output-format {Table,Json}]
                      {create,restart-connector,restart-task,configure,import,export,list,connector-plugins,delete,download-example,status,pause,resume}
                      ...

The connect-client command provides additinal  functionality to assist with
the administration of a Kafka  Connect  cluster.  All actions are performed
over the Kafka Connect REST API.

positional arguments:
  {create,restart-connector,restart-task,configure,import,export,list,connector-plugins,delete,download-example,status,pause,resume}

named arguments:
  -h, --help             show this help message and exit
  --connect-config CONNECTCONFIG
                         Location of the config file  this utility will use
                         to store connection  information  about  the Kafka
                         Connect Cluster.
  --host HOST            Host of the Kafka Connect cluster to connect to.
  --port PORT            Port on the Kafka Connect host to connect to.
  --output-format {Table,Json}
                         The   format   written   to   the   console   when
                         information is displayed.
```
### create

Command is used to create or update a connector on the Kafka Connect Cluster.
```
usage: create [-h] --config-path CONFIGPATH

Command is used to  create  or  update  a  connector  on  the Kafka Connect
Cluster.

named arguments:
  -h, --help             show this help message and exit
  --config-path CONFIGPATH
                         Location on the  local  file  system  to  read the
                         config from.
```

### restart-connector

Command is used to restart a connector and all of it's tasks.
```
usage: restart-connector [-h] --connector CONNECTOR [CONNECTOR ...]

Command is used to restart a connector and all of it's tasks.

named arguments:
  -h, --help             show this help message and exit
  --connector CONNECTOR [CONNECTOR ...]
                         The name of the connector
```

### restart-task

Command is used to restart a task for a connector.
```
usage: restart-task [-h] --connector CONNECTOR [CONNECTOR ...]
                    [--task TASK [TASK ...]] [--all-failed]

Command is used to restart a task for a connector.

named arguments:
  -h, --help             show this help message and exit
  --connector CONNECTOR [CONNECTOR ...]
                         The name of the connector
  --task TASK [TASK ...]
                         The task id(s) that should be restarted.
  --all-failed           Flag to include all  of  the  tasks  that are in a
                         failed state.
```

### configure

Command is used to configure the connection information for the connect cluster.
```
usage: configure [-h]

Command is used to  configure  the  connection  information for the connect
cluster.

named arguments:
  -h, --help             show this help message and exit
```

### import

Command is used to configure the connection information for the connect cluster.
```
usage: import [-h] --input-path INPUTPATH

Command is used to  configure  the  connection  information for the connect
cluster.

named arguments:
  -h, --help             show this help message and exit
  --input-path INPUTPATH
                         Directory to read connector configuration(s) from.
```

### export

Command is used to export the connector configurations from the Kafka Connect Cluster tothe local file system.
```
usage: export [-h] --output-path OUTPUTPATH

Command is used  to  export  the  connector  configurations  from the Kafka
Connect Cluster tothe local file system.

named arguments:
  -h, --help             show this help message and exit
  --output-path OUTPUTPATH
                         Directory on the file  system  to write the output
                         to.
```

### list

Command is used to list the connectors on the connect cluster.
```
usage: list [-h]

Command is used to list the connectors on the connect cluster.

named arguments:
  -h, --help             show this help message and exit
```

### connector-plugins

Command is used to list all of the connector plugins that are registered on the Kafka Connect Worker.
```
usage: connector-plugins [-h]

Command is used to list all  of  the  connector plugins that are registered
on the Kafka Connect Worker.

named arguments:
  -h, --help             show this help message and exit
```

### delete

Command is used to delete a connector from the Kafka Connect Cluster.
```
usage: delete [-h] --connector CONNECTOR [CONNECTOR ...]

Command is used to delete a connector from the Kafka Connect Cluster.

named arguments:
  -h, --help             show this help message and exit
  --connector CONNECTOR [CONNECTOR ...]
                         The name of the connector
```

### download-example

Command is used to download configuration required to create a new connector.
```
usage: download-example [-h] [--output-format {Json,Properties}]
                        --output-file OUTPUTFILE --class CLASSNAME
                        [--include-defaults]

Command is  used  to  download  configuration  required  to  create  a  new
connector.

named arguments:
  -h, --help             show this help message and exit
  --output-format {Json,Properties}
                         The output format  used  to write configuration(s)
                         to the file system.
  --output-file OUTPUTFILE
                         The path on the  file  system  to write the output
                         to.
  --class CLASSNAME      Class name of the  connector  to download settings
                         for.
  --include-defaults     Flag to determine if  the  config  items that have
                         default  values  should  be  included  with  their
                         defaults.
```

### status

Command is used to return the status of a connector and it's tasks.
```
usage: status [-h] --connector CONNECTOR [CONNECTOR ...]

Command is used to return the status of a connector and it's tasks.

named arguments:
  -h, --help             show this help message and exit
  --connector CONNECTOR [CONNECTOR ...]
                         The name of the connector
```

### pause

Command is used to pause a connector on the Kafka Connect Cluster.
```
usage: pause [-h] --connector CONNECTOR [CONNECTOR ...]

Command is used to pause a connector on the Kafka Connect Cluster.

named arguments:
  -h, --help             show this help message and exit
  --connector CONNECTOR [CONNECTOR ...]
                         The name of the connector
```

### resume

Command is used to resume a connector on the Kafka Connect Cluster.
```
usage: resume [-h] --connector CONNECTOR [CONNECTOR ...]

Command is used to resume a connector on the Kafka Connect Cluster.

named arguments:
  -h, --help             show this help message and exit
  --connector CONNECTOR [CONNECTOR ...]
                         The name of the connector
```
