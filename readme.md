# RPC

## interface
```java
public interface HelloService {
    String sayHello(String name);
}
```

## interface impl
```java
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name + "!";
    }
}
```

## client 
```java
public class Main{
    public static void main(String[] args) {
        RpcClient client = new RpcClient("127.0.0.1", 22221);
        HelloService helloService = client.get(HelloService.class);
        String result = helloService.sayHello("Bob");
        client.close();
    }
}
```

### server
```java
public class Main{
    public static void main(String[] args) throws InterruptedException {
        RpcServer server = new RpcServer(22221);
        server.start();
        server.register(HelloService.class, new HelloServiceImpl());
    }
}
```