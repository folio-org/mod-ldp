curl -w '\n' -X POST -D -   \
   -H "Content-type: application/json"   \
   -d @./local-deployment-descriptor.json \
   http://localhost:9130/_/discovery/modules
