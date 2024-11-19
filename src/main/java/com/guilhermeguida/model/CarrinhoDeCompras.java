package com.guilhermeguida.model;

public interface CarrinhoDeCompras {

    public void adcionarProduto(Produto produto);
    public void removerProduto(Produto produto);
    public void finalizarCompra();
}
