package com.example.hello;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("newGUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setTitle("Mission Controller");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                HelloController.threadExecutor.shutdown();
                try{
                    if (!HelloController.threadExecutor.awaitTermination(10, TimeUnit.SECONDS)){
                        HelloController.threadExecutor.shutdownNow();
                    }
                }
                catch(InterruptedException e){
                    HelloController.threadExecutor.shutdownNow();
                }
                
                try(ZContext ctx = new ZContext()){
                    ZMQ.Socket socket = ctx.createSocket(SocketType.REQ);
                    //ZMQ.Socket socket2 = ctx.createSocket(SocketType.REQ);
                    socket.connect("tcp://127.0.0.1:5555");
                    //socket2.connect("tcp://127.0.0.1:5554");
                    socket.send("END");
                    //socket2.send("END"); 
                    socket.recv();
                    //socket2.recv();
                    ctx.destroy();
                }
                System.exit(0);
            }
        });
    }
    public static void main(String[] args) throws IOException {
        /* 
        ProcessBuilder pb = new ProcessBuilder(
        "cmd", "/c", "path/to/exe.exe", 
        "/removeDrive", "driveLocation");
        pb.start();
        */
        launch();
    }
}
