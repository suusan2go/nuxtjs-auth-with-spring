package main

import (
	"flag"
	"net/http"
	"os"
	"path"
	"strings"

	"github.com/golang/glog"
	"github.com/grpc-ecosystem/grpc-gateway/runtime"
	"github.com/suusan2go/nuxtjs-auth-with-spring/gateway/greeter"
	"golang.org/x/net/context"
	"google.golang.org/grpc"
)

var (
	echoEndpoint = flag.String("echo_endpoint", "localhost:9090", "endpoint of YourService")

	swaggerDir = flag.String("swagger_dir", "./swagger", "path to the directory which contains swagger definitions")
)

func run() error {
	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)
	defer cancel()

	hmux := http.NewServeMux()
	gw := runtime.NewServeMux()
	opts := []grpc.DialOption{grpc.WithInsecure()}
	err := greeter.RegisterGreeterHandlerFromEndpoint(ctx, gw, *echoEndpoint, opts)
	if err != nil {
		return err
	}

	hmux.HandleFunc("/swagger/", serveSwagger)
	hmux.Handle("/", gw)

	return http.ListenAndServe(":8080", hmux)
}

func serveSwagger(w http.ResponseWriter, r *http.Request) {
	if !strings.HasSuffix(r.URL.Path, ".swagger.json") {
		glog.Errorf("Not Found: %s", r.URL.Path)
		http.NotFound(w, r)
		return
	}

	glog.Infof("Serving %s", r.URL.Path)
	p := strings.TrimPrefix(r.URL.Path, "/swagger/")
	p = path.Join(*swaggerDir, p)
	if !isExist(p) {
		glog.Errorf("Not Found: %s", p)
	}
	http.ServeFile(w, r, p)
}

func isExist(filename string) bool {
	_, err := os.Stat(filename)
	return err == nil
}

func main() {
	flag.Parse()
	defer glog.Flush()

	if err := run(); err != nil {
		glog.Fatal(err)
	}
}
