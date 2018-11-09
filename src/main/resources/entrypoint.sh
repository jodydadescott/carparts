#!/bin/bash

export PORT=80
cd $(dirname $0)

function main() {
   err "Starting Carparts"
   cmd="java -server -jar carparts.jar"
   err "Running cmd->$cmd"
   exec $cmd
}

function err() { printf "%s\n" "$*" >&2; }

main $@
