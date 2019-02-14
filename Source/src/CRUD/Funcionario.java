/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRUD;

/**
 *
 * @author joao.v.silva
 */
public class Funcionario {

    private String cpf;
    private String nome;
    private Cargo cargo;
    private double salario;

    public Funcionario(String cpf, String nome, double salario, int idCargo, String descCargo) {
        this.cpf = cpf;
        this.nome = nome;
        this.cargo = new Cargo(idCargo, descCargo);
        this.salario = salario;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public double getSalario() {
        return salario;
    }
    
    public int getIdCargo(){
        return cargo.getId();
    }
    
    public String getDescCargo(){
        return cargo.getDescricao();
    }    

}
