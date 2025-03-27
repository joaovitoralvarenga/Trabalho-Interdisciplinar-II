package com.app;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(4567); // Define a porta do servidor Spark

        get("/", (req, res) -> "Servidor Spark está rodando!");

        System.out.println("Servidor iniciado em: http://localhost:4567");
    }
}
