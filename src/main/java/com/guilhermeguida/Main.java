package com.guilhermeguida;

import com.guilhermeguida.model.Cliente;
import com.guilhermeguida.model.Pedido;
import com.guilhermeguida.model.PedidoEnum;
import com.guilhermeguida.model.Produto;
import com.guilhermeguida.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        Scanner sc = new Scanner(System.in);

        try {
            List<Produto> produtos = session.createQuery("FROM Produto", Produto.class).getResultList();
            List<Cliente> clientes = session.createQuery("FROM Cliente", Cliente.class).getResultList();
            List<Pedido> pedidos = session.createQuery("FROM Pedido", Pedido.class).getResultList();

            int opc = 0;
            do {
                try {
                    System.out.println("1-Produtos\n2-Pedidos\n3-Clientes\n9-Sair");
                    opc = sc.nextInt();
                    sc.nextLine();
                    switch (opc) {
                        case 1 -> menuGerenciamentoProdutos(session, sc, produtos);
                        case 2 -> menuGerenciamentoPedidos(session, sc, produtos, clientes, pedidos);
                        case 3 -> menuGerenciamentoClientes(session, sc, clientes);
                        case 9 -> System.out.println("Encerrando...");
                        default -> System.out.println("Opção inválida. Tente novamente.");
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao processar entrada: " + e.getMessage());
                    sc.nextLine();
                }
            } while (opc != 9);

        } catch (Exception e) {
            System.err.println("Erro geral: " + e.getMessage());
        } finally {
            session.close();
            HibernateUtil.shutdown();
        }
    }

    private static void menuGerenciamentoProdutos(Session session, Scanner sc, List<Produto> produtos) {
        System.out.println("Gerenciamento de produtos:");
        System.out.println("1 - Cadastrar\n2 - Listar\n3 - Remover");
        try {
            int opc = sc.nextInt();
            sc.nextLine();
            switch (opc) {
                case 1 -> {
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Preço: ");
                    double preco = sc.nextDouble();
                    System.out.print("Quantidade: ");
                    int quantidade = sc.nextInt();
                    sc.nextLine(); // Consome quebra de linha
                    System.out.print("Categoria: ");
                    String categoria = sc.nextLine();

                    Produto p = new Produto(nome, preco, quantidade, categoria);
                    Transaction transaction = session.beginTransaction();
                    session.saveOrUpdate(p);
                    transaction.commit();
                    produtos.add(p);
                    System.out.println("Produto cadastrado com sucesso!");
                }
                case 2 -> produtos.forEach(System.out::println);
                case 3 -> {
                    System.out.print("Id do produto para remover: ");
                    int id = sc.nextInt();
                    Produto produto = produtos.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
                    if (produto != null) {
                        Transaction transaction = session.beginTransaction();
                        session.delete(produto);
                        transaction.commit();
                        produtos.remove(produto);
                        System.out.println("Produto removido com sucesso!");
                    } else {
                        System.out.println("Produto não encontrado.");
                    }
                }
                default -> System.out.println("Opção inválida.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao processar gerenciamento de produtos: " + e.getMessage());
        }
    }

    private static void menuGerenciamentoPedidos(Session session, Scanner sc, List<Produto> produtos, List<Cliente> clientes, List<Pedido> pedidos) {
        System.out.println("Gerenciamento de pedidos:");
        System.out.println("1 - Criar\n2 - Listar\n3 - Remover");
        try {
            int opc = sc.nextInt();
            sc.nextLine();
            switch (opc) {
                case 1 -> {
                    System.out.print("Digite o id do cliente: ");
                    int idCliente = sc.nextInt();
                    Cliente auxCliente = clientes.stream().filter(c -> c.getId() == idCliente).findFirst().orElse(null);

                    if (auxCliente == null) {
                        System.out.println("Cliente não encontrado.");
                        return;
                    }

                    Date data = new Date();
                    System.out.print("Quantos produtos deseja adicionar: ");
                    int qnt = sc.nextInt();

                    List<Produto> auxProdutos = new ArrayList<>();
                    for (int i = 0; i < qnt; i++) {
                        System.out.print("Id do produto: ");
                        int idProduto = sc.nextInt();
                        Produto produto = produtos.stream().filter(p -> p.getId() == idProduto).findFirst().orElse(null);

                        if (produto != null) {
                            auxProdutos.add(produto);
                        } else {
                            System.out.println("Produto não encontrado. Ignorando...");
                            auxProdutos.clear();
                            i = -1;
                        }
                    }

                    System.out.print("Status: ");
                    PedidoEnum status = PedidoEnum.valueOf(sc.next().toUpperCase());

                    Pedido pedido = new Pedido(auxCliente, data, status);
                    pedido.setProdutos(auxProdutos);

                    Transaction transaction = session.beginTransaction();
                    session.save(pedido);
                    transaction.commit();
                    pedidos.add(pedido);
                    System.out.println("Pedido criado com sucesso!");
                }
                case 2 -> pedidos.forEach(System.out::println);
                case 3 -> {
                    System.out.print("Id do pedido para remover: ");
                    int idPedido = sc.nextInt();
                    Pedido pedido = pedidos.stream().filter(p -> p.getId() == idPedido).findFirst().orElse(null);

                    if (pedido != null) {
                        Transaction transaction = session.beginTransaction();
                        session.delete(pedido);
                        transaction.commit();
                        pedidos.remove(pedido);
                        System.out.println("Pedido removido com sucesso!");
                    } else {
                        System.out.println("Pedido não encontrado.");
                    }
                }
                default -> System.out.println("Opção inválida.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao processar gerenciamento de pedidos: " + e.getMessage());
        }
    }

    private static void menuGerenciamentoClientes(Session session, Scanner sc, List<Cliente> clientes) {
        System.out.println("Gerenciamento de clientes:");
        System.out.println("1 - Cadastrar\n2 - Listar\n3 - Remover");
        try {
            int opc = sc.nextInt();
            sc.nextLine();
            switch (opc) {
                case 1 -> {
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Endereço: ");
                    String end = sc.nextLine();
                    System.out.print("Telefone: ");
                    String telefone = sc.nextLine();

                    Cliente cliente = new Cliente(nome, end, telefone);
                    Transaction transaction = session.beginTransaction();
                    session.save(cliente);
                    transaction.commit();
                    clientes.add(cliente);
                    System.out.println("Cliente cadastrado com sucesso!");
                }
                case 2 -> clientes.forEach(System.out::println);
                case 3 -> {
                    System.out.print("Id do cliente para remover: ");
                    int id = sc.nextInt();
                    Cliente cliente = clientes.stream().filter(c -> c.getId() == id).findFirst().orElse(null);

                    if (cliente != null) {
                        Transaction transaction = session.beginTransaction();
                        session.delete(cliente);
                        transaction.commit();
                        clientes.remove(cliente);
                        System.out.println("Cliente removido com sucesso!");
                    } else {
                        System.out.println("Cliente não encontrado.");
                    }
                }
                default -> System.out.println("Opção inválida.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao processar gerenciamento de clientes: " + e.getMessage());
        }
    }
}
