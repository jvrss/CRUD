/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRUD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author joao.v.silva
 */
public class ConexaoPostGres {

    Connection con;

    private final String driver = "org.postgresql.Driver";
    private final String user = "postgres";
    private final String senha = "master";
    private final String url = "jdbc:postgresql://localhost:5432/crud";
    private long offset = -1;
    private String modo;

    public ConexaoPostGres() {

        try {
            Class.forName(driver);

            con = (Connection) DriverManager.getConnection(url, user, senha);
            System.out.println("Conexão realizada com sucesso.");
            modo = "";

            con.setAutoCommit(false);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void defineModo(String modo, String campo) {

        campo = campo.toLowerCase();

        switch (modo) {

            case "ANDCPF":
                this.modo = "AND f.cpf LIKE '" + campo + "'";
                break;
            case "ANDNOME":
                this.modo = "AND LOWER(f.nome) LIKE LOWER('" + campo + "')";
                break;
            case "ANDCARGO":
                this.modo = "AND c.id = " + campo;
                break;
            default:
                this.modo = "";

        }

    }

    public String getModo() {
        return modo;
    }

    public long getOffset() {
        return offset;
    }

    public void resetOffSet() {
        offset = -1;
    }

    public Funcionario buscarFuncionario(int direcao) {

        Funcionario func = null;
        int quantLinhas = getTotalLinhasNavegacao();

        if (quantLinhas == 0) {
            return null;
        }

        String cpf;
        String nome;
        int idCargo;
        double salario;
        String descricao;

        if (direcao == 1) {
            offset++;
        } else {
            offset--;
        }

        if (offset > quantLinhas - 1) {
            offset = 0;
        } else if (offset < 0) {
            offset = quantLinhas - 1;
        }

        try {
            ResultSet rsFuncionario = con.createStatement().executeQuery("SELECT * FROM funcionario f, cargo c WHERE f.cargo = c.id " + modo + " LIMIT 1 OFFSET " + offset);

            if (!rsFuncionario.next()) {
                return null;
            }

            cpf = rsFuncionario.getString("cpf");
            nome = rsFuncionario.getString("nome");
            idCargo = rsFuncionario.getInt("id");
            salario = rsFuncionario.getDouble("salario");
            descricao = rsFuncionario.getString("descricao");

            func = new Funcionario(cpf, nome, salario, idCargo, descricao);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return func;

    }

    public void fecharConexao() {
        try {
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void excluirRegistro(String cpf) {
        Statement stm;
        try {
            stm = con.createStatement();
            stm.execute("DELETE FROM funcionario f WHERE f.cpf = '" + cpf + "'");
            con.commit();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        buscarFuncionario(-1);

    }

    public int getTotalLinhas() {

        int quantidade = 0;

        try {

            ResultSet rs = con.createStatement().executeQuery("SELECT count(*) FROM funcionario f, cargo c WHERE f.cargo = c.id ");

            if (!rs.next()) {
                return 0;
            }

            quantidade = rs.getInt("count");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return quantidade;

    }

    public int getTotalLinhasNavegacao() {

        int quantidade = 0;

        try {

            ResultSet rs = con.createStatement().executeQuery("SELECT count(*) FROM funcionario f, cargo c WHERE f.cargo = c.id " + modo);

            if (!rs.next()) {
                return 0;
            }

            quantidade = rs.getInt("count");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return quantidade;

    }

    public boolean inserirRegistro(Funcionario f) {

        // String cpf, String nome, int cargo, double salario
        Statement stm;
        try {
            stm = con.createStatement();
            stm.execute("INSERT INTO funcionario VALUES ('" + f.getCpf() + "', '" + f.getNome() + "', " + f.getIdCargo() + ", " + f.getSalario() + ")");
            con.commit();
        } catch (SQLException ex) {

            if (ex.getErrorCode() == 0) {

                JOptionPane.showMessageDialog(null, "Já existe um registro com esse CPF");

            }

            try {
                con.rollback();
            } catch (SQLException ex1) {
                System.exit(0);
            }

            return false;
        }

        return true;
    }

    public String recuperarCargo(int idCargo) {

        String descCargo = null;

        try {

            ResultSet rs = con.createStatement().executeQuery("SELECT c.descricao FROM cargo c WHERE c.id = " + idCargo);

            if (!rs.next()) {
                return null;
            }

            descCargo = rs.getString("descricao");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return descCargo;
    }

    public Funcionario carregarUltimoFuncionario() {

        Funcionario func = null;

        String cpf;
        String nome;
        int idCargo;
        double salario;
        String descricao;

        try {

            int quantLinhas = getTotalLinhas();
            quantLinhas--;
            offset = quantLinhas;

            ResultSet rsFuncionario = con.createStatement().executeQuery("SELECT * FROM funcionario f, cargo c WHERE f.cargo = c.id " + modo + " LIMIT 1 OFFSET " + offset);

            if (!rsFuncionario.next()) {
                return null;
            }

            cpf = rsFuncionario.getString("cpf");
            nome = rsFuncionario.getString("nome");
            idCargo = rsFuncionario.getInt("id");
            salario = rsFuncionario.getDouble("salario");
            descricao = rsFuncionario.getString("descricao");


            func = new Funcionario(cpf, nome, salario, idCargo, descricao);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return func;

    }

    public boolean updateFuncionario(String campos, String cpf) {

        String sql = "UPDATE funcionario SET " + campos + " WHERE cpf = '" + cpf + "'";

        try {
            con.createStatement().execute(sql);
            con.commit();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            try {
                con.rollback();
            } catch (SQLException ex1) {
                System.exit(0);
            }
            return false;
        }

        return true;

    }

}
