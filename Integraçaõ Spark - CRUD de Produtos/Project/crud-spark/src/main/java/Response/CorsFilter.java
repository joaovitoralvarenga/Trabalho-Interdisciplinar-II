package Response;

import spark.Response;

public class CorsFilter {

    public static Response apply(Response res) {
        // Permitir origem específica (ajuste conforme a URL do seu front-end)
        res.header("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
        
        // Permitir os métodos HTTP que o seu servidor aceita
        res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        
        // Permitir os cabeçalhos que o servidor pode receber
        res.header("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Accept, Origin, Authorization");
        
        return res;
    }
}
