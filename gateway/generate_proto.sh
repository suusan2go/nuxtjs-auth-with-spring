#!/bin/sh
protoc -I/usr/local/include -I. \
  -I$GOPATH/src \
  -I$GOPATH/src/github.com/grpc-ecosystem/grpc-gateway/third_party/googleapis \
  --grpc-gateway_out=logtostderr=true:./greeter \
  --go_out=plugins=grpc:./greeter \
  --swagger_out=logtostderr=true:./swagger \
  -I../protofiles ../protofiles/*
