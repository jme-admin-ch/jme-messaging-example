# Generate the Root CA Private Key
openssl genpkey -algorithm RSA -out rootCA.key -aes256 -pass pass:strongpassword -pkeyopt rsa_keygen_bits:4096

# Create the Root CA Certificate
openssl req -x509 -new -key rootCA.key -sha256 -days 7300 -out rootCA.crt -subj "/C=CH/ST=Bern/L=Bern/O=TestRootCA/OU=IT/CN=RootCA" -passin pass:strongpassword

###################
# Create the Intermediate CA

# Generate the Intermediate CA Private Key
openssl genpkey -algorithm RSA -out intermediateCA.key -aes256 -pass pass:strongpassword -pkeyopt rsa_keygen_bits:4096

# Create the Intermediate CA Certificate Signing Request (CSR)
openssl req -new -key intermediateCA.key -out intermediateCA.csr -subj "/C=CH/ST=Bern/L=Bern/O=TestIntermediateCA/OU=BIT/CN=IntermediateCA" -passin pass:strongpassword

# Sign the Intermediate CA Certificate with the Root CA
openssl x509 -req -in intermediateCA.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out intermediateCA.crt -days 5000 -sha256 -passin pass:strongpassword -extfile <(echo "basicConstraints=critical,CA:TRUE")


###################
# Create Leaf (End-Entity) Certificate for jme-messaging-receiverpublisher-outbox-service

# Generate the Leaf Private Key
openssl genpkey -algorithm RSA -out jme-messaging-receiverpublisher-outbox-service.key -pkeyopt rsa_keygen_bits:2048

# Create the Leaf Certificate Signing Request (CSR)
openssl req -new -key jme-messaging-receiverpublisher-outbox-service.key -out jme-messaging-receiverpublisher-outbox-service.csr -subj "/C=CH/ST=Bern/L=Bern/O=BIT/OU=jeap/CN=jme-messaging-receiverpublisher-outbox-service"

# Sign the Leaf Certificate with the Intermediate CA
openssl x509 -req -in jme-messaging-receiverpublisher-outbox-service.csr -CA intermediateCA.crt -CAkey intermediateCA.key -CAcreateserial -out jme-messaging-receiverpublisher-outbox-service.crt -days 3000 -sha256 -passin pass:strongpassword


####################
AWS how to set the secrets
####################
aws secretsmanager get-secret-value --secret-id jme-messaging-receiverpublisher-outbox-service --query SecretString --output text > current_secret.json


NEW_VALUE_KEY=$(cat jme-messaging-receiverpublisher-outbox-service.key)
jq --arg newValue "$NEW_VALUE_KEY" '.["jeap.messaging.authentication.publisher.signatureKey"] = $newValue' current_secret.json > updated_secret.json


aws secretsmanager update-secret --secret-id jme-messaging-receiverpublisher-outbox-service --secret-string file://updated_secret.json

