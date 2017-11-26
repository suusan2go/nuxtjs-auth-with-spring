cp -rf ../gateway/swagger ./bin/
docker run --rm -v ${PWD}:/local swaggerapi/swagger-codegen-cli generate \
    -i /local/bin/swagger/greeter.swagger.json \
    -l typescript-fetch \
    -o /local/api/greeter \
    -t /local/bin/typescript-fetch
