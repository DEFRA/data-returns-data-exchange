#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SERVICE_HOME=${SCRIPT_DIR}
JAVA_HOME=/usr/lib/jvm/java-8-oracle
PATH=${JAVA_HOME}/bin:$PATH
SERVICE_JAR="$SERVICE_HOME/data-returns.jar"

PIDFILE=${SERVICE_HOME}/application.pid
SPRING_ARGS_FILE=${SERVICE_HOME}/application.args
SPRING_ARGS=""
if [ -f ${SPRING_ARGS_FILE} ]; then
	SPRING_ARGS=$(<"$SPRING_ARGS_FILE")
fi
JAVA_ARGS_FILE=${SERVICE_HOME}/java.args
JAVA_ARGS=""
if [ -f ${JAVA_ARGS_FILE} ]; then
	JAVA_ARGS=$(<"$JAVA_ARGS_FILE")
fi



cd ${SERVICE_HOME}


function check_pidfile {
	if [ -f ${PIDFILE} ]
	then
		return 0
	else
		return 1
	fi
}

function read_pid {
	echo $(<"$PIDFILE")
}

function check_running {
	if check_pidfile
	then
		# Get hold of the current pid
		if ps -p $(read_pid) > /dev/null
		then
			return 0
		else
			return 1
		fi
	else
		return 1
	fi
}

function start_service {
	if [ -f ${SERVICE_JAR} ]; then
		echo "Starting $SERVICE_JAR"
		/usr/bin/java ${JAVA_ARGS} -jar ${SERVICE_JAR} ${SPRING_ARGS}  > /dev/null 2>&1 &
		echo "Service started successfully using PID $!"
	else	
		echo "ERROR: Unable to find service JAR file at $SERVICE_JAR"
	fi
}

function restart_service {
	if check_running; then
		echo "Service already running on process $(read_pid), restarting"
		stop_service
	fi
	start_service
}

function stop_service {
	if ! check_running
	then
		echo "Service not currently running"
		return 1
	fi

	PID=$(read_pid)

    kill -SIGTERM ${PID}
    echo -ne "Waiting for process $PID to stop"
    NOT_KILLED=1
    for i in {1..20}; do
      if check_running
      then
        echo -ne "."
        sleep 1
      else
        NOT_KILLED=0
      fi
    done
    echo
    if [ ${NOT_KILLED} = 1 ]
    then
      echo "Cannot gracefully shutdown process $PID, terminating forcefully."
      kill -SIGKILL ${PID}
    fi
}

case "$1" in
	stop)
		stop_service
		;;
	start)
		restart_service
		;;
	*)
		restart_service
		;;
esac
