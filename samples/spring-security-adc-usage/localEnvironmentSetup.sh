#!/usr/bin/env bash
echo "Hint: run script with 'source localEnvironmentSetup.sh'"
echo "This script prepares the current shell's environment variables (not permanently)"

export VCAP_APPLICATION='{}'
export OPA_ADDR=https://adc-service-d048418.cfapps.eu10.hana.ondemand.com

echo \$OPA_ADDR=${OPA_ADDR}


