package Teste.teste;

import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        int opcao;

        do {
            System.out.println("Escolha uma opção:");
            System.out.println("1. Listar");
            System.out.println("2. Inserir");
            System.out.println("3. Excluir");
            System.out.println("4. Atualizar");
            System.out.println("5. Sair");
            opcao = scanner.nextInt();
            scanner.nextLine();  

            switch (opcao) {
                case 1: 
                    System.out.println("Lista de usuários:");
                    usuarioDAO.listar().forEach(System.out::println);
                    break;

                case 2: 
                    System.out.print("Digite o login: ");
                    String login = scanner.nextLine();
                    System.out.print("Digite a senha: ");
                    String senha = scanner.nextLine();
                    System.out.print("Digite o sexo (M/F): ");
                    String sexo = scanner.nextLine();
                    Usuario novoUsuario = new Usuario(login, senha, sexo);
                    if (usuarioDAO.inserir(novoUsuario)) {
                        System.out.println("Usuário inserido com sucesso!");
                    } else {
                        System.out.println("Erro ao inserir usuário.");
                    }
                    break;

                case 3:
                    System.out.print("Digite o código do usuário para excluir: ");
                    int codigoExcluir = scanner.nextInt();
                    if (usuarioDAO.excluir(codigoExcluir)) {
                        System.out.println("Usuário excluído com sucesso!");
                    } else {
                        System.out.println("Erro ao excluir usuário.");
                    }
                    break;

                case 4: 
                    System.out.print("Digite o código do usuário para atualizar: ");
                    int codigoAtualizar = scanner.nextInt();
                    scanner.nextLine();  // Limpar o buffer
                    System.out.print("Digite o novo login: ");
                    String novoLogin = scanner.nextLine();
                    System.out.print("Digite a nova senha: ");
                    String novaSenha = scanner.nextLine();
                    System.out.print("Digite o novo sexo (M/F): ");
                    String novoSexo = scanner.nextLine();
                    Usuario usuarioAtualizado = new Usuario(novoLogin, novaSenha, novoSexo);
                    usuarioAtualizado.setCodigo(codigoAtualizar);
                    if (usuarioDAO.atualizar(usuarioAtualizado)) {
                        System.out.println("Usuário atualizado com sucesso!");
                    } else {
                        System.out.println("Erro ao atualizar usuário.");
                    }
                    break;

                case 5: 
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }

        } while (opcao != 5);

        scanner.close();
    }
}
