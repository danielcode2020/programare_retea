package com.example.programareretea.service;

import com.example.programareretea.service.dto.PortHostDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Lab1Service {

    private final Logger log = LoggerFactory.getLogger(Lab1Service.class);


    public void request(PortHostDto dto) throws InterruptedException, IOException {
        String responseHTML = getResponse(dto);
        Semaphore semaphore = new Semaphore(2);
        ExecutorService exec = Executors.newFixedThreadPool(4);

        for (String image : getImages(responseHTML)) {
            semaphore.acquire();
            exec.execute(() -> {
                try {
                    log.info(image.replace("'",""));
                    log.info(getImageNameClean(image));
                    log.info(dto.host());
                    log.info(dto.port().toString());
                    if (!image.contains("http")){
                        log.info("http://" + dto.host() + "/"+image.replace("'",""));
                        ProcessBuilder pb = new ProcessBuilder("python3","/home/daniel/Desktop/An3Sem2/Programare in Retea/lab1/ProgramareRetea1/Laborator1PR/images/py/amain.py",
                                dto.port().toString(), dto.host(),"http://" + dto.host() + "/"+image.replace("'",""), getImageNameClean(image));
                        pb.start();
                    }else {
                        ProcessBuilder pb = new ProcessBuilder("python3", "/home/daniel/Desktop/An3Sem2/Programare in Retea/lab1/ProgramareRetea1/Laborator1PR/images/py/amain.py",
                                dto.port().toString(), dto.host(), image.replace("'", ""), getImageNameClean(image));
                        pb.start();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                semaphore.release();
                log.info("");
            });
        }
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    private List<String> getImages(String text) {
        String img;
        List<String> images = new ArrayList<>();
        Pattern pImage = Pattern.compile("<img.*src\\s*=\\s*(.*?)(jpg|png|gif)[^>]*?>", Pattern.CASE_INSENSITIVE);
        Matcher mImage = pImage.matcher(text);

        while (mImage.find()) {
            img = mImage.group();
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                images.add(m.group(1));
            }
        }
        return images;
    }

    private String getResponse(PortHostDto dto) throws IOException {
        Socket socket = new Socket(dto.host(), dto.port());
        log.info("Socket created for host {} and port {}", dto.host(), dto.port());
        InputStream response = socket.getInputStream();
        OutputStream request = socket.getOutputStream();

        byte[] data = (buildRequest(dto.host())).getBytes();
        request.write(data);

        int c;
        String serverResponse = "";
        while ((c = response.read()) != -1) {
            serverResponse += (char) c;
        }
        socket.close();
        log.error("Server response");
        return serverResponse;
    }


    private String getImageNameClean(String text) {
        // scoatem tot pana la ultimul / si ' din string
        return text.replaceAll("^.*\\/(.*)","$1").replace("'","");
    }

    private String buildRequest(String host) {
        String response = "GET " + "/" + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "User-Agent: Mozilla/5.0 (X11; Linux i686; rv:2.0.1) Gecko/20100101 Firefox/4.0.1 \r\n" +
                "Accept-Language: ro \r\n" +
                "Content-Language: en, ase, ru \r\n" +
                "Vary: Accept-Encoding \r\n" +
                "\r\n";
        log.info("\n" +response);
        return response;
    }

}
