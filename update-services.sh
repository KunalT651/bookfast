#!/bin/bash
# Script to update all service files to use environment.apiUrl
# This is a helper script - you'll need to manually update each service

echo "This script helps identify files that need to be updated."
echo "Searching for hardcoded localhost:8080 references..."
echo ""

find frontEnd/src -type f -name "*.ts" -exec grep -l "localhost:8080" {} \; | while read file; do
    echo "Need to update: $file"
    echo "  Replace 'http://localhost:8080' with 'environment.apiUrl'"
    echo ""
done

echo "Done! Please update each file manually."

