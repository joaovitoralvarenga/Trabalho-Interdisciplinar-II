package Api;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import Modelo.Produto;
import Dao.ProdutoDAO;
import Response.CorsFilter;
import Response.StandardResponse;
import Response.StandardResponse.StatusResponse;

import spark.Request;
import spark.Response;

public class ProdutoService {
    private ProdutoDAO produtoDAO;
    private Gson gson;
    
    public ProdutoService() {
        produtoDAO = new ProdutoDAO();
        gson = new Gson();
    }
    
    public String get(Request request, Response response) {
        response = CorsFilter.apply(response);
        response.type("application/json");
        String data = "";
        Produto produto;
        int id = Integer.parseInt(request.params(":id"));
        
        produto = produtoDAO.getProduto(id);
        if (produto != null) {
            StatusResponse status = StatusResponse.SUCCESS;
            JsonElement dataProduto = gson.toJsonTree(produto);
            StandardResponse resposta = new StandardResponse(status, dataProduto);
            data = gson.toJson(resposta);
            response.status(200); // sucesso
        } else {
            StatusResponse status = StatusResponse.ERROR;
            String message = "Produto com ID (" + id + ") não foi encontrado.";
            StandardResponse resposta = new StandardResponse(status, message);
            data = gson.toJson(resposta);
            response.status(404); // 404 Não encontrado
        }
        return data; 
    }
    
    public String insert(Request request, Response response) {
        response = CorsFilter.apply(response);
        String requestBody = request.body();
        JsonObject requestJson = gson.fromJson(requestBody, JsonObject.class);
        String data = "";
        
        String nome = requestJson.get("nome").getAsString();
        double preco = requestJson.get("preco").getAsDouble();
        int quantidade = requestJson.get("quantidade").getAsInt();
        
        
        Produto produto = new Produto(nome, preco, quantidade);
        
        if (produtoDAO.inserirProduto(produto)) {
            StatusResponse status = StatusResponse.SUCCESS;
            JsonElement dataProduto = gson.toJsonTree(produto);
            StandardResponse resposta = new StandardResponse(status, "O produto '" + nome + "' foi inserido com sucesso!", dataProduto);
            data = gson.toJson(resposta);
            response.status(201); // 201 Created
        } else {
            StatusResponse status = StatusResponse.ERROR;
            StandardResponse resposta = new StandardResponse(status, "Não foi possível inserir o produto " + nome + ".");
            data = gson.toJson(resposta);
            response.status(500); // 500 Internal Server Error
        }
        response.body(data);
        return data;
    }
    
    public String update(Request request, Response response) {
        response = CorsFilter.apply(response);
        String requestBody = request.body();
        JsonObject requestJson = gson.fromJson(requestBody, JsonObject.class);
        String data = "";
        
        int id = requestJson.get("id").getAsInt();
        String nome = requestJson.get("nome").getAsString();
        double preco = requestJson.get("preco").getAsDouble();
        int quantidade = requestJson.get("quantidade").getAsInt();
        
        Produto produto = new Produto(id, nome, preco, quantidade);
        
        if (produtoDAO.atualizarProduto(produto)) {
            StatusResponse status = StatusResponse.SUCCESS;
            JsonElement dataProduto = gson.toJsonTree(produto);
            StandardResponse resposta = new StandardResponse(status, "O produto '" + nome + "' foi alterado com sucesso!", dataProduto);
            data = gson.toJson(resposta);
            response.status(200); // 200 OK
        } else {
            StatusResponse status = StatusResponse.ERROR;
            StandardResponse resposta = new StandardResponse(status, "Não foi possível atualizar o produto " + nome + ".");
            data = gson.toJson(resposta);
            response.status(404); // 404 Não encontrado
        }
        response.body(data);
        return data;
    }
    
    public String getAll(Request request, Response response) {
        response = CorsFilter.apply(response);
        response.type("application/json");
        String data = "";
        List<Produto> produtos = produtoDAO.getProdutos("");
        
        if (produtos != null && !produtos.isEmpty()) {
            StatusResponse status = StatusResponse.SUCCESS;
            JsonElement dataProdutos = gson.toJsonTree(produtos);
            StandardResponse resposta = new StandardResponse(status, "Todos os produtos foram lidos com sucesso!", dataProdutos);
            data = gson.toJson(resposta);
            response.status(200); // sucesso
        } else {
            StatusResponse status = StatusResponse.ERROR;
            String message = "Não foi possível ler todos os dados.";
            StandardResponse resposta = new StandardResponse(status, message);
            data = gson.toJson(resposta);
            response.status(500); // 500 Internal Server Error
        }
        return data;
    }
    
    public String delete(Request request, Response response) {
        response = CorsFilter.apply(response);
        String data = "";
        int id = Integer.parseInt(request.params(":id"));
        
        if (produtoDAO.excluirProduto(id)) {
            StatusResponse status = StatusResponse.SUCCESS;
            StandardResponse resposta = new StandardResponse(status, "O produto de id = " + id + " foi excluído com sucesso!");
            data = gson.toJson(resposta);
            response.status(200); // 200 OK
        } else {
            StatusResponse status = StatusResponse.ERROR;
            StandardResponse resposta = new StandardResponse(status, "O produto de id = " + id + " não foi encontrado!");
            data = gson.toJson(resposta);
            response.status(404); // 404 Não encontrado
        }
        return data;
    }
}
