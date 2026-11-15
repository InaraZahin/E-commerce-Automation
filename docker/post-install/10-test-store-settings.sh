#!/bin/sh
# Runs once, right after PrestaShop installs. Makes the store deterministic for automation.
set -e

# The container has no mail server. With mail enabled, flows that send email (such as password
# recovery for a registered customer) fail with "An error occurred while sending the email".
# PS_MAIL_METHOD 3 = "Never send emails".
mysql -h "$DB_SERVER" -u "$DB_USER" -p"$DB_PASSWD" "$DB_NAME" \
  -e "UPDATE ${DB_PREFIX:-ps_}configuration SET value = '3' WHERE name = 'PS_MAIL_METHOD';"

echo "Test store settings applied."
