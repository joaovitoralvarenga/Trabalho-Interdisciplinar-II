package com.exemplo;

import static spark.Spark.*;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

public class Aplicacao {
    static final String AZURE_ENDPOINT = "https://analisefacial.cognitiveservices.azure.com/face/v1.0/detect";
    static final String AZURE_KEY = "CaEqMVeaLrM5OdBH86VsPanmbn6OO5KiR3fJoo2ovGYIjmyercTXJQQJ99BEAC1i4TkXJ3w3AAAKACOGo9Hk"; 

    public static void main(String[] args) {
        port(4567);
        

        staticFiles.externalLocation(Paths.get("public").toAbsolutePath().toString());
        
       
        enableCORS("*", "*", "*");
        
       
        get("/status", (req, res) -> {
            System.out.println("Recebida solicitação de status");
            res.type("application/json");
            return "{\"status\": \"online\"}";
        });
        
        post("/upload", (req, res) -> {
            System.out.println("Recebida solicitação de upload");
            Path tempFile = null;
            try {
                req.attribute("org.eclipse.jetty.multipartConfig", 
                           new MultipartConfigElement(System.getProperty("java.io.tmpdir")));
                           
                Part filePart = req.raw().getPart("imagem");
                if (filePart == null || filePart.getSize() == 0) {
                    System.out.println("Erro: Arquivo não enviado");
                    res.status(400);
                    return "{\"erro\": \"Arquivo não enviado\"}";
                }
                
                System.out.println("Tamanho do arquivo: " + filePart.getSize());
                System.out.println("Tipo do arquivo: " + filePart.getContentType());
                
                tempFile = Files.createTempFile("upload-", ".jpg");
                try (InputStream is = filePart.getInputStream()) {
                    Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }
                
                System.out.println("Arquivo salvo em: " + tempFile.toString());
                
                String analise = analisaFace(tempFile);
                res.type("application/json");
                return analise;
                
            } catch (Exception e) {
                System.err.println("Erro ao processar upload: " + e.getMessage());
                e.printStackTrace();
                res.status(500);
                return "{\"erro\": \"Erro no servidor: " + e.getMessage().replace("\"", "'") + "\"}";
            } finally {
                // Limpa o arquivo temporário após o uso
                if (tempFile != null && Files.exists(tempFile)) {
                    try {
                        Files.delete(tempFile);
                        System.out.println("Arquivo temporário excluído");
                    } catch (IOException e) {
                        System.err.println("Erro ao excluir arquivo temporário: " + e.getMessage());
                    }
                }
            }
        });
        
        System.out.println("Servidor iniciado em http://localhost:4567");
    }
    
    static String analisaFace(Path imagem) throws IOException, InterruptedException {
        System.out.println("Analisando imagem...");
        
 
        System.out.println("Tamanho da imagem: " + Files.size(imagem) + " bytes");
        
        // Simplificando a solicitação para apenas detecção básica
        String uri = AZURE_ENDPOINT;
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .header("Ocp-Apim-Subscription-Key", AZURE_KEY)
            .header("Content-Type", "application/octet-stream")
            .POST(HttpRequest.BodyPublishers.ofFile(imagem))
            .build();
        
        System.out.println("Enviando requisição para Azure: " + uri);    
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Resposta recebida. Status: " + response.statusCode());
        
        if (response.statusCode() != 200) {
            System.err.println("Erro da API Azure: " + response.body());
            throw new IOException("Erro da API Azure: " + response.statusCode() + " - " + response.body());
        }
        
     
        String responseBody = response.body();
        System.out.println("Corpo da resposta: " + responseBody.substring(0, Math.min(responseBody.length(), 200)) + "...");
        
        if (responseBody.equals("[]")) {
            return "{\"mensagem\": \"Nenhum rosto detectado na imagem\"}";
        }
        
        return responseBody;
    }
    

    private static void enableCORS(String origin, String methods, String headers) {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
            response.header("Access-Control-Allow-Credentials", "true");
        });
    }
}