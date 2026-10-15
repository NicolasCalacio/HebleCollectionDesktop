package com.example.heblecollectiondesktop.model;


public class Suporte extends Funcionario {
    public Suporte(int id, String matricula, String senha) {
    super(id, matricula, senha, Cargo.SUPORTE);
}

    public Suporte(int id, String matricula, String senha, Cargo cargo) {
        super(id, matricula, senha, cargo);
    }
}
