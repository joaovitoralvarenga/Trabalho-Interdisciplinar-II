package com.app;

import static spark.Spark.*;

import com.google.gson.Gson;

public class ProdutoApi {
    private static ProdutoDao produtoDao = new ProdutoDao();
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        port(4567);

        // Rota para listar todos os produtos
        get("/produtos", (req, res) -> {
            res.type("application/json");
            return gson.toJson(produtoDao.listarProdutos());
        });

        // Rota para criar um produto
        post("/produtos", (req, res) -> {
            Produto produto = gson.fromJson(req.body(), Produto.class);
            produtoDao.salvarProduto(produto);
            res.status(201);  // Created
            return gson.toJson(produto);
        });

        // Rota para buscar produto por ID
        get("/produtos/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Produto produto = produtoDao.buscarProdutoPorId(id);
            if (produto != null) {
                res.type("application/json");
                return gson.toJson(produto);
            } else {
                res.status(404); // Not Found
                return "Produto não encontrado!";
            }
        });

        // Rota para atualizar um produto
        put("/produtos/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Produto produto = gson.fromJson(req.body(), Produto.class);
            produto.setId(id);
            produtoDao.atualizarProduto(produto);
            return gson.toJson(produto);
        });

        // Rota para deletar um produto
        delete("/produtos/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            produtoDao.deletarProduto(id);
            res.status(204); // No Content
            return "Produto deletado!";
        });
    }
}
