package app;

import static spark.Spark.*;

import Response.CorsFilter;
import Api.ProdutoService;

public class Aplicacao {
    public static ProdutoService produtoService = new ProdutoService();
    public static CorsFilter corsFilter = new CorsFilter();

    public static void main(String[] args) {
        port(4567);

        // End-point para receber a requisição de OPTIONS (pre-flight)
        options("/*", (request, response) -> CorsFilter.apply(response));

        // End-point para inserir um produto
        post("/produto/insert", (request, response) -> produtoService.insert(request, response));

        // End-point para buscar um produto pelo ID
        get("/produto/:id", (request, response) -> produtoService.get(request, response));

        // End-point para buscar todos os produtos
        get("/produto", (request, response) -> produtoService.getAll(request, response));

        // End-point para atualizar um produto pelo ID
        put("/produto/update/:id", (request, response) -> produtoService.update(request, response));

        // End-point para deletar um produto pelo ID
        delete("/produto/delete/:id", (request, response) -> produtoService.delete(request, response));
    }
}
