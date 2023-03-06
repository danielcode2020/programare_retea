package com.example.programareretea;

import java.net.Socket;
import java.io.*;
import java.net.URL;

public class PlaySocket {
    public static void main(String[] args) throws IOException {
        // /slide/large/3.jpg
        // /img/eng.png
        String host = "me.utm.md";
        Integer port = 80;
        String filename = "/img/4.jpg";
        Socket socket = new Socket(host, 80);
        DataOutputStream bw = new DataOutputStream(socket.getOutputStream());

        bw.writeBytes("GET " + filename + " HTTP/1.1\r\n");
        bw.writeBytes("Host: " + host  + ":80\r\n");
        bw.writeBytes("Content-Type: text/html;charset=utf-8 \r\n");
        bw.writeBytes("User-Agent: Mozilla/5.0 (X11; Linux i686; rv:2.0.1) Gecko/20100101 Firefox/4.0.1 \r\n");
        bw.writeBytes("Accept-Language: ro \r\n");
        bw.writeBytes("Content-Language: en, ase, ru \r\n");
        bw.writeBytes("Vary: Accept-Encoding \r\n");
        bw.writeBytes("\r\n");

        bw.flush();

        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        OutputStream fileOutputStream = new FileOutputStream("pivamatilaiu.jpg");
        boolean headerEnded = false;
        byte[] bytes = new byte[2048];
        int length;
        while ((length = inputStream.read(bytes)) != -1) {
            if (headerEnded)
                fileOutputStream.write(bytes, 0, length);
            else {
                for (int i = 0; i < 2048; i++) {
                    if (bytes[i] == 13 && bytes[i + 1] == 10 && bytes[i + 2] == 13 && bytes[i + 3] == 10) {
                        headerEnded = true;
                        fileOutputStream.write(bytes, i + 4, 2048 - i - 4);
                        break;
                    }
                }
            }
        }
        inputStream.close();
        fileOutputStream.close();
        System.out.println("image transfer done");

        socket.close();
    }

}