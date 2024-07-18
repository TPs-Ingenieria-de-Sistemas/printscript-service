# Redis Usage

### Docker

On docker, by just starting the aplication Redis will start correctly and accept connections at port 8083.

### Local

However, if you want to run Redis locally, you should first install Redis:

```brew update```

```brew install redis```

Then, you can start Redis Server with the following command:

```redis-server```

Then, start the Spring Application. Remember that the redis server should be running while the Spring App is running. 
