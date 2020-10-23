# Step 1: Post a module descriptor for the local module to the Okapi
curl -w '\n' -X POST -D -   \
   -H "Content-type: application/json"   \
   -d @../target/ModuleDescriptor.json \
   http://localhost:9130/_/proxy/modules
