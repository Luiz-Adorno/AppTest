package miips.com.Models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Post {
    private String cnpj_owner;
    private String cod_barras;
    private String data_cadastro;
    private String descri;

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

    private String nome_produto;
    private String quantidade;
    private boolean state;
    private String url_product;

    public Post(String cnpj_owner, String cod_barras, String data_cadastro, String descri, String nome_produto, String quantidade, boolean state, String url_product, String valor) {
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

    private String valor;
    public Post() {
    }

}