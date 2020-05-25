package miips.com.Models.Products;

import java.util.List;

public class Products {
    private String docId;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
    private String product_category;
    private String cnpj_owner;
    private String cod_barras;
    private String data_cadastro;
    private String descri;
    private String nome_produto;
    private String quantidade;
    private boolean state;
    private String url_product;
    private String valor;
    private List<String> size;

    public List<String> getSize() {
        return size;
    }

    public void setSize(List<String> size) {
        this.size = size;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    @Override
    public String toString() {
        return "Products{" +
                "cnpj_owner='" + cnpj_owner + '\'' +
                ", cod_barras='" + cod_barras + '\'' +
                ", data_cadastro='" + data_cadastro + '\'' +
                ", descri='" + descri + '\'' +
                ", nome_produto='" + nome_produto + '\'' +
                ", quantidade='" + quantidade + '\'' +
                ", state=" + state +
                ", url_product='" + url_product + '\'' +
                ", valor='" + valor + '\'' +
                '}';
    }

    public String getCnpj_owner() {
        return cnpj_owner;
    }

    public void setCnpj_owner(String cnpj_owner) {
        this.cnpj_owner = cnpj_owner;
    }

    public String getCod_barras() {
        return cod_barras;
    }

    public void setCod_barras(String cod_barras) {
        this.cod_barras = cod_barras;
    }

    public String getData_cadastro() {
        return data_cadastro;
    }

    public void setData_cadastro(String data_cadastro) {
        this.data_cadastro = data_cadastro;
    }

    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public String getNome_produto() {
        return nome_produto;
    }

    public void setNome_produto(String nome_produto) {
        this.nome_produto = nome_produto;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getUrl_product() {
        return url_product;
    }

    public void setUrl_product(String url_product) {
        this.url_product = url_product;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Products(){

    }

    public Products(String cnpj_owner, String cod_barras, String data_cadastro, String descri, String nome_produto, String quantidade, boolean state, String url_product, String valor) {
        this.cnpj_owner = cnpj_owner;
        this.cod_barras = cod_barras;
        this.data_cadastro = data_cadastro;
        this.descri = descri;
        this.nome_produto = nome_produto;
        this.quantidade = quantidade;
        this.state = state;
        this.url_product = url_product;
        this.valor = valor;
    }
}
