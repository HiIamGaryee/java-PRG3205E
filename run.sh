#!/bin/bash

# Check if a filename was provided
if [ -z "$1" ]; then
    echo "Usage: ./run.sh <FileName.java>"
    exit 1
fi

FILE=$1
CLASS_NAME="${FILE%.*}"

echo "Compiling $FILE..."
javac "$FILE"

if [ $? -eq 0 ]; then
    echo "Running $CLASS_NAME..."
    java "$CLASS_NAME"
else
    echo "Compilation failed."
fi
